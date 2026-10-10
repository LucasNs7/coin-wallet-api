package com.lucas.coin_wallet_api.service;

import com.lucas.coin_wallet_api.controller.dto.UsuarioRequest;
import com.lucas.coin_wallet_api.controller.dto.UsuarioResponse;
import com.lucas.coin_wallet_api.exception.ConflictException;
import com.lucas.coin_wallet_api.exception.ResourceNotFoundException;
import com.lucas.coin_wallet_api.mapper.UsuarioMapper;
import com.lucas.coin_wallet_api.model.Usuario;
import com.lucas.coin_wallet_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado com o email " + email)
        );
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(String email){
        return usuarioMapper.toResponse(buscaUsuarioPorEmail(email));
    }

    public UsuarioResponse cadastrar(UsuarioRequest request){
        if (usuarioRepository.existsByEmail(request.email())){
            throw new ConflictException("Email Existente!");
        }

        Usuario entity = usuarioMapper.toEntity(request);
        entity.setSenha(passwordEncoder.encode(entity.getSenha()));

        return usuarioMapper.toResponse(usuarioRepository.save(entity));
    }

    public UsuarioResponse deletarPorEmail(String email){
        Usuario entity = buscaUsuarioPorEmail(email);
        usuarioRepository.delete(entity);
        return usuarioMapper.toResponse(entity);
    }
}
