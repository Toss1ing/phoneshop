package com.es.core.service.cart;

import com.es.core.exception.NotFoundException;
import com.es.core.model.cart.Cart;
import com.es.core.model.cart.CartItem;
import com.es.core.model.phone.Phone;
import com.es.core.service.phone.PhoneService;
import com.es.core.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpSessionCartServiceTest {

    @InjectMocks
    private HttpSessionCartService cartService;

    @Mock
    private PhoneService phoneService;

    @Mock
    private StockService stockService;

    private Phone phone1;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        phone1 = createPhone(BigDecimal.valueOf(1000));
    }

    @Test
    void testAddPhoneNewItem() {
        when(phoneService.findPhoneById(1L)).thenReturn(phone1);

        Cart cart = cartService.addPhone(1L, 2);

        assertEquals(1, cart.getItems().size());
        CartItem item = cart.getItems().get(0);
        assertEquals(phone1, item.getPhone());
        assertEquals(2, item.getQuantity());

        verify(stockService).reservePhone(1L, 2);
    }

    @Test
    void testAddPhoneExistingItemIncreasesQuantity() {
        when(phoneService.findPhoneById(1L)).thenReturn(phone1);

        cartService.addPhone(1L, 2);
        Cart cart = cartService.addPhone(1L, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());

        verify(stockService).reservePhone(1L, 2);
        verify(stockService).reservePhone(1L, 3);
    }

    @Test
    void testAddPhoneNotFoundThrowsException() {
        when(phoneService.findPhoneById(999L)).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> cartService.addPhone(999L, 1));

        verify(stockService, never()).reservePhone(anyLong(), anyInt());
    }

    @Test
    void testUpdateCartSuccessfully() {
        when(phoneService.findPhoneById(1L)).thenReturn(phone1);

        cartService.addPhone(1L, 2);

        Map<Long, Integer> updates = Map.of(1L, 5);
        cartService.update(updates);

        Cart cart = cartService.getCart();
        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());

        verify(stockService).reserveAndValidateItems(Map.of(1L, 3));
    }

    @Test
    void testRemoveCartItem() {
        when(phoneService.findPhoneById(1L)).thenReturn(phone1);

        cartService.addPhone(1L, 2);

        cartService.remove(1L);

        Cart cart = cartService.getCart();
        assertTrue(cart.getItems().isEmpty());

        verify(stockService).decreaseReservedQuantity(1L, 2);
    }

    @Test
    void testGetCartReturnsCopy() {
        when(phoneService.findPhoneById(1L)).thenReturn(phone1);
        cartService.addPhone(1L, 2);

        Cart cart1 = cartService.getCart();
        Cart cart2 = cartService.getCart();

        assertNotSame(cart1, cart2);
        assertEquals(cart1.getTotalQuantity(), cart2.getTotalQuantity());
        assertEquals(cart1.getTotalPrice(), cart2.getTotalPrice());
    }

    @Test
    void testCleanupReservedWhenCartIsEmptyShouldNotCallStockService() {
        cartService.cleanupSessionAndReservedItems();

        verify(stockService, never()).cleanUpReserved(any());

        assertTrue(cartService.getCart().getItems().isEmpty());
        assertEquals(0, cartService.getCart().getTotalQuantity());
        assertEquals(BigDecimal.ZERO, cartService.getCart().getTotalPrice());
    }

    @Test
    void testCleanupReservedWhenCartHasItemsShouldCallStockServiceAndClearCart() {
        when(phoneService.findPhoneById(1L)).thenReturn(phone1);

        cartService.addPhone(1L, 2);

        CartItem item = cartService.getCart().getItems().get(0);

        assertFalse(cartService.getCart().getItems().isEmpty());
        assertEquals(2, item.getQuantity());

        cartService.cleanupSessionAndReservedItems();

        verify(stockService).cleanUpReserved(Map.of(1L, 2));

        assertTrue(cartService.getCart().getItems().isEmpty());
        assertEquals(0, cartService.getCart().getTotalQuantity());
        assertEquals(BigDecimal.ZERO, cartService.getCart().getTotalPrice());
    }

    private Phone createPhone(BigDecimal price) {
        Phone phone = new Phone();
        phone.setId(1L);
        phone.setBrand("Apple");
        phone.setModel("iPhone 14");
        phone.setPrice(price);
        return phone;
    }
}
