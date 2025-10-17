package com.es.core.dto;

import com.es.core.model.color.Color;

import java.math.BigDecimal;
import java.util.Set;

public class PhoneDto {
    private Long id;

    private String imageUrl;

    private String brand;

    private String model;

    Set<Color> colors;

    private BigDecimal displaySizeInches;

    private BigDecimal price;

    public PhoneDto() {
    }

    public PhoneDto(
            Long id,
            String imageUrl,
            String brand,
            String model,
            Set<Color> colors,
            BigDecimal displaySizeInches,
            BigDecimal price
    ) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.model = model;
        this.colors = colors;
        this.displaySizeInches = displaySizeInches;
        this.price = price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColors(Set<Color> colors) {
        this.colors = colors;
    }

    public void setDisplaySizeInches(BigDecimal displaySizeInches) {
        this.displaySizeInches = displaySizeInches;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Set<Color> getColors() {
        return colors;
    }

    public BigDecimal getDisplaySizeInches() {
        return displaySizeInches;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
