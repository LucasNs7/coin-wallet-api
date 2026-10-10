package com.lucas.coin_wallet_api.exception;

public class CotacaoIndisponivelException extends RuntimeException {
    public CotacaoIndisponivelException(String message) {
        super(message);
    }

    public CotacaoIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}
