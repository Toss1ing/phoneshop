package com.es.core.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddPhoneToCartRequest {

    @NotNull(message = "phone id must not be null")
    private Long phoneId;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be at least 1")
    @Max(value = Short.MAX_VALUE, message = "Very large quantity")
    private Integer quantity;

    public Long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Long phoneId) {
        this.phoneId = phoneId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
