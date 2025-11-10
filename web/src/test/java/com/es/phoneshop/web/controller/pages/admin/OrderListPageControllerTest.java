package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.dao.pagination.Page;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.NotValidDataException;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;
import com.es.core.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderListPageControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private OrderListPageController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowOrdersSuccess() {
        int page = 0;
        int size = 10;
        Page<Order> mockPage = new Page<>(List.of(new Order()), 1, 10, 1);

        when(orderService.findAllOrders(page, size)).thenReturn(mockPage);

        String view = controller.showOrders(page, size, model);

        assertEquals("orderList", view);
        verify(model).addAttribute("orders", mockPage.content());
        verify(model).addAttribute("currentPage", page);
        verify(model).addAttribute("pageSize", mockPage.pageSize());
        verify(model).addAttribute("totalPages", mockPage.getTotalPages());
    }

    @Test
    void testShowOrderDetailSuccess() {
        Long orderId = 1L;
        Order mockOrder = new Order();
        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);

        String view = controller.showOrderDetail(orderId, model);

        assertEquals("orderDetail", view);
        verify(model).addAttribute("order", mockOrder);
    }

    @Test
    void testUpdateOrderStatusSuccess() {
        Long id = 1L;
        String status = "NEW";

        String result = controller.updateOrderStatus(id, status, redirectAttributes);

        assertEquals("redirect:/admin/orders/1", result);
        verify(orderService, atLeastOnce()).updateOrderStatus(id, OrderStatus.valueOf(status));
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void testUpdateOrderStatusInvalidStatus() {
        Long id = 1L;
        String invalidStatus = "INVALID_STATUS";

        String result = controller.updateOrderStatus(id, invalidStatus, redirectAttributes);

        assertEquals("redirect:/admin/orders/1", result);
        verify(orderService, never()).updateOrderStatus(anyLong(), any());
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Invalid order status"));
    }


    @Test
    void testUpdateOrderStatusNotFound() {
        Long id = 1L;
        String status = "NEW";
        NotFoundException exception = new NotFoundException("Order not found");

        doThrow(exception).when(orderService).updateOrderStatus(id, OrderStatus.valueOf(status));

        String result = controller.updateOrderStatus(id, status, redirectAttributes);

        assertEquals("redirect:/admin/orders/1", result);
        verify(orderService).updateOrderStatus(id, OrderStatus.NEW);
        verify(redirectAttributes).addFlashAttribute("error", "Order not found");
    }

    @Test
    void testValidateStatusSuccess() {
        OrderStatus result = controllerTestHelperValidateStatus("DELIVERED");
        assertEquals(OrderStatus.DELIVERED, result);
    }

    @Test
    void testValidateStatusInvalidThrows() {
        try {
            controllerTestHelperValidateStatus("INVALID");
        } catch (NotValidDataException e) {
            assertEquals("Invalid order status", e.getMessage());
        }
    }

    private OrderStatus controllerTestHelperValidateStatus(String status) {
        try {
            var method = OrderListPageController.class.getDeclaredMethod("validateStatus", String.class);
            method.setAccessible(true);
            return (OrderStatus) method.invoke(controller, status);
        } catch (Exception e) {
            if (e.getCause() instanceof NotValidDataException ex) throw ex;
            throw new RuntimeException(e);
        }
    }
}
