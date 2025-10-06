package com.es.phoneshop.web.controller;

import com.es.core.service.cart.CartService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/ajaxCart")
public class AjaxCartController {
    @Resource
    private CartService cartService;

    @RequestMapping(method = RequestMethod.POST)
    public void addPhone(Long phoneId, Long quantity) {
        cartService.addPhone(phoneId, quantity);
    }
}
