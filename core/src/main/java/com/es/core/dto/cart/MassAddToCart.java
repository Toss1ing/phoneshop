package com.es.core.dto.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.LinkedHashMap;
import java.util.Map;

public class MassAddToCart {

    private Map<Integer, String> productModels;
    private Map<Integer,
            @Min(value = 1, message = "Quantity must be at least 1")
            @Max(value = Short.MAX_VALUE, message = "Very large quantity")
                    Integer> quantities;

    public MassAddToCart() {
        productModels = new LinkedHashMap<>();
        quantities = new LinkedHashMap<>();

        for (int i = 0; i < 8; i++) {
            productModels.put(i, "");
            quantities.put(i, null);
        }
    }

    public Map<Integer, String> getProductModels() {
        return productModels;
    }

    public void setProductModels(Map<Integer, String> productCodes) {
        this.productModels = productCodes;
    }

    public Map<Integer, Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(Map<Integer, Integer> quantities) {
        this.quantities = quantities;
    }
}
