package com.es.phoneshop.web.controller.pages;

import com.es.core.dto.CartView;
import com.es.core.exception.StockException;
import com.es.core.service.cart.CartService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/cart")
public class CartPageController {

    @Resource
    private CartService cartService;

    private static final String CART_PAGE = "cartPage";
    private static final String REDIRECT_CART_PAGE = "redirect:/cart";

    private static final String OUT_OF_STOCK_CODE = "OutOfStock";

    private static final String CART_VIEW_ATTRIBUTE = "cartView";
    private static final String DELETE_ERROR_ATTRIBUTE = "deleteError";

    private static final String ITEM_FIELD_TEMPLATE = "items[%d]";

    @RequestMapping(method = RequestMethod.GET)
    public String getCart(Model model) {
        if (!model.containsAttribute(CART_VIEW_ATTRIBUTE)) {
            model.addAttribute(
                    CART_VIEW_ATTRIBUTE,
                    new CartView(cartService.getCart())
            );
        }
        return CART_PAGE;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String updateCart(
            @ModelAttribute(CART_VIEW_ATTRIBUTE) @Valid CartView cartView,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (!bindingResult.hasErrors()) {
                cartService.update(cartView.getItems());
            }
        } catch (StockException ex) {
            ex.getErrors().forEach((phoneId, message) ->
                    bindingResult.rejectValue(
                            String.format(ITEM_FIELD_TEMPLATE,phoneId),
                            OUT_OF_STOCK_CODE,
                            message
                    )
            );
        }

        if (bindingResult.hasErrors()) {
            cartView.setCart(cartService.getCart());

            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX + CART_VIEW_ATTRIBUTE,
                    bindingResult
            );
            redirectAttributes.addFlashAttribute(
                    CART_VIEW_ATTRIBUTE,
                    cartView
            );
        }

        return REDIRECT_CART_PAGE;
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{phoneId}")
    public String deleteCartItem(
            @PathVariable Long phoneId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            cartService.remove(phoneId);
        } catch (StockException ex) {
            redirectAttributes.addFlashAttribute(
                    DELETE_ERROR_ATTRIBUTE,
                    ex.getMessage()
            );
        }
        return REDIRECT_CART_PAGE;
    }
}
