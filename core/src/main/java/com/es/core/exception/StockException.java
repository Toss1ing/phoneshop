package com.es.core.exception;

import java.util.HashMap;
import java.util.Map;

public class StockException extends RuntimeException {
    private final Map<Long, String> errors;

    public StockException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }

    public StockException(Map<Long, String> errors) {
        this.errors = errors;
    }

    public Map<Long, String> getErrors() {
        return errors;
    }
}
