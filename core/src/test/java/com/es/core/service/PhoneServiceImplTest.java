package com.es.core.service;

import com.es.core.dao.color.ColorDao;
import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.dao.phone.PhoneDao;
import com.es.core.dto.PhoneDto;
import com.es.core.exception.NotFoundException;
import com.es.core.model.color.Color;
import com.es.core.model.phone.Phone;
import com.es.core.service.phone.PhoneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

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
        Phone phone1 = new Phone();
        phone1.setId(1L);
        phone1.setBrand("Apple");
        phone1.setModel("iPhone 14");
        phone1.setPrice(BigDecimal.valueOf(1000));

        Phone phone2 = new Phone();
        phone2.setId(2L);
        phone2.setBrand("Samsung");
        phone2.setModel("Galaxy S23");
        phone2.setPrice(BigDecimal.valueOf(1200));

        List<Phone> phones = List.of(phone1, phone2);
        Page<Phone> phonesPage = new Page<>(phones, 0, 10, 2L);

        when(phoneDao.findAll(any(Pageable.class), any())).thenReturn(phonesPage);

        Map<Long, Set<Color>> colorMap = Map.of(
                1L, Set.of(new Color(1L, "BLACK")),
                2L, Set.of(new Color(2L, "WHITE"))
        );

        when(colorDao.findColorsForPhoneIds(List.of(1L, 2L))).thenReturn(colorMap);

        Pageable pageable = new Pageable(0, 10, "id", "asc");

        Page<PhoneDto> result = phoneService.findAllPhones(pageable, null);

        assertEquals(2, result.content().size());
        assertEquals("Apple", result.content().get(0).getBrand());
        assertEquals(1, result.content().get(0).getColors().size());
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
        Phone phone = new Phone();
        phone.setId(1L);
        phone.setBrand("Apple");
        phone.setModel("iPhone 14");

        when(phoneDao.get(1L)).thenReturn(Optional.of(phone));
        when(colorDao.findColorsByPhoneId(1L)).thenReturn(Set.of(new Color(1L, "BLACK")));

        Phone result = phoneService.findPhoneById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getColors().size());
        assertEquals("BLACK", result.getColors().iterator().next().getCode());
    }

    @Test
    void testFindPhoneByIdNullIdThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> phoneService.findPhoneById(null));
    }

    @Test
    void testFindPhoneByIdNotFoundThrowsNotFoundException() {
        when(phoneDao.get(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> phoneService.findPhoneById(999L));
    }

    @Test
    void testSaveCallsDaos() {
        Phone phone = new Phone();
        phone.setId(null);
        phone.setColors(Set.of(new Color(1L, "BLACK")));

        phoneService.save(phone);

        verify(phoneDao).save(phone);
        verify(colorDao).saveColorsByPhoneId(phone.getColors(), phone.getId());
    }
}
