package com.es.phoneshop.web.controller.pages;

import com.es.core.dto.order.UserPersonalInfoDto;
import com.es.core.exception.CartChangedException;
import com.es.core.exception.NotValidDataException;
import com.es.core.model.order.Order;
import com.es.core.service.order.OrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/order")
public class OrderPageController {

    private static final String ORDER_DRAFT_ATTRIBUTE = "order";
    private static final String USER_PERSONAL_INFO_ATTRIBUTE = "userPersonalInfo";
    private static final String ERROR_ATTRIBUTE = "error";

    private static final String ORDER_NOT_FOUND_MESSAGE = "Draft order not found. Please start again.";
    private static final String ORDER_EDIT_EXCEPTION_MESSAGE = "Cart has changed since you started checkout. Please review your order.";

    private static final String ORDER_PAGE = "orderPage";
    private static final String REDIRECT_ORDER_OVERVIEW_PAGE = "redirect:/orderOverview/%s";
    private static final String REDIRECT_ORDER_PAGE = "redirect:/order";

    @Resource
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET)
    public String getOrder(HttpSession session, Model model) {
        Order draft = getOrCreateDraft(session);
        model.addAttribute(ORDER_DRAFT_ATTRIBUTE, draft);
        addUserPersonalInfoIfAbsent(model);
        return ORDER_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String saveOrder(
            @ModelAttribute(USER_PERSONAL_INFO_ATTRIBUTE) @Valid UserPersonalInfoDto userPersonalInfoDto,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Order draft = getDraft(session, redirectAttributes);
        if (draft == null) {
            return REDIRECT_ORDER_PAGE;
        }

        if (bindingResult.hasErrors()) {
            return handleBindingErrors(
                    userPersonalInfoDto,
                    bindingResult,
                    redirectAttributes
            );
        }

        return processOrder(
                draft,
                userPersonalInfoDto,
                session,
                redirectAttributes
        );
    }

    private String processOrder(Order draft,
                                UserPersonalInfoDto userPersonalInfoDto,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            orderService.placeOrder(draft, userPersonalInfoDto);
            session.removeAttribute(ORDER_DRAFT_ATTRIBUTE);
            return String.format(REDIRECT_ORDER_OVERVIEW_PAGE, draft.getSecureId());
        } catch (CartChangedException ex) {
            return handleCartChanged(redirectAttributes);
        } catch (NotValidDataException ex) {
            return handleNotValidDataException(redirectAttributes, ex);
        }
    }

    private Order getOrCreateDraft(HttpSession session) {
        Order draft = (Order) session.getAttribute(ORDER_DRAFT_ATTRIBUTE);
        if (draft == null || !orderService.isOrderConsistency(draft)) {
            draft = orderService.createOrder();
            session.setAttribute(ORDER_DRAFT_ATTRIBUTE, draft);
        }
        return draft;
    }

    private void addUserPersonalInfoIfAbsent(Model model) {
        if (!model.containsAttribute(USER_PERSONAL_INFO_ATTRIBUTE)) {
            model.addAttribute(USER_PERSONAL_INFO_ATTRIBUTE, new UserPersonalInfoDto());
        }
    }

    Order getDraft(HttpSession session, RedirectAttributes redirectAttributes) {
        Order draft = (Order) session.getAttribute(ORDER_DRAFT_ATTRIBUTE);
        if (draft == null) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, ORDER_NOT_FOUND_MESSAGE);
        }
        return draft;
    }

    private String handleBindingErrors(UserPersonalInfoDto userPersonalInfoDto,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(USER_PERSONAL_INFO_ATTRIBUTE, userPersonalInfoDto);
        redirectAttributes.addFlashAttribute(
                BindingResult.MODEL_KEY_PREFIX + USER_PERSONAL_INFO_ATTRIBUTE,
                bindingResult
        );
        return REDIRECT_ORDER_PAGE;
    }

    private String handleCartChanged(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                ERROR_ATTRIBUTE,
                ORDER_EDIT_EXCEPTION_MESSAGE
        );
        Order updatedDraft = orderService.createOrder();
        redirectAttributes.addFlashAttribute(ORDER_DRAFT_ATTRIBUTE, updatedDraft);
        return REDIRECT_ORDER_PAGE;
    }

    private String handleNotValidDataException(RedirectAttributes redirectAttributes, NotValidDataException ex) {
        redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, ex.getMessage());
        return REDIRECT_ORDER_PAGE;
    }
}
