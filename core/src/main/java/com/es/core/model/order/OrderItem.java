package com.es.core.model.order;

import com.es.core.model.phone.Phone;

import java.util.Objects;

public class OrderItem {
    private Long id;
    private Phone phone;
    private Order order;
    private Integer quantity;

    public OrderItem(Phone phone, Integer quantity) {
        this.phone = phone;
        this.quantity = quantity;
    }

    public OrderItem() {
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(final Phone phone) {
        this.phone = phone;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(final Order order) {
        this.order = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem that)) return false;

        Long thisPhoneId = this.phone != null ? this.phone.getId() : null;
        Long thatPhoneId = that.phone != null ? that.phone.getId() : null;

        return Objects.equals(thisPhoneId, thatPhoneId) &&
                Objects.equals(this.quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        Long phoneId = phone != null ? phone.getId() : null;
        return Objects.hash(phoneId, quantity);
    }
}
