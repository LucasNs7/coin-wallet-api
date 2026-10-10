package com.lucas.coin_wallet_api.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public record PatrimonioTotalResponse(
        String usuarioEmail,
        List<CarteiraResponse> carteiras,
        BigDecimal totalEmBrl
) {}
