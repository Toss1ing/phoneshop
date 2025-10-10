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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        jdbcTemplate.update("DELETE FROM phone2color");
        jdbcTemplate.update("DELETE FROM colors");
        jdbcTemplate.update("DELETE FROM phones");

        phoneId1 = 1L;
        phoneId2 = 2L;

        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                phoneId1, "Apple", "iPhone 14", 1000);
        jdbcTemplate.update("INSERT INTO phones (id, brand, model, price) VALUES (?, ?, ?, ?)",
                phoneId2, "Samsung", "Galaxy S24", 1300);
    }

    @Test
    void testSaveColorsByPhoneIdInsertsAndUpdatesCorrectly() {
        Set<Color> colors = Set.of(
                new Color(null, "BLACK"),
                new Color(null, "WHITE")
        );

        jdbcColorDao.saveColorsByPhoneId(colors, phoneId1);

        List<Map<String, Object>> phone2colorRows =
                jdbcTemplate.queryForList("SELECT * FROM phone2color WHERE phoneId = ?", phoneId1);
        assertEquals(2, phone2colorRows.size(), "Should have 2 colors linked to phone");

        Set<Color> updatedColors = Set.of(
                new Color(null, "WHITE"),
                new Color(null, "RED")
        );
        jdbcColorDao.saveColorsByPhoneId(updatedColors, phoneId1);

        List<String> colorCodes = jdbcTemplate.query(
                "SELECT c.code FROM colors c JOIN phone2color p2c ON c.id = p2c.colorId WHERE p2c.phoneId = ?",
                (rs, i) -> rs.getString("code"),
                phoneId1
        );

        assertEquals(Set.of("WHITE", "RED"), new HashSet<>(colorCodes),
                "Phone should now have updated color set");
    }

    @Test
    void testFindColorsByPhoneIdReturnsCorrectColors() {
        Long colorBlackId = insertColor("BLACK");
        Long colorWhiteId = insertColor("WHITE");

        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", phoneId1, colorBlackId);
        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", phoneId1, colorWhiteId);

        Set<Color> colors = jdbcColorDao.findColorsByPhoneId(phoneId1);

        Set<String> colorCodes = colors.stream().map(Color::getCode).collect(Collectors.toSet());
        assertEquals(Set.of("BLACK", "WHITE"), colorCodes, "Should return both colors for phone");
    }

    @Test
    void testFindColorsForPhoneIdsReturnsColorsGroupedByPhone() {
        Long redId = insertColor("RED");
        Long blueId = insertColor("BLUE");
        Long greenId = insertColor("GREEN");

        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", phoneId1, redId);
        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", phoneId1, blueId);
        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", phoneId2, greenId);

        Map<Long, Set<Color>> map = jdbcColorDao.findColorsForPhoneIds(List.of(phoneId1, phoneId2));

        assertEquals(2, map.size(), "Should return colors for 2 phones");
        assertEquals(Set.of("RED", "BLUE"), map.get(phoneId1).stream().map(Color::getCode).collect(Collectors.toSet()));
        assertEquals(Set.of("GREEN"), map.get(phoneId2).stream().map(Color::getCode).collect(Collectors.toSet()));
    }

    @Test
    void testSaveColorsByPhoneIdDeletesAllWhenEmptySetPassed() {
        Long blackId = insertColor("BLACK");
        jdbcTemplate.update("INSERT INTO phone2color (phoneId, colorId) VALUES (?, ?)", phoneId1, blackId);

        jdbcColorDao.saveColorsByPhoneId(Collections.emptySet(), phoneId1);

        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM phone2color WHERE phoneId = ?",
                Integer.class,
                phoneId1
        );
        assertEquals(0, count, "Should delete all colors for phone");
    }

    @Test
    void testFindOrCreateColorCreatesNewIfNotExists() {
        Long colorId = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM colors WHERE code = 'PINK'",
                Long.class
        );
        assertEquals(0L, colorId, "PINK color should not exist yet");

        jdbcColorDao.saveColorsByPhoneId(Set.of(new Color(null, "PINK")), phoneId1);

        Long pinkCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM colors WHERE code = 'PINK'",
                Long.class
        );
        assertEquals(1L, pinkCount, "Should create new color in DB");
    }

    private Long insertColor(String code) {
        jdbcTemplate.update("INSERT INTO colors (code) VALUES (?)", code);
        return jdbcTemplate.queryForObject("SELECT id FROM colors WHERE code = ?", Long.class, code);
    }
}
