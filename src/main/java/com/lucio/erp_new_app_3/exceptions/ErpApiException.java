package com.lucio.erp_new_app_3.exceptions;

public class ErpApiException extends RuntimeException {
    private final int statusCode;

    public ErpApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ErpApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
