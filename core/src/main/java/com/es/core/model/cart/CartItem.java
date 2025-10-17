package com.es.core.model.cart;

import com.es.core.model.phone.Phone;

public class CartItem {
    private Phone phone;
    private Long quantity;

    public CartItem() {
    }

    public CartItem(Phone phone, Long quantity) {
        this.phone = phone;
        this.quantity = quantity;
    }

    public Phone getPhone() {
        return phone;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setPhones(Phone phone) {
        this.phone = phone;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

}
