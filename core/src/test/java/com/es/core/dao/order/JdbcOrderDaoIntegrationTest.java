package com.es.core.dao.order;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:context/applicationContext-coreTest.xml")
public class JdbcOrderDaoIntegrationTest {

    @Autowired
    private JdbcOrderDao jdbcOrderDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long phoneId;

    @BeforeEach
    void setup() {
        phoneId = insertPhone("Apple", "iPhone 14", new BigDecimal("1000"));
    }

    @Test
    void testSaveOrderInsertsNewOrder() {
        Order order = buildTestOrder("secure123");

        jdbcOrderDao.saveOrder(order);

        assertNotNull(order.getId(), "Order ID should be generated");
        assertTrue(orderExists(order.getId()), "Order should exist in database");
        assertEquals("secure123", getOrderSecureId(order.getId()), "Secure ID should match");
    }

    @Test
    void testFindOrderBySecureIdReturnsOrderWithItems() {
        Long orderId = insertTestOrder("secure456");
        insertOrderItem(orderId, phoneId, 2);

        Optional<Order> optionalOrder = jdbcOrderDao.findOrderBySecureId("secure456");

        assertTrue(optionalOrder.isPresent(), "Order should be found by secureId");

        Order order = optionalOrder.get();
        assertEquals(orderId, order.getId());
        assertEquals("secure456", order.getSecureId());
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertNotNull(order.getOrderItems(), "Order items should not be null");
        assertFalse(order.getOrderItems().isEmpty(), "Order should contain items");

        OrderItem item = order.getOrderItems().get(0);
        assertEquals(2, item.getQuantity());
        assertNotNull(item.getPhone(), "OrderItem should contain a phone");
        assertEquals(phoneId, item.getPhone().getId());
    }

    @Test
    void testFindOrderBySecureIdReturnsEmptyWhenNotExists() {
        Optional<Order> result = jdbcOrderDao.findOrderBySecureId("nonexistent");
        assertTrue(result.isEmpty(), "Optional should be empty when order not found");
    }

    private Order buildTestOrder(String secureId) {
        Order order = new Order();
        order.setSecureId(secureId);
        order.setSubtotal(new BigDecimal("900"));
        order.setDeliveryPrice(new BigDecimal("10"));
        order.setTotalPrice(new BigDecimal("910"));
        order.setFirstName("John");
        order.setLastName("Doe");
        order.setDeliveryAddress("123 Test Street");
        order.setContactPhoneNo("+123456789");
        order.setStatus(OrderStatus.NEW);
        return order;
    }

    private Long insertPhone(String brand, String model, BigDecimal price) {
        jdbcTemplate.update("INSERT INTO phones (brand, model, price) VALUES (?, ?, ?)", brand, model, price);
        return jdbcTemplate.queryForObject("SELECT id FROM phones WHERE model = ?", Long.class, model);
    }

    private Long insertTestOrder(String secureId) {
        jdbcTemplate.update("""
                        INSERT INTO orders 
                        (secureId, subtotal, deliveryPrice, totalPrice, firstName, lastName, deliveryAddress, contactPhoneNo, status)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                secureId, 100, 10, 110, "Alice", "Smith", "Test Street 5", "+123456789", "NEW"
        );
        return jdbcTemplate.queryForObject("SELECT id FROM orders WHERE secureId = ?", Long.class, secureId);
    }

    private Long insertOrderItem(Long orderId, Long phoneId, int quantity) {
        jdbcTemplate.update("INSERT INTO orderItems (orderId, phoneId, quantity) VALUES (?, ?, ?)", orderId, phoneId, quantity);
        return jdbcTemplate.queryForObject("SELECT id FROM orderItems WHERE orderId = ? AND phoneId = ?", Long.class, orderId, phoneId);
    }

    private boolean orderExists(Long orderId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders WHERE id = ?", Integer.class, orderId);
        return count != null && count > 0;
    }

    private String getOrderSecureId(Long orderId) {
        return jdbcTemplate.queryForObject("SELECT secureId FROM orders WHERE id = ?", String.class, orderId);
    }
}
