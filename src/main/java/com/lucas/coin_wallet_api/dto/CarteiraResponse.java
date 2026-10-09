package com.lucas.coin_wallet_api.dto;

import com.lucas.coin_wallet_api.model.enums.Moeda;

import java.math.BigDecimal;

public record CarteiraResponse(
        Moeda moeda,
        BigDecimal quantidade
) {}