package com.replayx.app.util;

/**
 * Resultado da operação de transferência do bypass.
 */
public final class TransferResult {
    public final int code;
    public final String message;
    public final boolean success;

    public TransferResult() {
        this.code = 1;
        this.message = "OK";
        this.success = true;
    }

    public TransferResult(int code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }
}
