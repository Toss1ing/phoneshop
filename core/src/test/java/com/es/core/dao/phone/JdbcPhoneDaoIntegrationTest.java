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
    public JdbcPhoneDaoIntegrationTest(JdbcPhoneDao jdbcPhoneDao, JdbcTemplate jdbcTemplate) {
        this.jdbcPhoneDao = jdbcPhoneDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setup() {
        insertPhone(1L, "Apple", "iPhone 14", 1000);
        insertColor(1L, "BLACK");
        linkPhoneColor(1L, 1L);

        basePhone = createTestPhone();
    }

    @Test
    void testGetExistingPhoneReturnsPhoneWithColors() {
        Optional<Phone> phoneOpt = jdbcPhoneDao.get(1L);
        assertTrue(phoneOpt.isPresent());
        assertFalse(phoneOpt.get().getColors().isEmpty());
    }

    @Test
    void testGetExistingPhoneReturnsPhoneWithoutColors() {
        jdbcTemplate.update("DELETE FROM colors WHERE id = ?", 1L);
        Optional<Phone> phoneOpt = jdbcPhoneDao.get(1L);
        assertTrue(phoneOpt.isPresent());
        System.out.println(phoneOpt.get().getColors().size());
        assertTrue(phoneOpt.get().getColors().isEmpty());
    }

    @Test
    void testGetNotExistingPhoneReturnsEmptyOptional() {
        Optional<Phone> phoneOpt = jdbcPhoneDao.get(100L);
        assertTrue(phoneOpt.isEmpty());
    }

    @Test
    void testSaveNewPhoneWithoutColors() {
        jdbcPhoneDao.save(basePhone);
        assertNotNull(basePhone.getId());

        Optional<Phone> phoneFromDb = jdbcPhoneDao.get(basePhone.getId());
        assertTrue(phoneFromDb.isPresent());
        assertEquals("TestBrand", phoneFromDb.get().getBrand());
        assertEquals("TestModel", phoneFromDb.get().getModel());
        assertEquals(0, phoneFromDb.get().getPrice().compareTo(BigDecimal.valueOf(100)));
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
        assertTrue(phoneFromDb.isPresent());
        assertEquals("UpdatedBrand", phoneFromDb.get().getBrand());
        assertEquals("UpdatedModel", phoneFromDb.get().getModel());
        assertEquals(0, phoneFromDb.get().getPrice().compareTo(BigDecimal.valueOf(200)));
    }

    @Test
    void testFindAllReturnsPhonesWithoutSearch() {
        insertPhone(3L, "Apple", "iPhone 2", 1000);
        insertPhone(4L, "Samsung", "Galaxy S23", 1200);
        insertStock(3L, 5, 0);
        insertStock(4L, 10, 0);

        Pageable pageable = new Pageable(0, 10, TableColumnsNames.ID, "asc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, null);

        assertEquals(2, page.content().size());
        assertEquals(0, page.pageNumber());
        assertEquals(10, page.pageSize());
        assertEquals(2, page.totalElements());
    }

    @Test
    void testFindAllWithSearchFiltersPhones() {
        insertPhone(3L, "Nokia", "Nokia 3310", 50);
        insertPhone(4L, "Apple", "iPhone 15", 1500);
        insertStock(3L, 20, 0);
        insertStock(4L, 15, 0);

        Pageable pageable = new Pageable(0, 10, TableColumnsNames.ID, "asc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, "nokia");

        assertEquals(1, page.content().size());
        assertEquals("Nokia 3310", page.content().get(0).getModel());
    }

    @Test
    void testFindAllWithSorting() {
        insertPhone(5L, "Apple", "iPhone 12", 800);
        insertPhone(6L, "Samsung", "Galaxy S24", 1300);
        insertPhone(7L, "Nokia", "3310", 50);
        insertStock(5L, 10, 0);
        insertStock(6L, 10, 0);
        insertStock(7L, 10, 0);

        Pageable pageable = new Pageable(0, 10, TableColumnsNames.Phone.PRICE, "desc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, null);

        assertEquals(3, page.content().size());
        assertTrue(page.content().get(0).getPrice().compareTo(page.content().get(1).getPrice()) >= 0);
    }

    @Test
    void testFindAllWithPagination() {
        for (long i = 5; i <= 15; i++) {
            insertPhone(i, "Brand" + i, "Model" + i, 100 + i);
            insertStock(i, 10, 0);
        }

        Pageable pageable = new Pageable(1, 5, TableColumnsNames.ID, "asc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, null);

        assertEquals(5, page.content().size());
        assertEquals(1, page.pageNumber());
        assertEquals(5, page.pageSize());
        assertEquals(10L, page.content().get(0).getId());
    }

    @Test
    void testFindAllReturnsEmptyPageWhenNoPhones() {
        clearTables();

        Pageable pageable = new Pageable(0, 10, TableColumnsNames.ID, "asc");
        Page<Phone> page = jdbcPhoneDao.findAll(pageable, null);

        assertTrue(page.content().isEmpty());
        assertEquals(0, page.totalElements());
    }


    private void clearTables() {
        jdbcTemplate.update("DELETE FROM phone2color");
        jdbcTemplate.update("DELETE FROM colors");
        jdbcTemplate.update("DELETE FROM phones");
        jdbcTemplate.update("DELETE FROM stocks");
    }

    private void insertPhone(long id, String brand, String model, double price) {
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                id, brand, model, price);
    }

    private void insertColor(long id, String code) {
        jdbcTemplate.update("INSERT INTO colors (id, code) VALUES (?, ?)", id, code);
    }

    private void linkPhoneColor(long phoneId, long colorId) {
        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", phoneId, colorId);
    }

    private void insertStock(long phoneId, int stock, int reserved) {
        jdbcTemplate.update("INSERT INTO stocks (phoneId, stock, reserved) VALUES (?, ?, ?)", phoneId, stock, reserved);
    }

    private Phone createTestPhone() {
        Phone phone = new Phone();
        phone.setBrand("TestBrand");
        phone.setModel("TestModel");
        phone.setPrice(BigDecimal.valueOf(100));
        return phone;
    }
}
