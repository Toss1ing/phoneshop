package com.es.core.service.stock;

import com.es.core.model.phone.Stock;

import java.util.Map;

public interface StockService {
    void reservePhone(Long phoneId, int quantity);
    void decreaseReservedQuantity(Long phoneId, int quantity);
    void reserveAndValidateItems(Map<Long, Integer> items);
}
