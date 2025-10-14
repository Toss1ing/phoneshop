package com.es.core.dao.color;

import com.es.core.model.color.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:context/applicationContext-coreTest.xml")
public class JdbcColorDaoIntegrationTest {

    @Autowired
    private JdbcColorDao jdbcColorDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long phoneId1;
    private Long phoneId2;

    @BeforeEach
    void setup() {
        clearTables();
        phoneId1 = insertPhone("Apple", "iPhone 14", 1000);
        phoneId2 = insertPhone("Samsung", "Galaxy S23", 1200);
    }

    @Test
    void testSaveColorsByPhoneIdInsertsNewColors() {
        Set<Color> colors = Set.of(
                new Color(null, "BLACK"),
                new Color(null, "WHITE")
        );

        jdbcColorDao.saveColorsByPhoneId(colors, phoneId1);

        Set<String> codes = getColorCodesForPhone(phoneId1);
        assertEquals(Set.of("BLACK", "WHITE"), codes, "Colors should be inserted correctly");
    }

    @Test
    void testSaveColorsByPhoneIdUpdatesExistingColors() {
        Long blackId = insertColor("BLACK");
        Long whiteId = insertColor("WHITE");
        insertPhone2Color(phoneId1, blackId);
        insertPhone2Color(phoneId1, whiteId);

        Set<Color> updatedColors = Set.of(
                new Color(null, "WHITE"),
                new Color(null, "RED")
        );

        jdbcColorDao.saveColorsByPhoneId(updatedColors, phoneId1);

        Set<String> codes = getColorCodesForPhone(phoneId1);
        assertEquals(Set.of("WHITE", "RED"), codes, "Colors should be updated correctly");
    }

    @Test
    void testSaveColorsByPhoneIdDeletesAllWhenEmpty() {
        Long blackId = insertColor("BLACK");
        insertPhone2Color(phoneId1, blackId);

        jdbcColorDao.saveColorsByPhoneId(Collections.emptySet(), phoneId1);

        Set<String> codes = getColorCodesForPhone(phoneId1);
        assertTrue(codes.isEmpty(), "All colors should be deleted");
    }

    @Test
    void testFindColorsForPhoneIds() {
        Long blackId = insertColor("BLACK");
        Long redId = insertColor("RED");

        insertPhone2Color(phoneId1, blackId);
        insertPhone2Color(phoneId2, redId);

        Map<Long, Set<Color>> map = jdbcColorDao.findColorsForPhoneIds(Arrays.asList(phoneId1, phoneId2));

        assertEquals(2, map.size());
        assertEquals(Set.of("BLACK"), map.get(phoneId1).stream().map(Color::getCode).collect(Collectors.toSet()));
        assertEquals(Set.of("RED"), map.get(phoneId2).stream().map(Color::getCode).collect(Collectors.toSet()));
    }

    @Test
    void testFindColorsForPhoneIdsReturnsEmptyMapForNoPhones() {
        Map<Long, Set<Color>> map = jdbcColorDao.findColorsForPhoneIds(Collections.emptyList());
        assertTrue(map.isEmpty(), "Map should be empty when no phone IDs passed");
    }


    private void clearTables() {
        jdbcTemplate.update("DELETE FROM phone2color");
        jdbcTemplate.update("DELETE FROM colors");
        jdbcTemplate.update("DELETE FROM phones");
    }

    private Long insertPhone(String brand, String model, int price) {
        jdbcTemplate.update("INSERT INTO phones (brand, model, price) VALUES (?, ?, ?)", brand, model, price);
        return jdbcTemplate.queryForObject("SELECT id FROM phones WHERE brand=? AND model=?", Long.class, brand, model);
    }

    private Long insertColor(String code) {
        jdbcTemplate.update("INSERT INTO colors (code) VALUES (?)", code);
        return jdbcTemplate.queryForObject("SELECT id FROM colors WHERE code=?", Long.class, code);
    }

    private void insertPhone2Color(Long phoneId, Long colorId) {
        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", phoneId, colorId);
    }

    private Set<String> getColorCodesForPhone(Long phoneId) {
        List<String> codes = jdbcTemplate.queryForList(
                "SELECT c.code FROM colors c JOIN phone2color p2c ON c.id = p2c.colorId WHERE p2c.phoneId = ?",
                String.class,
                phoneId
        );
        return new HashSet<>(codes);
    }
}
