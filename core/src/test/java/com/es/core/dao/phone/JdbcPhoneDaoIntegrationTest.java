package com.es.core.dao.phone;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.model.phone.Phone;
import com.es.core.util.TableColumnsNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:context/applicationContext-coreTest.xml")
public class JdbcPhoneDaoIntegrationTest {

    private final JdbcPhoneDao jdbcPhoneDao;
    private final JdbcTemplate jdbcTemplate;
    private Phone basePhone;

    @Autowired
    public JdbcPhoneDaoIntegrationTest(
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
    void testFindAllReturnsPhonesWithoutSearch() {

        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                3L, "Apple", "iPhone 2", 1000);
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                4L, "Samsung", "Galaxy S23", 1200);

        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", 3L, 5, 0);
        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", 4L, 10, 0);

        Pageable pageable = new Pageable(0, 10, TableColumnsNames.ID, "asc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, null);

        assertEquals(2, page.content().size(), "Should return 2 phones");
        assertEquals(0, page.pageNumber(), "Page index should be 0");
        assertEquals(10, page.pageSize(), "Page size should be 10");
        assertEquals(2, page.totalElements(), "Total elements should be 2");
    }

    @Test
    void testFindAllWithSearchFiltersPhones() {
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                3L, "Nokia", "Nokia 3310", 50);
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                4L, "Apple", "iPhone 15", 1500);

        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", 3L, 20, 0);
        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", 4L, 15, 0);

        Pageable pageable = new Pageable(0, 10, TableColumnsNames.ID, "asc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, "nokia");

        assertEquals(1, page.content().size(), "Should return 1 phone with model containing 'nokia'");
        assertEquals("Nokia 3310", page.content().get(0).getModel());
    }

    @Test
    void testFindAllWithSorting() {
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                5L, "Apple", "iPhone 12", 800);
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                6L, "Samsung", "Galaxy S24", 1300);
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                7L, "Nokia", "3310", 50);

        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", 5L, 10, 0);
        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", 6L, 10, 0);
        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", 7L, 10, 0);

        Pageable pageable = new Pageable(0, 10, TableColumnsNames.Phone.PRICE, "desc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, null);

        assertEquals(3, page.content().size(), "Should return 3 phones");
        assertTrue(
                page.content().get(0).getPrice().compareTo(page.content().get(1).getPrice()) >= 0,
                "Phones should be sorted by price descending"
        );
    }

    @Test
    void testFindAllWithPagination() {
        for (long i = 5; i <= 15; i++) {
            jdbcTemplate.update(
                    "INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                    i, "Brand" + i, "Model" + i, 100 + i
            );

            jdbcTemplate.update(
                    "INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)",
                    i, 10, 0
            );
        }

        Pageable pageable = new Pageable(1, 5, TableColumnsNames.ID, "asc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, null);

        assertEquals(5, page.content().size(), "Should return 5 phones for page 1");
        assertEquals(1, page.pageNumber());
        assertEquals(5, page.pageSize());
        assertEquals(10L, page.content().get(0).getId(), "First phone on page 1 should have id=11");
    }


    @Test
    void testFindAllReturnsEmptyPageWhenNoPhones() {
        jdbcTemplate.update("DELETE FROM phone2color");
        jdbcTemplate.update("DELETE FROM phones");

        Pageable pageable = new Pageable(0, 10, TableColumnsNames.ID, "asc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, null);

        assertTrue(page.content().isEmpty(), "Content should be empty");
        assertEquals(0, page.totalElements());
    }

}
