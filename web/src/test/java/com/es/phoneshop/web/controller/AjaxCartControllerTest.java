package com.es.phoneshop.web.controller;

import com.es.core.dto.cart.AddPhoneToCartRequest;
import com.es.core.model.cart.Cart;
import com.es.core.service.cart.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AjaxCartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private AjaxCartController controller;

    private Cart mockCart;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockCart = new Cart();
        mockCart.setTotalQuantity(5L);
    }

    @Test
    void testGetCartReturnsCartAndStatusOk() {
        when(cartService.getCart()).thenReturn(mockCart);

        ResponseEntity<Cart> response = controller.getCart();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCart, response.getBody());
        verify(cartService).getCart();
    }

    @Test
    void testAddPhoneReturnsUpdatedCartAndStatusAccepted() {
        AddPhoneToCartRequest request = new AddPhoneToCartRequest();
        request.setPhoneId(1L);
        request.setQuantity(3);

        when(cartService.addPhone(1L, 3)).thenReturn(mockCart);

        ResponseEntity<Cart> response = controller.addPhone(request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(mockCart, response.getBody());
        verify(cartService).addPhone(1L, 3);
    }

    @Test
    void testAddPhoneWithZeroQuantityStillCallsService() {
        AddPhoneToCartRequest request = new AddPhoneToCartRequest();
        request.setPhoneId(2L);
        request.setQuantity(0);

        when(cartService.addPhone(2L, 0)).thenReturn(mockCart);

        ResponseEntity<Cart> response = controller.addPhone(request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(cartService).addPhone(2L, 0);
    }
}
