package com.es.core.dao.stock;

import java.util.Map;

public interface StockDao {

    boolean updateReservedByPhoneId(Long phoneId, int reserve);

    boolean decreaseReservedByPhoneId(Long phoneId, int quantity);

    int[] updateReservedItems(Map<Long, Integer> items);
}
