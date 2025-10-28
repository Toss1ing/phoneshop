package com.es.core.util.sql;

public final class StockSql {

    private StockSql() {
    }

    public static final String FIND_STOCK_STOCK_BY_PHONE_ID = "SELECT * FROM stocks WHERE phoneId = :phoneId";

    public static final String UPDATE_RESERVED_WHERE_STOCK_AVAILABLE_BY_PHONE_ID = """
                UPDATE stocks
                SET reserved = reserved + :quantity
                WHERE phoneId = :phoneId
                  AND (
                        (:quantity >= 0 AND reserved + :quantity <= stock)
                     OR (:quantity < 0 AND reserved + :quantity >= 0)
                  )
            """;

    public static final String DECREASE_RESERVED_BY_PHONE_ID = """
                UPDATE stocks
                SET reserved = reserved - :quantity
                WHERE phoneId = :phoneId AND reserved >= :quantity
            """;

    public static final String DECREASE_STOCK_BY_PHONE_ID = """
                UPDATE stocks
                SET stock = stock - :quantity
                WHERE phoneId = :phoneId AND stock >= 0
            """;

    public static final String CLEAN_UP_RESERVED_CART_ITEMS = """
                UPDATE stocks
                SET reserved = reserved - :quantity
                WHERE phoneId = :phoneId
            """;

}
