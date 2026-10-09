package com.lucas.coin_wallet_api.Enum;

import lombok.Getter;

@Getter
public enum Moeda {
    BRL("Real Brasileiro"),
    USD("Dólar Americano"),
    EUR("Euro"),
    BTC("Bitcoin"),
    ETH("Ethereum");

    private final String descricao;

    Moeda(String descricao) {
        this.descricao = descricao;
    }
}