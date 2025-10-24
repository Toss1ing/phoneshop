package com.es.core.service.stock;

import com.es.core.dao.stock.StockDao;
import com.es.core.exception.StockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockServiceImplTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockDao stockDao;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReservePhoneSuccess() {
        when(stockDao.updateReservedByPhoneId(1L, 5)).thenReturn(true);

        assertDoesNotThrow(() -> stockService.reservePhone(1L, 5));
        verify(stockDao).updateReservedByPhoneId(1L, 5);
    }

    @Test
    void testReservePhoneFailThrowsException() {
        when(stockDao.updateReservedByPhoneId(1L, 10)).thenReturn(false);

        StockException exception = assertThrows(StockException.class, () -> stockService.reservePhone(1L, 10));
        assertEquals("Out of stock", exception.getMessage());
    }

    @Test
    void testReleasePhoneSuccess() {
        when(stockDao.decreaseReservedByPhoneId(1L, 3)).thenReturn(true);

        assertDoesNotThrow(() -> stockService.decreaseReservedQuantity(1L, 3));
        verify(stockDao).decreaseReservedByPhoneId(1L, 3);
    }

    @Test
    void testReleasePhoneFailThrowsException() {
        when(stockDao.decreaseReservedByPhoneId(1L, 4)).thenReturn(false);

        StockException exception = assertThrows(StockException.class, () -> stockService.decreaseReservedQuantity(1L, 4));
        assertEquals("Failed to release reserved", exception.getMessage());
    }

    @Test
    void testReserveAndValidateItemsSuccess() {
        Map<Long, Integer> items = Map.of(1L, 2, 2L, 3);
        when(stockDao.updateReservedItems(items)).thenReturn(new int[]{1, 1});

        assertDoesNotThrow(() -> stockService.reserveAndValidateItems(items));
        verify(stockDao).updateReservedItems(items);
    }

    @Test
    void testReserveAndValidateItemsPartialFailure() {
        Map<Long, Integer> items = new LinkedHashMap<>();
        items.put(1L, 2);
        items.put(2L, 3);
        when(stockDao.updateReservedItems(items)).thenReturn(new int[]{1, 0});

        StockException exception = assertThrows(StockException.class, () -> stockService.reserveAndValidateItems(items));
        assertTrue(exception.getErrors().containsKey(2L));
        assertEquals("Out of stock", exception.getErrors().get(2L));
    }

    @Test
    void testReserveAndValidateItemsAllFail() {
        Map<Long, Integer> items = Map.of(1L, 2, 2L, 3);
        when(stockDao.updateReservedItems(items)).thenReturn(new int[]{0, 0});

        StockException exception = assertThrows(StockException.class, () -> stockService.reserveAndValidateItems(items));
        assertEquals(2, exception.getErrors().size());
    }
}
