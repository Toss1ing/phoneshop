package com.es.core.dao.stock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:context/applicationContext-coreTest.xml")
public class JdbcStockDaoIntegrationTest {

    private final JdbcStockDao jdbcStockDao;
    private final JdbcTemplate jdbcTemplate;

    private Long phoneId1;
    private Long phoneId2;

    @Autowired
    public JdbcStockDaoIntegrationTest(JdbcStockDao jdbcStockDao, JdbcTemplate jdbcTemplate) {
        this.jdbcStockDao = jdbcStockDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setup() {
        phoneId1 = 1L;
        phoneId2 = 2L;

        insertPhone(phoneId1, "Apple", "iPhone 14", 1000);
        insertPhone(phoneId2, "Samsung", "Galaxy S23", 1200);

        insertStock(phoneId1, 10, 0);
        insertStock(phoneId2, 5, 0);
    }

    @Test
    void testUpdateReservedByPhoneId() {
        boolean updated = jdbcStockDao.updateReservedByPhoneId(phoneId1, 3);
        assertTrue(updated, "Reserved quantity should be updated");

        Integer reserved = getReserved(phoneId1);
        assertEquals(3, reserved, "Reserved should be set to 3");
    }

    @Test
    void testUpdateReservedItemsBatch() {
        var items = Map.of(
                phoneId1, 4,
                phoneId2, 2
        );

        int[] results = jdbcStockDao.updateReservedItems(items);
        assertEquals(2, results.length, "Should update 2 items");

        assertEquals(4, getReserved(phoneId1));
        assertEquals(2, getReserved(phoneId2));
    }

    @Test
    void testDecreaseReservedByPhoneId() {
        jdbcStockDao.updateReservedByPhoneId(phoneId1, 5);

        boolean decreased = jdbcStockDao.decreaseReservedByPhoneId(phoneId1, 3);
        assertTrue(decreased, "Reserved quantity should be decreased");

        assertEquals(2, getReserved(phoneId1), "Reserved should be decreased to 2");
    }

    @Test
    void testUpdateReservedByPhoneIdFailsIfNotEnoughStock() {
        boolean updated = jdbcStockDao.updateReservedByPhoneId(phoneId1, 15);
        assertFalse(updated, "Update should fail because not enough stock");

        assertEquals(0, getReserved(phoneId1), "Reserved should remain unchanged");
    }

    @Test
    void testDecreaseReservedByPhoneIdFailsIfReservedTooLow() {
        boolean decreased = jdbcStockDao.decreaseReservedByPhoneId(phoneId1, 5);
        assertFalse(decreased, "Decrease should fail because reserved is too low");

        assertEquals(0, getReserved(phoneId1), "Reserved should remain unchanged");
    }

    @Test
    void testDecreaseReservedByPhoneIdToZero() {
        jdbcStockDao.updateReservedByPhoneId(phoneId1, 5);

        boolean decreased = jdbcStockDao.decreaseReservedByPhoneId(phoneId1, 5);
        assertTrue(decreased, "Reserved quantity should be decreased to zero");

        Integer reserved = getReserved(phoneId1);
        assertEquals(0, reserved, "Reserved should now be 0");
    }

    @Test
    void testDecreaseReservedByPhoneIdPartial() {
        jdbcStockDao.updateReservedByPhoneId(phoneId1, 7);

        boolean decreased = jdbcStockDao.decreaseReservedByPhoneId(phoneId1, 3);
        assertTrue(decreased, "Reserved quantity should be decreased partially");

        Integer reserved = getReserved(phoneId1);
        assertEquals(4, reserved, "Reserved should now be 4");
    }

    @Test
    void testDecreaseReservedByPhoneIdFailsIfOverDecrease() {
        jdbcStockDao.updateReservedByPhoneId(phoneId1, 2);

        boolean decreased = jdbcStockDao.decreaseReservedByPhoneId(phoneId1, 5);
        assertFalse(decreased, "Decrease should fail because reserved is too low");

        Integer reserved = getReserved(phoneId1);
        assertEquals(2, reserved, "Reserved should remain unchanged");
    }


    private void insertPhone(Long id, String brand, String model, int price) {
        jdbcTemplate.update(
                "INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                id,
                brand,
                model,
                price
        );
    }

    private void insertStock(Long phoneId, int stock, int reserved) {
        jdbcTemplate.update(
                "INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)",
                phoneId,
                stock,
                reserved
        );
    }

    private Integer getReserved(Long phoneId) {
        return jdbcTemplate.queryForObject(
                "SELECT reserved FROM stocks WHERE phoneId = ?",
                Integer.class,
                phoneId
        );
    }


}
