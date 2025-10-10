package com.es.phoneshop.web.controller.pages;

import com.es.core.service.cart.CartService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/cart")
public class CartPageController {
    @Resource
    private CartService cartService;

    private static final String CART_PAGE_NAME = "cartPage";

    @RequestMapping(method = RequestMethod.GET)
    public String getCart() {
        return CART_PAGE_NAME;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateCart() {
        cartService.update(null);
    }
}
