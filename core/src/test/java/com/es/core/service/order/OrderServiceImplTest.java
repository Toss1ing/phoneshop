package com.es.core.service.order;

import com.es.core.dao.color.ColorDao;
import com.es.core.dao.order.OrderDao;
import com.es.core.dao.orderItem.OrderItemDao;
import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
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
import java.util.Map;
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
    private ColorDao colorDao;

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

        doAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1L);
            return null;
        }).when(orderDao).saveOrder(any(Order.class));

        when(orderItemDao.saveOrderItemsByOrderId(anyLong(), anyList())).thenReturn(new int[]{1});
        when(colorDao.findColorsForPhoneIds(anyList())).thenReturn(Map.of());

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

    @Test
    void testFindAllOrdersDelegatesToDao() {
        Page<Order> page = new Page<>(List.of(), 0, 10, 0);

        when(orderDao.findAllOrders(any(Pageable.class))).thenReturn(page);

        Page<Order> result = orderService.findAllOrders(0, 10);

        assertEquals(page, result);
        verify(orderDao).findAllOrders(any(Pageable.class));
    }

    @Test
    void testGetOrderByIdReturnsOrder() {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);
        order.setOrderItems(List.of(new OrderItem(phone, 1)));

        when(orderDao.findOrderById(id)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(id);

        assertEquals(order, result);
        verify(orderDao).findOrderById(id);
    }

    @Test
    void testGetOrderByIdThrowsExceptionWhenNull() {
        assertThrows(NotValidDataException.class, () ->
                orderService.getOrderById(null)
        );
        verify(orderDao, never()).findOrderById(any());
    }

    @Test
    void testGetOrderByIdThrowsExceptionWhenNotFound() {
        when(orderDao.findOrderById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                orderService.getOrderById(1L)
        );
    }

    @Test
    void testUpdateOrderStatusSuccessfully() {
        when(orderDao.updateOrderStatus(1L, OrderStatus.DELIVERED)).thenReturn(1);

        assertDoesNotThrow(() ->
                orderService.updateOrderStatus(1L, OrderStatus.DELIVERED)
        );
        verify(orderDao).updateOrderStatus(1L, OrderStatus.DELIVERED);
    }

    @Test
    void testUpdateOrderStatusThrowsNotFoundWhenZeroRows() {
        when(orderDao.updateOrderStatus(1L, OrderStatus.NEW)).thenReturn(0);

        assertThrows(NotFoundException.class, () ->
                orderService.updateOrderStatus(1L, OrderStatus.NEW)
        );
    }
}
