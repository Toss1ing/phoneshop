package com.es.phoneshop.web.controller.pages;

import com.es.core.model.phone.Phone;
import com.es.core.service.phone.PhoneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/productDetails")
public class ProductDetailsPageController {

    private final PhoneService phoneService;

    public ProductDetailsPageController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    private static final String PRODUCT_DETAILS_PAGE_NAME = "productDetails";

    private static final String PHONE_ATTRIBUTE_NAME = "phone";

    @RequestMapping(method = RequestMethod.GET, path = "/{phoneId}")
    public String getProductDetails(
            @PathVariable Long phoneId,
            Model model
    ) {
        Phone phone = phoneService.findPhoneById(phoneId);

        model.addAttribute(PHONE_ATTRIBUTE_NAME, phone);

        return PRODUCT_DETAILS_PAGE_NAME;
    }

}
