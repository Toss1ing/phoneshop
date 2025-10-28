package com.es.core.dao.order;

import com.es.core.dao.orderItem.OrderItemRowMapper;
import com.es.core.dao.phone.PhoneRowMapper;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
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
            long orderId = rs.getLong("orderId");

            Order order = orderMap.get(orderId);
            if (order == null) {
                order = new Order();
                order.setId(orderId);
                order.setSecureId(rs.getString("secureId"));
                order.setSubtotal(rs.getBigDecimal("subtotal"));
                order.setDeliveryPrice(rs.getBigDecimal("deliveryPrice"));
                order.setTotalPrice(rs.getBigDecimal("totalPrice"));
                order.setFirstName(rs.getString("firstName"));
                order.setLastName(rs.getString("lastName"));
                order.setDeliveryAddress(rs.getString("deliveryAddress"));
                order.setContactPhoneNo(rs.getString("contactPhoneNo"));
                String info = rs.getString("additionalInformation");
                order.setAdditionalInformation(info != null ? info : "");
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                order.setOrderItems(new ArrayList<>());
                orderMap.put(orderId, order);
            }

            long orderItemId = rs.getLong("orderItemId");
            if (orderItemId > 0) {
                OrderItem orderItem = orderItemRowMapper.mapRow(rs, rs.getRow());
                order.getOrderItems().add(orderItem);
            }
        }

        return orderMap.values().stream().findFirst();
    }
}
