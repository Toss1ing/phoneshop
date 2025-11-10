package com.es.core.dao.order;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;

import java.util.Optional;

public interface OrderDao {

    void saveOrder(Order order);

    Optional<Order> findOrderBySecureId(String secureId);

    Optional<Order> findOrderById(Long orderId);

    Page<Order> findAllOrders(Pageable pageable);

    int updateOrderStatus(Long orderId, OrderStatus orderStatus);
}
