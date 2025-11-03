package com.es.core.dao.order;

import com.es.core.dao.orderItem.OrderItemRowMapper;
import com.es.core.dao.phone.PhoneRowMapper;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.util.TableColumnsNames;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OrderRowMapper implements ResultSetExtractor<Optional<Order>> {

    private final OrderItemRowMapper orderItemRowMapper;

    public OrderRowMapper() {
        this.orderItemRowMapper = new OrderItemRowMapper(new PhoneRowMapper());
    }

    @Override
    public Optional<Order> extractData(ResultSet rs) throws SQLException {
        Map<Long, Order> orderMap = new HashMap<>();

        while (rs.next()) {
            long orderId = rs.getLong(TableColumnsNames.Order.ORDER_ID);

            Order order = orderMap.get(orderId);
            if (order == null) {
                order = new Order();
                order.setId(orderId);
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
                order.setStatus(OrderStatus.valueOf(rs.getString(TableColumnsNames.Order.STATUS)));
                order.setOrderItems(new ArrayList<>());
                orderMap.put(orderId, order);
            }

            long orderItemId = rs.getLong(TableColumnsNames.OrderItem.ORDER_ITEM_ID);
            if (orderItemId > 0) {
                OrderItem orderItem = orderItemRowMapper.mapRow(rs, rs.getRow());
                order.getOrderItems().add(orderItem);
            }
        }

        return orderMap.values().stream().findFirst();
    }
}
