package com.es.core.dao.orderItem;

import com.es.core.model.order.OrderItem;
import com.es.core.model.phone.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:context/applicationContext-coreTest.xml")
public class JdbcOrderItemDaoIntegrationTest {

    @Autowired
    private JdbcOrderItemDao jdbcOrderItemDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long orderId;
    private Long phoneId1;
    private Long phoneId2;

    @BeforeEach
    void setup() {
        phoneId1 = insertPhone("Apple", "iPhone 14", 1000);
        phoneId2 = insertPhone("Samsung", "Galaxy S23", 1200);

        orderId = insertOrder();
    }

    @Test
    void testSaveOrderItemsByOrderIdInsertsItems() {
        Phone phone1 = new Phone();
        phone1.setId(phoneId1);

        Phone phone2 = new Phone();
        phone2.setId(phoneId2);

        List<OrderItem> items = List.of(
                new OrderItem(phone1, 2),
                new OrderItem(phone2, 3)
        );

        jdbcOrderItemDao.saveOrderItemsByOrderId(orderId, items);

        Integer quantity1 = getOrderItemQuantity(orderId, phoneId1);
        Integer quantity2 = getOrderItemQuantity(orderId, phoneId2);

        assertEquals(2, quantity1);
        assertEquals(3, quantity2);
    }

    @Test
    void testSaveOrderItemsByOrderIdWithEmptyListDoesNothing() {
        jdbcOrderItemDao.saveOrderItemsByOrderId(orderId, List.of());

        List<Integer> quantities = jdbcTemplate.queryForList(
                "SELECT quantity FROM orderItems WHERE orderId = ?",
                Integer.class,
                orderId
        );

        assertEquals(0, quantities.size(), "No items should be inserted");
    }

    private Long insertPhone(String brand, String model, int price) {
        jdbcTemplate.update("INSERT INTO phones (brand, model, price) VALUES (?, ?, ?)", brand, model, price);
        return jdbcTemplate.queryForObject("SELECT id FROM phones WHERE brand=? AND model=?", Long.class, brand, model);
    }

    private Long insertOrder() {
        jdbcTemplate.update(
                "INSERT INTO orders " +
                        "(secureId, subtotal, deliveryPrice, totalPrice, firstName, lastName, deliveryAddress, contactPhoneNO, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "secureIdTest",
                1000,
                50,
                1050,
                "Kirill",
                "Ivanov",
                "123 Street",
                "1234567890",
                "NEW"
        );
        return jdbcTemplate.queryForObject("SELECT id FROM orders WHERE secureId=?", Long.class, "secureIdTest");
    }

    private Integer getOrderItemQuantity(Long orderId, Long phoneId) {
        return jdbcTemplate.queryForObject(
                "SELECT quantity FROM orderItems WHERE orderId = ? AND phoneId = ?",
                Integer.class,
                orderId, phoneId
        );
    }

}
