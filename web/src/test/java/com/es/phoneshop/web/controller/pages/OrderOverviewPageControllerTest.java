package com.es.phoneshop.web.controller.pages;

import com.es.core.model.order.Order;
import com.es.core.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderOverviewPageControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Model model;

    @InjectMocks
    private OrderOverviewPageController controller;

    private Order order;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        order = new Order();
        order.setSecureId("secure123");
    }

    @Test
    void testGetOrderAddsOrderToModel() {
        when(orderService.getOrderBySecureId("secure123")).thenReturn(order);

        String view = controller.getOrder("secure123", model);

        assertEquals("orderOverviewPage", view);
        verify(orderService).getOrderBySecureId("secure123");
        verify(model).addAttribute("order", order);
    }

    @Test
    void testGetOrderWithDifferentSecureId() {
        Order anotherOrder = new Order();
        anotherOrder.setSecureId("abc999");

        when(orderService.getOrderBySecureId("abc999")).thenReturn(anotherOrder);

        String view = controller.getOrder("abc999", model);

        assertEquals("orderOverviewPage", view);
        verify(orderService).getOrderBySecureId("abc999");
        verify(model).addAttribute("order", anotherOrder);
    }
}
