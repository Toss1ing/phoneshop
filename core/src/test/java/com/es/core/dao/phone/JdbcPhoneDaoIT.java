package com.es.core.dao.phone;

import com.es.core.model.phone.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:context/applicationContext-coreTest.xml")
public class JdbcPhoneDaoIT {

    private final JdbcPhoneDao jdbcPhoneDao;
    private final JdbcTemplate jdbcTemplate;
    private Phone basePhone;

    @Autowired
    public JdbcPhoneDaoIT(
            JdbcPhoneDao jdbcPhoneDao,
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcPhoneDao = jdbcPhoneDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setup() {
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                1L, "Apple", "iPhone 14", 1000);

        jdbcTemplate.update("INSERT INTO colors (id, code) VALUES (?, ?)", 1L, "BLACK");
        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", 1L, 1L);

        basePhone = new Phone();
        basePhone.setBrand("TestBrand");
        basePhone.setModel("TestModel");
        basePhone.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void testGetExistingPhoneReturnsPhoneWithColors() {
        Optional<Phone> phoneOpt = jdbcPhoneDao.get(1L);
        assertTrue(phoneOpt.isPresent(), "Phone should be present");
        assertFalse(phoneOpt.get().getColors().isEmpty(), "Colors should be present");
    }

    @Test
    void testGetExistingPhoneReturnsPhoneWithoutColors() {
        jdbcTemplate.update("DELETE FROM colors WHERE id = ?", 1L);
        Optional<Phone> phoneOpt = jdbcPhoneDao.get(1L);
        assertTrue(phoneOpt.isPresent(), "Phone should be present");
        assertTrue(phoneOpt.get().getColors().isEmpty(), "Colors should be empty");
    }

    @Test
    void testGetNotExistingPhoneReturnsEmptyOptional() {
        Optional<Phone> phoneOpt = jdbcPhoneDao.get(100L);
        assertTrue(phoneOpt.isEmpty(), "Optional should be empty");
    }

    @Test
    void testSaveNewPhoneWithoutColors() {
        jdbcPhoneDao.save(basePhone);

        assertNotNull(basePhone.getId(), "Phone must be have an id");

        Optional<Phone> phoneFromDb = jdbcPhoneDao.get(basePhone.getId());
        assertTrue(phoneFromDb.isPresent(), "Phone should be present in database");
        assertEquals("TestBrand", phoneFromDb.get().getBrand());
        assertEquals("TestModel", phoneFromDb.get().getModel());
        assertEquals(0, phoneFromDb.get().getPrice().compareTo(BigDecimal.valueOf(100)),
                "Price should be equals");
    }

    @Test
    void testSaveUpdatesExistingPhone() {
        jdbcPhoneDao.save(basePhone);
        Long phoneId = basePhone.getId();

        basePhone.setBrand("UpdatedBrand");
        basePhone.setModel("UpdatedModel");
        basePhone.setPrice(BigDecimal.valueOf(200));

        jdbcPhoneDao.save(basePhone);

        Optional<Phone> phoneFromDb = jdbcPhoneDao.get(phoneId);
        assertTrue(phoneFromDb.isPresent(), "Phone should be present");
        assertEquals("UpdatedBrand", phoneFromDb.get().getBrand());
        assertEquals("UpdatedModel", phoneFromDb.get().getModel());
        assertEquals(0, phoneFromDb.get().getPrice().compareTo(BigDecimal.valueOf(200)),
                "Price should be updated");
    }

    @Test
    void testFindAllReturnsPhonesWithColors() {
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                2L, "Samsung", "Galaxy S23", 1200);
        jdbcTemplate.update("INSERT INTO colors (id, code) VALUES (?, ?)", 2L, "WHITE");
        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", 2L, 2L);

        List<Phone> phones = jdbcPhoneDao.findAll(0, 10);

        Optional<Phone> phoneWithColor = phones.stream()
                .filter(p -> p.getId().equals(2L))
                .findFirst();

        assertTrue(phoneWithColor.isPresent());
        assertEquals(1, phoneWithColor.get().getColors().size());
        assertTrue(phoneWithColor.get().getColors().stream()
                .anyMatch(c -> c.getCode().equals("WHITE")));
    }

    @Test
    void testFindAllReturnsPhonesWithoutColors() {
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                3L, "Nokia", "3310", 50);

        List<Phone> phones = jdbcPhoneDao.findAll(0, 10);

        Optional<Phone> phoneWithoutColor = phones.stream()
                .filter(p -> p.getId().equals(3L))
                .findFirst();

        assertTrue(phoneWithoutColor.isPresent());
        assertTrue(phoneWithoutColor.get().getColors().isEmpty());
    }

    @Test
    void testFindAllReturnsEmptyListWhenNoPhones() {
        jdbcTemplate.update("DELETE FROM phone2color");
        jdbcTemplate.update("DELETE FROM phones");

        List<Phone> phones = jdbcPhoneDao.findAll(0, 10);

        assertTrue(phones.isEmpty(), "List of phones should be empty");
    }

}
