
package com.lucas.coin_wallet_api.service;

import com.lucas.coin_wallet_api.controller.dto.UsuarioRequest;
import com.lucas.coin_wallet_api.controller.dto.UsuarioResponse;
import com.lucas.coin_wallet_api.exception.ConflictException;
import com.lucas.coin_wallet_api.exception.ResourceNotFoundException;
import com.lucas.coin_wallet_api.mapper.UsuarioMapper;
import com.lucas.coin_wallet_api.model.Usuario;
import com.lucas.coin_wallet_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequest request;
    private Usuario usuario;
    private UsuarioResponse response;

    @BeforeEach
    void setUp() {
        request = mock(UsuarioRequest.class);
        usuario = mock(Usuario.class);
        response = mock(UsuarioResponse.class);
    }

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        when(request.email()).thenReturn("teste@email.com");
        when(usuarioRepository.existsByEmail("teste@email.com"))
                .thenReturn(false);
        when(usuarioMapper.toEntity(request)).thenReturn(usuario);
        when(usuario.getSenha()).thenReturn("senha123");
        when(passwordEncoder.encode("senha123")).thenReturn("senhaHash");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse resultado = usuarioService.cadastrar(request);

        assertSame(response, resultado);

        verify(usuario).setSenha("senhaHash");
        verify(usuarioRepository).save(usuario);
        verify(usuarioMapper).toResponse(usuario);
    }

    @Test
    void deveLancarExcecaoAoCadastrarEmailExistente() {
        when(request.email()).thenReturn("teste@email.com");
        when(usuarioRepository.existsByEmail("teste@email.com"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> usuarioService.cadastrar(request)
        );

        verify(usuarioRepository, never()).save(any());
        verify(usuarioMapper, never()).toEntity(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse resultado =
                usuarioService.buscarPorEmail("teste@email.com");

        assertSame(response, resultado);

        verify(usuarioRepository).findByEmail("teste@email.com");
        verify(usuarioMapper).toResponse(usuario);
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        when(usuarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.buscarPorEmail("inexistente@email.com")
        );

        verify(usuarioMapper, never()).toResponse(any());
    }

    @Test
    void deveExcluirUsuarioExistente() {
        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse resultado =
                usuarioService.deletarPorEmail("teste@email.com");

        assertSame(response, resultado);

        verify(usuarioRepository).delete(usuario);
        verify(usuarioMapper).toResponse(usuario);
    }

    @Test
    void naoDeveExcluirUsuarioInexistente() {
        when(usuarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.deletarPorEmail("inexistente@email.com")
        );

        verify(usuarioRepository, never()).delete(any());
        verify(usuarioMapper, never()).toResponse(any());
    }
}
