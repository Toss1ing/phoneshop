package com.es.core.util.sql;

public final class StockSql {

    private StockSql() {
    }

    public static final String FIND_STOCK_STOCK_BY_PHONE_ID = "SELECT * FROM stocks WHERE phoneId = :phoneId";

}
