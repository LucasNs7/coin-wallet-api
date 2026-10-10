package com.lucas.coin_wallet_api.controller;

import com.lucas.coin_wallet_api.controller.dto.UsuarioRequest;
import com.lucas.coin_wallet_api.controller.dto.UsuarioResponse;
import com.lucas.coin_wallet_api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.cadastrar(request));
    }

    @GetMapping("/{email}")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<UsuarioResponse> excluir(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.deletarPorEmail(email));
    }
}