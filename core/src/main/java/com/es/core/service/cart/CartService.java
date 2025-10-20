package com.es.core.service.cart;

import com.es.core.model.cart.Cart;

import java.util.Map;

public interface CartService {

    Cart getCart();

    Cart addPhone(Long phoneId, Integer quantity);

    void update(Map<Long, Integer> items);

    void remove(Long phoneId);
}
