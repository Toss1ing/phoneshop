package com.es.phoneshop.web.controller.pages;

import com.es.core.dto.CartView;
import com.es.core.exception.StockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.cart.CartItem;
import com.es.core.model.phone.Phone;
import com.es.core.service.cart.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CartPageControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private CartPageController controller;

    private CartView cartView;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        cartView = new CartView(new Cart());
    }


    @Test
    void testGetCartDoesNotAddCartViewIfPresent() {
        when(model.containsAttribute("cartView")).thenReturn(true);

        String view = controller.getCart(model);

        assertEquals("cartPage", view);
        verify(model, never()).addAttribute(eq("cartView"), any());
    }

    @Test
    void testUpdateCartSuccessful() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(cartService.getCart()).thenReturn(cartView.getCart());

        String view = controller.updateCart(cartView, bindingResult, redirectAttributes);

        assertEquals("redirect:/cart", view);
        verify(cartService).update(cartView.getItems());
    }

    @Test
    void testUpdateCartStockException() {
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        Phone phone = new Phone();
        phone.setId(1L);
        cartItem.setPhone(phone);
        cartItem.setQuantity(2);
        cart.getItems().add(cartItem);

        cartView = new CartView(cart);

        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new StockException(Map.of(1L, "Out of stock")))
                .when(cartService).update(cartView.getItems());

        when(cartService.getCart()).thenReturn(cart);

        when(bindingResult.hasErrors()).thenReturn(false).thenReturn(true);

        String view = controller.updateCart(cartView, bindingResult, redirectAttributes);

        assertEquals("redirect:/cart", view);

        verify(bindingResult).rejectValue("items[1]", "OutOfStock", "Out of stock");
        verify(redirectAttributes).addFlashAttribute(eq("cartView"), any(CartView.class));
        verify(redirectAttributes).addFlashAttribute(
                startsWith(BindingResult.MODEL_KEY_PREFIX),
                eq(bindingResult)
        );
    }



    @Test
    void testDeleteCartItemSuccessful() {
        Long phoneId = 1L;

        String view = controller.deleteCartItem(phoneId, redirectAttributes);

        assertEquals("redirect:/cart", view);
        verify(cartService).remove(phoneId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("deleteError"), any());
    }

    @Test
    void testDeleteCartItemStockException() {
        Long phoneId = 1L;
        doThrow(new StockException("Cannot remove")).when(cartService).remove(phoneId);

        String view = controller.deleteCartItem(phoneId, redirectAttributes);

        assertEquals("redirect:/cart", view);
        verify(redirectAttributes).addFlashAttribute("deleteError", "Cannot remove");
    }
}
