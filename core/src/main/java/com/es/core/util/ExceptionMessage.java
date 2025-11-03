package com.es.core.util;

public final class ExceptionMessage {

    private ExceptionMessage() {
    }

    public static final String PHONE_NOT_FOUND_BY_ID_MESSAGE = "Phone with id - %d not found";
    public static final String PHONE_ID_IS_NULL = "Phone id is null";

    public static final String RELEASE_FAILED_MESSAGE = "Failed to release reserved";
    public static final String OUT_OF_STOCK_MESSAGE = "Out of stock";

    public static final String CART_ITEM_NOT_FOUND = "Cart item not found";
    public static final String CART_CHANGED_MESSAGE = "Cart has changed since you started checkout. Please review your order.";

    public static final String ORDER_EMPTY_MESSAGE = "Order must not be empty";
    public static final String ORDER_BY_SECURE_ID_NOT_FOUND = "Order by secureId '%s' not found";
    public static final String ORDER_BY_ID_NOT_FOUND = "Order by id '%s' not found";
    public static final String ORDER_ITEM_INSERT_EXCEPTION = "Error during insert order item in database";
    public static final String INVALID_ORDER_STATUS_MESSAGE = "Invalid order status";
    public static final String ORDER_ID_IS_NULL = "Order id is null";

}
