package com.lucas.coin_wallet_api.controller;

import com.lucas.coin_wallet_api.controller.dto.UsuarioRequest;
import com.lucas.coin_wallet_api.controller.dto.UsuarioResponse;
import com.lucas.coin_wallet_api.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void deveCriarUsuarioComStatusCreated() {
        UsuarioRequest request = mock(UsuarioRequest.class);
        UsuarioResponse response = mock(UsuarioResponse.class);

        when(usuarioService.cadastrar(request)).thenReturn(response);

        ResponseEntity<UsuarioResponse> resultado =
                usuarioController.criar(request);

        assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
        assertSame(response, resultado.getBody());

        verify(usuarioService).cadastrar(request);
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        String email = "teste@email.com";
        UsuarioResponse response = mock(UsuarioResponse.class);

        when(usuarioService.buscarPorEmail(email)).thenReturn(response);

        ResponseEntity<UsuarioResponse> resultado =
                usuarioController.buscarPorEmail(email);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertSame(response, resultado.getBody());

        verify(usuarioService).buscarPorEmail(email);
    }

    @Test
    void deveExcluirUsuarioPorEmail() {
        String email = "teste@email.com";
        UsuarioResponse response = mock(UsuarioResponse.class);

        when(usuarioService.deletarPorEmail(email)).thenReturn(response);

        ResponseEntity<UsuarioResponse> resultado =
                usuarioController.excluir(email);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertSame(response, resultado.getBody());

        verify(usuarioService).deletarPorEmail(email);
    }
}