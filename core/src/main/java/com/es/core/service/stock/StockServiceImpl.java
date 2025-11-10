package com.es.core.service.stock;

import com.es.core.dao.stock.StockDao;
import com.es.core.exception.StockException;
import com.es.core.util.ExceptionMessage;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

public class StockServiceImpl implements StockService {

    private final StockDao stockDao;

    public StockServiceImpl(StockDao stockDao) {
        this.stockDao = stockDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reservePhone(Long phoneId, int quantity) {
        boolean isRowUpdated = stockDao.updateReservedByPhoneId(phoneId, quantity);

        if (!isRowUpdated) {
            throw new StockException(ExceptionMessage.OUT_OF_STOCK_MESSAGE);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseReservedQuantity(Long phoneId, int quantity) {
        boolean isRowUpdated = stockDao.decreaseReservedByPhoneId(phoneId, quantity);

        if (!isRowUpdated) {
            throw new StockException(ExceptionMessage.RELEASE_FAILED_MESSAGE);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reserveAndValidateItems(Map<Long, Integer> items) {
        int[] updatedRows = stockDao.updateReservedItems(items);

        validateUpdateResult(items, updatedRows);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseStock(Map<Long, Integer> items) {
        int[] updatedRows = stockDao.decreaseStock(items);

        validateUpdateResult(items, updatedRows);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void cleanUpReserved(Map<Long, Integer> items) {
        stockDao.cleanUpReserved(items);
    }

    private void validateUpdateResult(Map<Long, Integer> items, int[] updatedRows) {
        Map<Long, String> errors = new HashMap<>();
        int i = 0;
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            if (updatedRows[i] == 0) {
                errors.put(entry.getKey(), ExceptionMessage.OUT_OF_STOCK_MESSAGE);
            }
            i++;
        }

        if (!errors.isEmpty()) {
            throw new StockException(errors);
        }
    }

}
