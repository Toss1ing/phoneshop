package com.es.core.util.sql;

public final class PhoneSql {

    private PhoneSql() {
    }

    public static final String SELECT_PHONE_BY_ID_WITH_COLORS = """
                SELECT p.*,
                       c.id AS colorId,
                       c.code AS colorCode
                FROM phones p
                LEFT JOIN phone2color pc ON p.id = pc.phoneId
                LEFT JOIN colors c ON pc.colorId = c.id
                WHERE p.id = :phoneId
            """;

    public static final String INSERT_PHONE = """
                INSERT INTO phones (
                    brand, model, price, displaySizeInches, weightGr,
                    lengthMm, widthMm, heightMm, announced, deviceType,
                    os, displayResolution, pixelDensity, displayTechnology,
                    backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb,
                    batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth,
                    positioning, imageUrl, description
                ) VALUES (
                    :brand, :model, :price, :displaySizeInches, :weightGr,
                    :lengthMm, :widthMm, :heightMm, :announced, :deviceType,
                    :os, :displayResolution, :pixelDensity, :displayTechnology,
                    :backCameraMegapixels, :frontCameraMegapixels, :ramGb, :internalStorageGb,
                    :batteryCapacityMah, :talkTimeHours, :standByTimeHours, :bluetooth,
                    :positioning, :imageUrl, :description
                )
            """;

    public static final String UPDATE_PHONE = """
                UPDATE phones SET
                    brand=:brand, model=:model, price=:price, displaySizeInches=:displaySizeInches, weightGr=:weightGr,
                    lengthMm=:lengthMm, widthMm=:widthMm, heightMm=:heightMm, announced=:announced, deviceType=:deviceType,
                    os=:os, displayResolution=:displayResolution, pixelDensity=:pixelDensity, displayTechnology=:displayTechnology,
                    backCameraMegapixels=:backCameraMegapixels, frontCameraMegapixels=:frontCameraMegapixels, ramGb=:ramGb, internalStorageGb=:internalStorageGb,
                    batteryCapacityMah=:batteryCapacityMah, talkTimeHours=:talkTimeHours, standByTimeHours=:standByTimeHours, bluetooth=:bluetooth,
                    positioning=:positioning, imageUrl=:imageUrl, description=:description
                WHERE id=:id
            """;

    public static final String SELECT_PHONES_WITH_AVAILABLE_STOCK_AND_PRICE = """
                SELECT p.* FROM phones p
                    JOIN stocks s ON p.id = s.phoneId
                    WHERE s.stock > 0 AND p.price IS NOT NULL
            """;

    public static final String COUNT_PHONES_WITH_AVAILABLE_STOCK_AND_PRICE = """
                SELECT COUNT(DISTINCT p.id) FROM phones p
                    JOIN stocks s ON p.id = s.phoneId
                    WHERE s.stock > 0 AND p.price IS NOT NULL
            """;

    public static final String SEARCH_BY_MODEL = " AND LOWER(p.model) LIKE :search";

    public static final String ORDER_BY_PRICE = " ORDER BY p.price";

    public static final String ORDER_BY_BRAND = " ORDER BY LOWER(p.brand)";

    public static final String ORDER_BY_MODEL = " ORDER BY LOWER(p.model)";

    public static final String ORDER_BY_DISPLAY_SIZE = " ORDER BY p.displaySizeInches";

    public static final String ORDER_BY_ID = " ORDER BY p.id";

    public static final String PAGINATION = " LIMIT :limit OFFSET :offset";
}
