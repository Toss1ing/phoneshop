package com.es.phoneshop.web.controller.pages;

import com.es.core.dto.cart.MassAddToCart;
import com.es.core.exception.StockException;
import com.es.core.service.cart.CartService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mass-add")
public class massAddToCartController {

    private final CartService cartService;

    private static final String ADD_TO_CART_PAGE_NAME = "massAddToCart";
    private static final String MASS_ADD_TO_CART_ATTRIBUTE = "massAddToCart";
    private static final String REDIRECT_MASS_ADD_TO_CART = "redirect:/mass-add";
    private static final String ERROR_MASS_ADD = "errorMassAdd";

    public massAddToCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getMassAddToCartPage(Model model) {
        if (!model.containsAttribute(ERROR_MASS_ADD)) {
            model.addAttribute(ERROR_MASS_ADD, null);
        }
        if(!model.containsAttribute(MASS_ADD_TO_CART_ATTRIBUTE)){
            model.addAttribute(MASS_ADD_TO_CART_ATTRIBUTE, new MassAddToCart());
        }
        return ADD_TO_CART_PAGE_NAME;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String massAddToCart(
            @ModelAttribute(MASS_ADD_TO_CART_ATTRIBUTE) @Valid MassAddToCart massAddToCart,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX + MASS_ADD_TO_CART_ATTRIBUTE,
                    bindingResult
            );
            redirectAttributes.addFlashAttribute(
                    MASS_ADD_TO_CART_ATTRIBUTE,
                    massAddToCart
            );
            return REDIRECT_MASS_ADD_TO_CART;
        }

        try {
            cartService.addPhonesByModels(massAddToCart);
        } catch (StockException ex) {
            redirectAttributes.addFlashAttribute(
                    MASS_ADD_TO_CART_ATTRIBUTE,
                    massAddToCart
            );
            redirectAttributes.addFlashAttribute(
                    ERROR_MASS_ADD,
                    ex.getMessage()
            );
        }
        return REDIRECT_MASS_ADD_TO_CART;
    }

}
