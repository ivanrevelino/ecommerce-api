package com.ecommerce.ecommerce_api.exception;

public class ConcurrentStockException extends RuntimeException {
    public ConcurrentStockException(String message) {
        super(message);
    }
}
