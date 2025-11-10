package com.es.core.util.sql;

public final class ColorSql {

    public static final String SELECT_COLORS_BY_COLOR_CODES = """
                SELECT
                    id as colorId,
                    code as colorCode,
                    name
                FROM colors WHERE code IN (:colorsCode)
            """;

    public static final String SELECT_COLORS_BY_PHONE_IDS = """
                SELECT
                    pc.phoneId,
                    c.id as colorId,
                    c.code as colorCode
                FROM phone2color pc
                JOIN colors c ON pc.colorId = c.id
                WHERE pc.phoneId IN (:phoneIds)
            """;

    public static final String DELETE_COLORS_BY_PHONE_ID = """
                DELETE FROM phone2color WHERE phoneId = :phoneId
            """;

    public static final String INSERT_COLORS = """
                INSERT INTO colors (code) VALUES (:code)
            """;

    public static final String SELECT_COLORS_BY_PHONE_ID = """
                SELECT colorId FROM phone2color WHERE phoneId = :phoneId
            """;

    public static final String DELETE_PHONE_COLOR_RELATION = """
                DELETE FROM phone2color WHERE phoneId = :phoneId AND colorId = :colorId
            """;

    public static final String INSERT_PHONE_COLOR_RELATION = """
                INSERT INTO phone2color (colorId, phoneId) VALUES ( :colorId, :phoneId)
            """;

}
