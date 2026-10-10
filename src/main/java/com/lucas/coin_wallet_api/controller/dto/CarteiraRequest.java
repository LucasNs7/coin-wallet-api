package com.lucas.coin_wallet_api.controller.dto;

import com.lucas.coin_wallet_api.model.enums.Moeda;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CarteiraRequest(
        @NotBlank(message = "O e-mail do usuário é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String usuarioEmail,

        @NotNull(message = "A moeda é obrigatória")
        Moeda moeda,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser um valor positivo")
        BigDecimal quantidade
) {}