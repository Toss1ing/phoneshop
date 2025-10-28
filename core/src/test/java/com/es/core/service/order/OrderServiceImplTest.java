package com.es.core.service.order;

import com.es.core.dao.order.OrderDao;
import com.es.core.dao.orderItem.OrderItemDao;
import com.es.core.dto.order.UserPersonalInfoDto;
import com.es.core.exception.CartChangedException;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.NotValidDataException;
import com.es.core.model.cart.Cart;
import com.es.core.model.cart.CartItem;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.Phone;
import com.es.core.service.cart.CartService;
import com.es.core.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private CartService cartService;

    @Mock
    private StockService stockService;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderItemDao orderItemDao;

    private Cart cart;
    private Phone phone;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        phone = new Phone();
        phone.setId(1L);
        phone.setBrand("Apple");
        phone.setModel("iPhone 14");
        phone.setPrice(BigDecimal.valueOf(1000));
        orderService.deliveryPrice = BigDecimal.valueOf(100);

        cart = new Cart();
    }

    @Test
    void testCreateOrderWithEmptyCart() {
        when(cartService.getCart()).thenReturn(new Cart());

        Order order = orderService.createOrder();

        assertNotNull(order);
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    void testCreateOrderWithItems() {
        CartItem item = new CartItem(phone, 2);
        cart.getItems().add(item);
        cart.setTotalPrice(BigDecimal.valueOf(2000));

        when(cartService.getCart()).thenReturn(cart);

        Order order = orderService.createOrder();

        assertNotNull(order);
        assertEquals(1, order.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(2000).add(order.getDeliveryPrice()), order.getTotalPrice());
        assertEquals(OrderStatus.NEW, order.getStatus());
    }

    @Test
    void testPlaceOrderSuccessfully() {
        CartItem item = new CartItem(phone, 2);
        cart.getItems().add(item);
        cart.setTotalPrice(BigDecimal.valueOf(2000));

        when(cartService.getCart()).thenReturn(cart);

        Order order = orderService.createOrder();

        UserPersonalInfoDto userInfo = new UserPersonalInfoDto();
        userInfo.setFirstName("John");
        userInfo.setLastName("Doe");
        userInfo.setDeliveryAddress("123 Street");
        userInfo.setContactPhoneNo("123456789");

        orderService.placeOrder(order, userInfo);

        verify(stockService).decreaseStock(anyMap());
        verify(orderDao).saveOrder(order);
        verify(orderItemDao).saveOrderItemsByOrderId(order.getId(), order.getOrderItems());
        verify(cartService).cleanupSessionAndReservedItems();

        assertEquals("John", order.getFirstName());
        assertEquals("Doe", order.getLastName());
        assertEquals("123 Street", order.getDeliveryAddress());
        assertEquals("123456789", order.getContactPhoneNo());
    }

    @Test
    void testPlaceOrderThrowsNotValidDataExceptionWhenEmpty() {
        Order emptyOrder = new Order();

        assertThrows(NotValidDataException.class, () ->
                orderService.placeOrder(emptyOrder, new UserPersonalInfoDto())
        );

        verify(stockService, never()).decreaseStock(anyMap());
        verify(orderDao, never()).saveOrder(any());
    }

    @Test
    void testPlaceOrderThrowsCartChangedException() {
        CartItem item = new CartItem(phone, 2);
        cart.getItems().add(item);

        Order order = new Order();
        order.setOrderItems(List.of(new OrderItem(phone, 3)));

        when(cartService.getCart()).thenReturn(cart);

        assertThrows(CartChangedException.class, () ->
                orderService.placeOrder(order, new UserPersonalInfoDto())
        );
    }

    @Test
    void testGetOrderBySecureIdFound() {
        String secureId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setSecureId(secureId);

        when(orderDao.findOrderBySecureId(secureId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderBySecureId(secureId);

        assertEquals(order, result);
    }

    @Test
    void testGetOrderBySecureIdNotFound() {
        String secureId = UUID.randomUUID().toString();
        when(orderDao.findOrderBySecureId(secureId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                orderService.getOrderBySecureId(secureId)
        );
    }
}
