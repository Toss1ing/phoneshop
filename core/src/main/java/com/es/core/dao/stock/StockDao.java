package com.es.core.dao.stock;

import com.es.core.model.phone.Stock;

import java.util.Optional;

public interface StockDao {

    Optional<Stock> getStockByPhoneId(Long phoneId);

}
