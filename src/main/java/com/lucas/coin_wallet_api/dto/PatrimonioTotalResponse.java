package com.lucas.coin_wallet_api.dto;

import java.math.BigDecimal;
import java.util.List;

public record PatrimonioTotalResponse(
        String usuarioEmail,
        List<CarteiraResponse> carteiras,
        BigDecimal totalEmBrl
) {}
