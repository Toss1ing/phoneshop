package com.es.core.dto;

import com.es.core.model.cart.Cart;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CartView {
    private Cart cart;

    @NotNull
    private Map<Long,
            @Min(value = 1, message = "Quantity must be at least 1")
            @Max(value = Short.MAX_VALUE, message = "Very large quantity")
                    Integer> items = new HashMap<>();

    public CartView() {
    }

    public CartView(Cart cart) {
        this.cart = cart;
        cart.getItems().forEach(item -> items.put(item.getPhone().getId(), item.getQuantity()));
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Map<Long, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Long, Integer> items) {
        this.items = items;
    }

}
