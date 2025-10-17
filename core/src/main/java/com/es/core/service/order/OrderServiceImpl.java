package com.es.core.service.order;

import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.CartItem;
import com.es.core.model.order.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Order createOrder(CartItem cart) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void placeOrder(Order order) throws OutOfStockException {
        throw new UnsupportedOperationException("TODO");
    }
}
