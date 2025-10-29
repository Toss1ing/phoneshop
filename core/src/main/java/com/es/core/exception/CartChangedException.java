package com.es.core.exception;

public class CartChangedException extends RuntimeException {
    public CartChangedException(String message) {
        super(message);
    }
}
