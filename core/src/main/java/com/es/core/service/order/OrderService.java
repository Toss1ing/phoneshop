package com.es.core.service.order;

import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.CartItem;
import com.es.core.model.order.Order;

public interface OrderService {
    Order createOrder(CartItem cart);

    void placeOrder(Order order) throws OutOfStockException;
}
