package com.es.phoneshop.web.controller;

import com.es.core.dto.AddPhoneToCartRequest;
import com.es.core.model.cart.Cart;
import com.es.core.service.cart.CartService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/ajaxCart")
public class AjaxCartController {

    @Resource
    private CartService cartService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Cart> getCart() {
        return new ResponseEntity<>(cartService.getCart(), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Cart> addPhone(
            @Valid @RequestBody AddPhoneToCartRequest request
    ) {

        return new ResponseEntity<>(cartService.addPhone(request.getPhoneId(), request.getQuantity()),
                HttpStatus.ACCEPTED
        );
    }
}
