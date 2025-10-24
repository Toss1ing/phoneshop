package com.es.phoneshop.web.controller.pages;

import com.es.core.exception.StockException;
import com.es.core.service.order.OrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/order")
public class OrderPageController {
    @Resource
    private OrderService orderService;

    private final static String ORDER_PAGE = "orderPage";

    @RequestMapping(method = RequestMethod.GET)
    public String getOrder() throws StockException {
        return ORDER_PAGE;
    }
}
