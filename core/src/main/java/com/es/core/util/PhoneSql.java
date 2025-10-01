package com.es.core.util;

public final class PhoneSql {

    private PhoneSql() {
    }

    public static final String SELECT_PHONE_BY_ID = "SELECT * FROM phones WHERE id = ?";

    public static final String SELECT_COLORS_BY_PHONE_ID = """
                SELECT c.id, c.code
                FROM colors c
                JOIN phone2color pc ON c.id = pc.colorId
                WHERE pc.phoneId = ?
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

    public static final String SELECT_PHONES_PAGINATED = "SELECT * FROM phones ORDER BY id LIMIT ? OFFSET ?";

    public static final String SELECT_COLORS_BY_PHONE_IDS = """
                SELECT pc.phoneId, c.id, c.code
                FROM phone2color pc
                JOIN colors c ON pc.colorId = c.id
                WHERE pc.phoneId IN (%s)
            """;

}
