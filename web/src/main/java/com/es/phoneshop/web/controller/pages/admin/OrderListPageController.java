package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.dao.pagination.Page;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.NotValidDataException;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;
import com.es.core.service.order.OrderService;
import com.es.core.util.ExceptionMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping(value = "/admin/orders")
public class OrderListPageController {

    @Resource
    private OrderService orderService;

    private static final String ORDER_LIST_ATTRIBUTE = "orders";
    private static final String ORDER_ATTRIBUTE = "order";
    private static final String CURRENT_PAGE_ATTRIBUTE = "currentPage";
    private static final String TOTAL_PAGES_ATTRIBUTE = "totalPages";
    private static final String PAGE_SIZE_ATTRIBUTE = "pageSize";

    private static final String ERROR_ATTRIBUTE = "error";

    private static final String ADMIN_ORDER_LIST_PAGE = "orderList";
    private static final String ADMIN_ORDER_DETAIL_PAGE = "orderDetail";
    private static final String REDIRECT_ORDER_DETAIL_PAGE = "redirect:/admin/orders/%d";

    @GetMapping()
    public String showOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model

    ) {
        Page<Order> orders = orderService.findAllOrders(page, size);

        model.addAttribute(ORDER_LIST_ATTRIBUTE, orders.content());
        model.addAttribute(CURRENT_PAGE_ATTRIBUTE, page);
        model.addAttribute(PAGE_SIZE_ATTRIBUTE, orders.pageSize());
        model.addAttribute(TOTAL_PAGES_ATTRIBUTE, orders.getTotalPages());

        return ADMIN_ORDER_LIST_PAGE;
    }

    @GetMapping("/{id}")
    public String showOrderDetail(
            @PathVariable Long id,
            Model model
    ) {
        Order order = orderService.getOrderById(id);

        model.addAttribute(ORDER_ATTRIBUTE, order);
        return ADMIN_ORDER_DETAIL_PAGE;
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes
    ) {
        try {
            OrderStatus orderStatus = validateStatus(status);
            orderService.updateOrderStatus(id, orderStatus);
        } catch (NotValidDataException | NotFoundException e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, e.getMessage());
        }

        return String.format(REDIRECT_ORDER_DETAIL_PAGE, id);
    }

    private OrderStatus validateStatus(String status) {
        return Arrays.stream(OrderStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new NotValidDataException(ExceptionMessage.INVALID_ORDER_STATUS_MESSAGE));
    }
}
