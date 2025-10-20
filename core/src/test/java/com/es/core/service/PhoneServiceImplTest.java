package com.es.core.service;

import com.es.core.dao.color.ColorDao;
import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.dao.phone.PhoneDao;
import com.es.core.dto.PhoneDto;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.NotValidDataException;
import com.es.core.model.color.Color;
import com.es.core.model.phone.Phone;
import com.es.core.service.phone.PhoneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhoneServiceImplTest {

    @InjectMocks
    private PhoneServiceImpl phoneService;

    @Mock
    private PhoneDao phoneDao;

    @Mock
    private ColorDao colorDao;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllPhonesReturnsMappedDtos() {
        Phone phone1 = createPhone(1L, "Apple", "iPhone 14", BigDecimal.valueOf(1000),
                Set.of(createColor(1L, "BLACK")));
        Phone phone2 = createPhone(2L, "Samsung", "Galaxy S23", BigDecimal.valueOf(1200),
                Set.of(createColor(2L, "WHITE")));

        List<Phone> phones = List.of(phone1, phone2);
        Page<Phone> phonesPage = new Page<>(phones, 0, 10, 2L);

        when(phoneDao.findAll(any(Pageable.class), any())).thenReturn(phonesPage);

        Map<Long, Set<Color>> colorMap = Map.of(
                1L, Set.of(createColor(1L, "BLACK")),
                2L, Set.of(createColor(2L, "WHITE"))
        );
        when(colorDao.findColorsForPhoneIds(List.of(1L, 2L))).thenReturn(colorMap);

        Pageable pageable = new Pageable(0, 10, "id", "asc");

        Page<PhoneDto> result = phoneService.findAllPhones(pageable, null);

        assertEquals(2, result.content().size());
        assertEquals("Apple", result.content().get(0).getBrand());
        assertEquals("BLACK", result.content().get(0).getColors().iterator().next().getCode());
        assertEquals("Samsung", result.content().get(1).getBrand());
        assertEquals("WHITE", result.content().get(1).getColors().iterator().next().getCode());
    }

    @Test
    void testFindAllPhonesEmptyPageReturnsEmptyDtoPage() {
        Page<Phone> emptyPage = new Page<>(Collections.emptyList(), 0, 10, 0L);
        when(phoneDao.findAll(any(Pageable.class), any())).thenReturn(emptyPage);

        Pageable pageable = new Pageable(0, 10, "id", "asc");

        Page<PhoneDto> result = phoneService.findAllPhones(pageable, null);

        assertTrue(result.content().isEmpty());
        assertEquals(0L, result.totalElements());
    }

    @Test
    void testFindPhoneByIdReturnsPhoneWithColors() {
        Phone phone = createPhone(1L, "Apple", "iPhone 14", new BigDecimal(100), Set.of(createColor(1L, "BLACK")));
        when(phoneDao.get(1L)).thenReturn(Optional.of(phone));

        Phone result = phoneService.findPhoneById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getColors().size());
        assertEquals("BLACK", result.getColors().iterator().next().getCode());
    }

    @Test
    void testFindPhoneByIdNullIdThrowsNotFoundException() {
        assertThrows(NotValidDataException.class, () -> phoneService.findPhoneById(null));
    }

    @Test
    void testFindPhoneByIdNotFoundThrowsNotFoundException() {
        when(phoneDao.get(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> phoneService.findPhoneById(999L));
    }

    @Test
    void testSaveCallsDaos() {
        Phone phone = createPhone(null, "Apple", "iPhone 14", new BigDecimal(100), Set.of(createColor(1L, "BLACK")));

        phoneService.save(phone);

        verify(phoneDao).save(phone);
        verify(colorDao).saveColorsByPhoneId(phone.getColors(), phone.getId());
    }


    private Phone createPhone(Long id, String brand, String model, BigDecimal price, Set<Color> colors) {
        Phone phone = new Phone();
        phone.setId(id);
        phone.setBrand(brand);
        phone.setModel(model);
        phone.setPrice(price);
        if (colors != null) {
            phone.setColors(colors);
        }
        return phone;
    }

    private Color createColor(Long id, String code) {
        return new Color(id, code);
    }

}
