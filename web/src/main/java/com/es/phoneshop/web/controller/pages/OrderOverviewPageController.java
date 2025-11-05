package com.es.phoneshop.web.controller.pages;

import com.es.core.model.order.Order;
import com.es.core.service.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/orderOverview")
public class OrderOverviewPageController {

    private final OrderService orderService;

    public OrderOverviewPageController(OrderService orderService) {
        this.orderService = orderService;
    }

    private static final String ORDER_ATTRIBUTE = "order";

    private static final String ORDER_OVERVIEW_PAGE = "orderOverviewPage";

    @RequestMapping("/{secureId}")
    public String getOrder(@PathVariable String secureId, Model model) {
        Order order = orderService.getOrderBySecureId(secureId);
        model.addAttribute(ORDER_ATTRIBUTE, order);
        return ORDER_OVERVIEW_PAGE;
    }
}
