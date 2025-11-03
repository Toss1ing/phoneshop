package com.es.phoneshop.web.controller.pages;

import com.es.core.dto.order.UserPersonalInfoDto;
import com.es.core.exception.CartChangedException;
import com.es.core.exception.NotValidDataException;
import com.es.core.model.order.Order;
import com.es.core.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderPageControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private OrderPageController controller;

    private Order draftOrder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        draftOrder = new Order();
        draftOrder.setSecureId("secure123");
    }

    @Test
    void testGetOrderWithExistingDraft() {
        when(session.getAttribute("order")).thenReturn(draftOrder);
        when(orderService.isOrderConsistency(draftOrder)).thenReturn(true);

        String view = controller.getOrder(session, model);

        assertEquals("orderPage", view);
        verify(model).addAttribute("order", draftOrder);
        verify(model).addAttribute(eq("userPersonalInfo"), any(UserPersonalInfoDto.class));
    }

    @Test
    void testGetOrderWithoutDraftCreatesNew() {
        when(session.getAttribute("order")).thenReturn(null);
        when(orderService.createOrder()).thenReturn(draftOrder);

        String view = controller.getOrder(session, model);

        assertEquals("orderPage", view);
        verify(session).setAttribute("order", draftOrder);
        verify(model).addAttribute("order", draftOrder);
        verify(model).addAttribute(eq("userPersonalInfo"), any(UserPersonalInfoDto.class));
    }

    @Test
    void testSaveOrderWithValidationErrors() {
        when(session.getAttribute("order")).thenReturn(draftOrder);
        when(bindingResult.hasErrors()).thenReturn(true);

        UserPersonalInfoDto dto = new UserPersonalInfoDto();

        String view = controller.saveOrder(dto, bindingResult, session, redirectAttributes);

        assertEquals("redirect:/order", view);
        verify(redirectAttributes).addFlashAttribute("userPersonalInfo", dto);
        verify(redirectAttributes).addFlashAttribute(
                startsWith(BindingResult.MODEL_KEY_PREFIX),
                eq(bindingResult)
        );
    }

    @Test
    void testSaveOrderSuccessfully() throws Exception {
        when(session.getAttribute("order")).thenReturn(draftOrder);
        when(bindingResult.hasErrors()).thenReturn(false);

        UserPersonalInfoDto dto = new UserPersonalInfoDto();

        String view = controller.saveOrder(dto, bindingResult, session, redirectAttributes);

        assertEquals("redirect:/orderOverview/secure123", view);
        verify(orderService).placeOrder(draftOrder, dto);
        verify(session).removeAttribute("order");
    }

    @Test
    void testSaveOrderCartChangedException() throws Exception {
        when(session.getAttribute("order")).thenReturn(draftOrder);
        when(bindingResult.hasErrors()).thenReturn(false);

        UserPersonalInfoDto dto = new UserPersonalInfoDto();

        doThrow(new CartChangedException("")).when(orderService).placeOrder(draftOrder, dto);
        Order newDraft = new Order();
        when(orderService.createOrder()).thenReturn(newDraft);

        String view = controller.saveOrder(dto, bindingResult, session, redirectAttributes);

        assertEquals("redirect:/order", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
        verify(redirectAttributes).addFlashAttribute("order", newDraft);
    }

    @Test
    void testSaveOrderNotValidDataException() throws Exception {
        when(session.getAttribute("order")).thenReturn(draftOrder);
        when(bindingResult.hasErrors()).thenReturn(false);

        UserPersonalInfoDto dto = new UserPersonalInfoDto();
        String errorMessage = "Invalid data";

        doThrow(new NotValidDataException(errorMessage)).when(orderService).placeOrder(draftOrder, dto);

        String view = controller.saveOrder(dto, bindingResult, session, redirectAttributes);

        assertEquals("redirect:/order", view);
        verify(redirectAttributes).addFlashAttribute("error", errorMessage);
    }

    @Test
    void testGetDraftReturnsNullWhenNoOrder() {
        when(session.getAttribute("order")).thenReturn(null);

        Order result = controller.getDraft(session, redirectAttributes);

        assertEquals(null, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }
}
