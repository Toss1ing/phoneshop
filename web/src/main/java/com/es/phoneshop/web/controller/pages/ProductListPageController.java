package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.dto.PhoneDto;
import com.es.core.service.phone.PhoneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/productList")
public class ProductListPageController {

    private final PhoneService phoneService;

    public ProductListPageController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    private static final String ID_NAME = "id";
    private static final String BRAND_NAME = "brand";
    private static final String MODEL_NAME = "model";
    private static final String PRICE_NAME = "price";
    private static final String DISPLAY_SIZE_NAME = "displaySizeInches";

    private static final String SORT_ASC_NAME = "asc";
    private static final String SORT_DESC_NAME = "desc";

    private static final String PHONES_ATTRIBUTE_NAME = "phones";
    private static final String CURRENT_PAGE_ATTRIBUTE_NAME = "currentPage";
    private static final String TOTAL_PAGES_ATTRIBUTE_NAME = "totalPages";
    private static final String PAGE_SIZE_ATTRIBUTE_NAME = "pageSize";

    private static final String PRODUCT_LIST_PAGE_NAME = "productList";


    @GetMapping("")
    public String showProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String search,
            Model model
    ) {

        Pageable pageable = new Pageable(
                validatePage(page),
                size,
                validateSortField(sortField),
                validateSortOrder(sortOrder)
        );

        String validateSearch = validateSearch(search);
        Page<PhoneDto> phones = phoneService.findAllPhones(pageable, validateSearch);

        model.addAttribute(PHONES_ATTRIBUTE_NAME, phones.content());
        model.addAttribute(CURRENT_PAGE_ATTRIBUTE_NAME, page);
        model.addAttribute(TOTAL_PAGES_ATTRIBUTE_NAME, phones.getTotalPages());
        model.addAttribute(PAGE_SIZE_ATTRIBUTE_NAME, size);

        return PRODUCT_LIST_PAGE_NAME;
    }

    private int validatePage(int page) {
        return Math.max(page, 0);
    }

    private String validateSortField(String sortField) {
        if (sortField == null) return ID_NAME;

        return switch (sortField) {
            case PRICE_NAME, BRAND_NAME, MODEL_NAME, DISPLAY_SIZE_NAME -> sortField;
            default -> ID_NAME;
        };
    }

    private String validateSortOrder(String sortOrder) {
        if (sortOrder == null) return SORT_ASC_NAME;

        return switch (sortOrder.toLowerCase()) {
            case SORT_ASC_NAME, SORT_DESC_NAME -> sortOrder.toUpperCase();
            default -> SORT_ASC_NAME.toUpperCase();
        };
    }

    private String validateSearch(String search) {
        return search == null ? "" : search.trim().toLowerCase();
    }

}
