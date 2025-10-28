package com.es.core.util.sql;

public final class OrderItemSql {

    public static final String INSERT_ORDER_ITEM = """
                INSERT INTO orderItems (orderId, phoneId, quantity) VALUES (:orderId, :phoneId, :quantity)
            """;
}
