package com.es.phoneshop.web.controller.pages;

import com.es.core.exception.NotValidDataException;
import com.es.core.model.order.Order;
import com.es.core.service.order.OrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/orderOverview")
public class OrderOverviewPageController {

    private static final String ORDER_ATTRIBUTE = "order";

    private static final String ORDER_OVERVIEW_PAGE = "orderOverviewPage";

    @Resource
    private OrderService orderService;

    @RequestMapping("/{secureId}")
    public String getOrder(@PathVariable String secureId, Model model) {
        Order order = orderService.getOrderBySecureId(secureId);
        model.addAttribute(ORDER_ATTRIBUTE, order);
        return ORDER_OVERVIEW_PAGE;
    }
}
