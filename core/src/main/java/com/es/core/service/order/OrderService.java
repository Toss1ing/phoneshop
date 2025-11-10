package com.es.core.service.order;

import com.es.core.dao.pagination.Page;
import com.es.core.dto.order.UserPersonalInfoDto;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;

public interface OrderService {

    Order createOrder();

    void placeOrder(Order order, UserPersonalInfoDto userPersonalInfoDto);

    Order getOrderBySecureId(String secureId);

    boolean isOrderConsistency(Order draft);

    Page<Order> findAllOrders(int page, int size);

    Order getOrderById(Long orderId);

    void updateOrderStatus(Long orderId, OrderStatus orderStatus);

}
