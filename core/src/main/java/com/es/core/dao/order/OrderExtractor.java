package com.es.core.dao.order;

import com.es.core.dao.orderItem.OrderItemRowMapper;
import com.es.core.dao.phone.PhoneRowMapper;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.util.TableColumnsNames;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OrderExtractor implements ResultSetExtractor<Optional<Order>> {

    private final OrderItemRowMapper orderItemRowMapper;

    public OrderExtractor() {
        this.orderItemRowMapper = new OrderItemRowMapper(new PhoneRowMapper());
    }

    @Override
    public Optional<Order> extractData(ResultSet rs) throws SQLException {
        Map<Long, Order> orderMap = new HashMap<>();

        while (rs.next()) {
            Long orderId = rs.getLong(TableColumnsNames.ID);
            Order order = orderMap.get(orderId);

            if (order == null) {
                order = OrderMapperUtil.mapBaseOrder(rs);
                order.setOrderItems(new ArrayList<>());
                orderMap.put(orderId, order);
            }

            long orderItemId = rs.getLong(TableColumnsNames.OrderItem.ORDER_ITEM_ID);
            if (!rs.wasNull() && orderItemId > 0) {
                OrderItem orderItem = orderItemRowMapper.mapRow(rs, rs.getRow());
                order.getOrderItems().add(orderItem);
            }
        }

        return orderMap.values().stream().findFirst();
    }

}
