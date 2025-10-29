package com.es.core.util;

public final class TableColumnsNames {

    public static final String ID = "id";

    private TableColumnsNames() {
    }

    public static final class Phone {
        private Phone() {
        }

        public static final String PHONE_ID = "phoneId";
        public static final String BRAND = "brand";
        public static final String MODEL = "model";
        public static final String PRICE = "price";
        public static final String DISPLAY_SIZE = "displaySizeInches";

        public static final String WEIGHT_GR = "weightGr";
        public static final String LENGTH_MM = "lengthMm";
        public static final String HEIGHT_MM = "heightMm";
        public static final String WIDTH_MM = "widthMm";
        public static final String ANNOUNCED = "announced";
        public static final String DEVICE_TYPE = "deviceType";
        public static final String OS = "os";
        public static final String DISPLAY_RESOLUTION = "displayResolution";
        public static final String PIXEL_DENSITY = "pixelDensity";
        public static final String DISPLAY_TECHNOLOGY = "displayTechnology";
        public static final String BACK_CAMERA_MP = "backCameraMegapixels";
        public static final String FRONT_CAMERA_MP = "frontCameraMegapixels";
        public static final String RAM_GB = "ramGb";
        public static final String STORAGE_GB = "internalStorageGb";
        public static final String BATTERY_MAH = "batteryCapacityMah";
        public static final String TALK_TIME_HOURS = "talkTimeHours";
        public static final String STANDBY_TIME_HOURS = "standByTimeHours";
        public static final String BLUETOOTH = "bluetooth";
        public static final String POSITIONING = "positioning";
        public static final String IMAGE_URL = "imageUrl";
        public static final String DESCRIPTION = "description";
    }

    public static final class Color {
        private Color() {
        }

        public static final String COLOR_ID = "colorId";
        public static final String CODE = "code";
        public static final String COLOR_CODE = "colorCode";
    }

    public static final class Stock {
        private Stock() {
        }

        public static final String STOCK = "stock";
        public static final String RESERVED = "reserved";
        public static final String RESERVED_QUANTITY = "quantity";
    }

    public static final class Order {
        private Order() {
        }

        public static final String ORDER_ID = "orderId";
        public static final String SECURE_ID = "secureId";
        public static final String SUBTOTAL = "subtotal";
        public static final String DELIVERY_PRICE = "deliveryPrice";
        public static final String TOTAL_PRICE = "totalPrice";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String DELIVERY_ADDRESS = "deliveryAddress";
        public static final String CONTACT_PHONE_NO = "contactPhoneNo";
        public static final String ADDITIONAL_INFORMATION = "additionalInformation";
        public static final String STATUS = "status";

    }

    public static final class OrderItem {
        private OrderItem() {
        }

        public static final String ORDER_ITEM_ID = "orderItemId";
        public static final String ORDER_ID = "orderId";
        public static final String QUANTITY = "quantity";
    }
}

