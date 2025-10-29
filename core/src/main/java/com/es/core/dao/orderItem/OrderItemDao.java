package com.es.core.dao.orderItem;

import com.es.core.model.order.OrderItem;

import java.util.List;

public interface OrderItemDao {

    int[] saveOrderItemsByOrderId(Long orderId, List<OrderItem> orderItems);

}
