package com.es.phoneshop.web.controller.pages;

import com.es.core.exception.OutOfStockException;
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

    @RequestMapping(method = RequestMethod.GET)
    public void getOrder() throws OutOfStockException {
        orderService.createOrder(null);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void placeOrder() throws OutOfStockException {
        orderService.placeOrder(null);
    }
}
