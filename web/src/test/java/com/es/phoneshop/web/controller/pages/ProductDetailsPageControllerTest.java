package com.es.phoneshop.web.controller.pages;

import com.es.core.model.phone.Phone;
import com.es.core.service.phone.PhoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductDetailsPageControllerTest {

    @Mock
    private PhoneService phoneService;

    @Mock
    private Model model;

    @InjectMocks
    private ProductDetailsPageController controller;

    private Phone phone;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        phone = new Phone();
        phone.setId(1L);
        phone.setBrand("Apple");
        phone.setModel("iPhone 14");
    }

    @Test
    void testGetProductDetailsAddsPhoneToModel() {
        Long phoneId = 1L;
        when(phoneService.findPhoneById(phoneId)).thenReturn(phone);

        String view = controller.getProductDetails(phoneId, model);

        assertEquals("productDetails", view);
        verify(phoneService).findPhoneById(phoneId);
        verify(model).addAttribute(eq("phone"), eq(phone));
    }

    @Test
    void testGetProductDetailsReturnsCorrectViewName() {
        when(phoneService.findPhoneById(1L)).thenReturn(phone);

        String viewName = controller.getProductDetails(1L, model);

        assertEquals("productDetails", viewName);
    }

    @Test
    void testGetProductDetailsWhenPhoneNotFound() {
        Long phoneId = 999L;
        when(phoneService.findPhoneById(phoneId)).thenReturn(null);

        String view = controller.getProductDetails(phoneId, model);

        assertEquals("productDetails", view);
        verify(model).addAttribute("phone", null);
    }
}
