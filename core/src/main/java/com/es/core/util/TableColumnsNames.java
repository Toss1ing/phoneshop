package com.es.core.util;

public final class TableColumnsNames {

    public static final String ID = "id";

    private TableColumnsNames() {
    }

    public static final class Phone {
        private Phone() {
        }

        public static final String BRAND = "brand";
        public static final String MODEL = "model";
        public static final String PRICE = "price";
        public static final String DISPLAY_SIZE = "displaySizeInches";
        public static final String PHONE_ID = "phoneId";
    }

    public static final class Color {
        private Color() {
        }

        public static final String COLOR_ID = "colorId";
        public static final String CODE = "code";
    }

    public static final class Stock {
        private Stock() {
        }

        public static final String RESERVED_QUANTITY = "quantity";
    }

    public static final class Order {
        private Order() {
        }

        public static final String SECURE_ID = "secureId";
    }

    public static final class OrderItem {
        private OrderItem() {
        }

        public static final String ORDER_ID = "orderId";
        public static final String PHONE_ID = "phoneId";
        public static final String QUANTITY = "quantity";
    }
}

