package com.es.core.dao.order;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;
import com.es.core.util.TableColumnsNames;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapperUtil {
    private OrderMapperUtil() {
    }

    public static Order mapBaseOrder(ResultSet rs) throws SQLException {
        Order order = new Order();

        Long idColumn = getNullableLong(rs, hasColumn(rs, TableColumnsNames.Order.ORDER_ID) ?
                TableColumnsNames.Order.ORDER_ID
                : TableColumnsNames.ID);
        order.setId(idColumn);
        order.setSecureId(rs.getString(TableColumnsNames.Order.SECURE_ID));
        order.setSubtotal(rs.getBigDecimal(TableColumnsNames.Order.SUBTOTAL));
        order.setDeliveryPrice(rs.getBigDecimal(TableColumnsNames.Order.DELIVERY_PRICE));
        order.setTotalPrice(rs.getBigDecimal(TableColumnsNames.Order.TOTAL_PRICE));
        order.setFirstName(rs.getString(TableColumnsNames.Order.FIRST_NAME));
        order.setLastName(rs.getString(TableColumnsNames.Order.LAST_NAME));
        order.setDeliveryAddress(rs.getString(TableColumnsNames.Order.DELIVERY_ADDRESS));
        order.setContactPhoneNo(rs.getString(TableColumnsNames.Order.CONTACT_PHONE_NO));

        String info = rs.getString(TableColumnsNames.Order.ADDITIONAL_INFORMATION);
        order.setAdditionalInformation(info != null ? info : "");

        String status = rs.getString(TableColumnsNames.Order.STATUS);
        if (status != null) {
            order.setStatus(OrderStatus.valueOf(status));
        }

        return order;
    }

    private static Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private static boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
