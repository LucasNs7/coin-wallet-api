package com.lucas.coin_wallet_api.controller;

import com.lucas.coin_wallet_api.dto.CarteiraRequest;
import com.lucas.coin_wallet_api.dto.CarteiraResponse;
import com.lucas.coin_wallet_api.dto.PatrimonioTotalResponse;
import com.lucas.coin_wallet_api.service.CarteiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carteiras")
@RequiredArgsConstructor
public class CarteiraController {

    private final CarteiraService carteiraService;

    @PostMapping
    public ResponseEntity<CarteiraResponse> criarOuAtualizar(@Valid @RequestBody CarteiraRequest request) {
        return ResponseEntity.ok(carteiraService.criarOuAtualizar(request));
    }

    @GetMapping("/total-brl/{email}")
    public ResponseEntity<PatrimonioTotalResponse> calcularTotalEmBrl(@PathVariable String email) {
        return ResponseEntity.ok(carteiraService.calcularTotalEmBrl(email));
    }
}