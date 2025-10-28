package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.dto.PhoneDto;
import com.es.core.service.phone.PhoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductListPageControllerTest {

    @Mock
    private PhoneService phoneService;

    @Mock
    private Model model;

    @InjectMocks
    private ProductListPageController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowProductListDefaultParams() {
        PhoneDto phoneDto = new PhoneDto(
                1L,
                null,
                "Apple",
                "iPhone",
                Set.of(),
                BigDecimal.valueOf(6.1),
                BigDecimal.valueOf(1000)
        );
        Page<PhoneDto> page = new Page<>(List.of(phoneDto), 0, 10, 1L);

        when(phoneService.findAllPhones(any(Pageable.class), eq(""))).thenReturn(page);

        String view = controller.showProductList(0, 10, null, null, null, model);

        assertEquals("productList", view);

        verify(model).addAttribute("phones", page.content());
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("totalPages", page.getTotalPages());
        verify(model).addAttribute("pageSize", 10);
    }

    @Test
    void testShowProductListWithSearchAndSorting() {
        PhoneDto phoneDto = new PhoneDto(
                2L,
                null,
                "Samsung",
                "Galaxy S23",
                Set.of(),
                BigDecimal.valueOf(6.8),
                BigDecimal.valueOf(1200)
        );
        Page<PhoneDto> page = new Page<>(List.of(phoneDto), 0, 5, 1L);

        when(phoneService.findAllPhones(any(Pageable.class), eq("galaxy"))).thenReturn(page);

        String view = controller.showProductList(0, 5, "brand", "desc", "GALAXY", model);

        assertEquals("productList", view);
        verify(model).addAttribute("phones", page.content());
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("totalPages", page.getTotalPages());
        verify(model).addAttribute("pageSize", 5);
    }
}
