package com.es.core.util.sql;

public final class ColorSql {

    public static final String SELECT_COLORS_BY_PHONE_ID = """
                 SELECT c.id, c.code FROM colors c
                 JOIN phone2color pc ON c.id = pc.colorId
                 WHERE pc.phoneId = ?
            """;

    public static final String SELECT_COLORS_BY_PHONE_IDS = """
                SELECT pc.phoneId, c.id, c.code
                FROM phone2color pc
                JOIN colors c ON pc.colorId = c.id
                WHERE pc.phoneId IN (%s)
            """;

    public static final String DELETE_COLORS_BY_PHONE_ID = """
                DELETE FROM phone2color WHERE phoneId = ?
            """;

    public static final String SELECT_EXIST_COLORS = "SELECT colorId FROM phone2color WHERE phoneId = ?";

    public static final String SELECT_EXIST_COLOR = "SELECT id FROM colors WHERE code = ?";

    public static final String INSERT_COLOR = """
                INSERT INTO colors (code) VALUES (:code)
            """;

    public static final String DELETE_PHONE_COLORS_BY_IDS =
            "DELETE FROM phone2color WHERE phoneId = :phoneId AND colorId IN (:colorIds)";

    public static final String INSERT_INTO_PHONE2COLOR = "INSERT INTO phone2color (phoneId, colorId) VALUES (:phoneId, :colorId)";

}
