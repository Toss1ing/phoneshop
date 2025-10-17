package com.es.core.model.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private long totalQuantity;
    private BigDecimal totalPrice;

    public Cart() {
        this.items = new ArrayList<>();
        this.totalQuantity = 0;
        this.totalPrice = new BigDecimal(0);
    }

    public Cart(List<CartItem> items) {
        this.items = items;
        this.totalQuantity = 0;
        this.totalPrice = new BigDecimal(0);
    }

    public Cart(List<CartItem> items, long totalQuantity, BigDecimal totalPrice) {
        this.items = items;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
