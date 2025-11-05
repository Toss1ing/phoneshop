package com.es.core.util.sql;

public class OrderSql {

    public static final String SELECT_ORDERS = """
                SELECT
                    id AS orderId,
                    secureId,
                    subtotal,
                    deliveryPrice,
                    totalPrice,
                    firstName,
                    lastName,
                    deliveryAddress,
                    contactPhoneNo,
                    status,
                    additionalInformation
                FROM orders
                ORDER BY id ASC
                LIMIT :limit OFFSET :offset
            """;

    public static final String COUNT_ORDERS = """
                SELECT COUNT(DISTINCT o.id) FROM orders o
            """;

    public static final String INSERT_ORDER = """
            INSERT INTO orders (
                    secureId, subtotal,
                    deliveryPrice,
                    totalPrice,
                    firstName,
                    lastName,
                    deliveryAddress,
                    contactPhoneNO,
                    additionalInformation,
                    status
                )
                VALUES (
                    :secureId,
                    :subtotal,
                    :deliveryPrice,
                    :totalPrice,
                    :firstName,
                    :lastName,
                    :deliveryAddress,
                    :contactPhoneNo,
                    :additionalInformation,
                    :statusName
                )
            """;

    public static final String SELECT_ORDER_BY_SECURE_ID = """
                SELECT
                    o.id AS orderId,
                    o.secureId,
                    o.subtotal,
                    o.deliveryPrice,
                    o.totalPrice,
                    o.firstName,
                    o.lastName,
                    o.deliveryAddress,
                    o.contactPhoneNo,
                    o.status,
                    o.additionalInformation,
                    oi.id AS orderItemId,
                    oi.quantity,
                    p.id,
                    p.brand,
                    p.model,
                    p.price,
                    p.displaySizeInches,
                    p.weightGr,
                    p.lengthMm,
                    p.heightMm,
                    p.widthMm,
                    p.announced,
                    p.deviceType,
                    p.os,
                    p.displayResolution,
                    p.pixelDensity,
                    p.displayTechnology,
                    p.backCameraMegapixels,
                    p.frontCameraMegapixels,
                    p.ramGb,
                    p.internalStorageGb,
                    p.batteryCapacityMah,
                    p.talkTimeHours,
                    p.standByTimeHours,
                    p.bluetooth,
                    p.positioning,
                    p.imageUrl,
                    p.description
                FROM orders o
                LEFT JOIN orderItems oi ON o.id = oi.orderId
                LEFT JOIN phones p ON oi.phoneId = p.id
                WHERE o.secureId = :secureId
            """;

    public static final String SELECT_ORDER_BY_ID = """
                SELECT
                    o.id AS orderId,
                    o.secureId,
                    o.subtotal,
                    o.deliveryPrice,
                    o.totalPrice,
                    o.firstName,
                    o.lastName,
                    o.deliveryAddress,
                    o.contactPhoneNo,
                    o.status,
                    o.additionalInformation,
                    oi.id AS orderItemId,
                    oi.quantity,
                    p.id,
                    p.brand,
                    p.model,
                    p.price,
                    p.displaySizeInches,
                    p.weightGr,
                    p.lengthMm,
                    p.heightMm,
                    p.widthMm,
                    p.announced,
                    p.deviceType,
                    p.os,
                    p.displayResolution,
                    p.pixelDensity,
                    p.displayTechnology,
                    p.backCameraMegapixels,
                    p.frontCameraMegapixels,
                    p.ramGb,
                    p.internalStorageGb,
                    p.batteryCapacityMah,
                    p.talkTimeHours,
                    p.standByTimeHours,
                    p.bluetooth,
                    p.positioning,
                    p.imageUrl,
                    p.description
                FROM orders o
                LEFT JOIN orderItems oi ON o.id = oi.orderId
                LEFT JOIN phones p ON oi.phoneId = p.id
                WHERE o.id = :id
            """;

    public static final String UPDATE_ORDER_STATUS_BY_ID = """
                UPDATE orders
                SET status = :status
                WHERE id = :id
            """;
}
