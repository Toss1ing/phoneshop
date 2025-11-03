package com.es.core.dao.orderItem;

import com.es.core.model.order.OrderItem;
import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.OrderItemSql;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class JdbcOrderItemDao implements OrderItemDao {

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int[] saveOrderItemsByOrderId(Long orderId, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return new int[0];
        }

        MapSqlParameterSource[] params = orderItems.stream()
                .map(orderItem -> new MapSqlParameterSource()
                        .addValue(TableColumnsNames.OrderItem.ORDER_ID, orderId)
                        .addValue(TableColumnsNames.Phone.PHONE_ID, orderItem.getPhone().getId())
                        .addValue(TableColumnsNames.OrderItem.QUANTITY, orderItem.getQuantity())
                )
                .toArray(MapSqlParameterSource[]::new);

        return namedParameterJdbcTemplate.batchUpdate(
                OrderItemSql.INSERT_ORDER_ITEM,
                params
        );
    }
}
