package com.es.core.dao.order;

import com.es.core.model.order.Order;

import java.util.Optional;

public interface OrderDao {

    void saveOrder(Order order);

    Optional<Order> findOrderBySecureId(String secureId);
}
