package com.es.core.dao.stock;

import com.es.core.model.phone.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:context/applicationContext-coreTest.xml")
public class JdbcStockDaoIntegrationTest {

    private final JdbcStockDao jdbcStockDao;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcStockDaoIntegrationTest(JdbcStockDao jdbcStockDao, JdbcTemplate jdbcTemplate) {
        this.jdbcStockDao = jdbcStockDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    private Long phoneId1;
    private Long phoneId2;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                1L, "Apple", "iPhone 14", 1000);
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                2L, "Samsung", "Galaxy S23", 1200);

        phoneId1 = 1L;
        phoneId2 = 2L;

        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", phoneId1, 10, 2);
        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", phoneId2, 5, 1);
    }

    @Test
    void testGetStockByPhoneIdExistingPhone() {
        Optional<Stock> stockOpt = jdbcStockDao.getStockByPhoneId(phoneId1);

        assertTrue(stockOpt.isPresent(), "Stock should be present");
        Stock stock = stockOpt.get();
        assertEquals(phoneId1, stock.getPhone().getId());
        assertEquals(10, stock.getStock());
        assertEquals(2, stock.getReserved());
    }

    @Test
    void testGetStockByPhoneIdAnotherPhone() {
        Optional<Stock> stockOpt = jdbcStockDao.getStockByPhoneId(phoneId2);

        assertTrue(stockOpt.isPresent(), "Stock should be present");
        Stock stock = stockOpt.get();
        assertEquals(phoneId2, stock.getPhone().getId());
        assertEquals(5, stock.getStock());
        assertEquals(1, stock.getReserved());
    }

    @Test
    void testGetStockByPhoneIdNotExistingPhone() {
        Optional<Stock> stockOpt = jdbcStockDao.getStockByPhoneId(999L);
        assertTrue(stockOpt.isEmpty(), "Stock should be empty for non-existing phone");
    }

    @Test
    void testStockValuesCanBeUpdated() {
        jdbcTemplate.update("UPDATE stocks SET stock = ?, reserved = ? WHERE phoneId = ?", 20, 5, phoneId1);

        Optional<Stock> stockOpt = jdbcStockDao.getStockByPhoneId(phoneId1);
        assertTrue(stockOpt.isPresent());
        Stock stock = stockOpt.get();
        assertEquals(20, stock.getStock());
        assertEquals(5, stock.getReserved());
    }
}
