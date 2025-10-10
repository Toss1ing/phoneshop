package com.es.core.dao.color;

import com.es.core.model.color.Color;
import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.ColorSql;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JdbcColorDao implements ColorDao {

    @Resource
    private ColorRowMapper colorRowMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public Set<Color> findColorsByPhoneId(Long phoneId) {
        List<Color> colorsByPhoneId = jdbcTemplate.query(
                ColorSql.SELECT_COLORS_BY_PHONE_ID,
                new Object[]{phoneId},
                colorRowMapper
        );

        return new HashSet<>(colorsByPhoneId);
    }

    @Override
    public Map<Long, Set<Color>> findColorsForPhoneIds(List<Long> phoneIds) {
        if (phoneIds.isEmpty()) {
            return new HashMap<>();
        }

        String inClause = phoneIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sqlColors = String.format(ColorSql.SELECT_COLORS_BY_PHONE_IDS, inClause);

        Map<Long, Set<Color>> colorsMap = new HashMap<>();
        jdbcTemplate.query(sqlColors, phoneIds.toArray(), rs -> {
            Long phoneId = rs.getLong(TableColumnsNames.Phone.PHONE_ID);

            Color color = new Color();
            color.setId(rs.getLong(TableColumnsNames.ID));
            color.setCode(rs.getString(TableColumnsNames.Color.CODE));

            colorsMap.computeIfAbsent(phoneId, k -> new HashSet<>()).add(color);
        });

        return colorsMap;
    }

    @Override
    public void saveColorsByPhoneId(Set<Color> colors, Long phoneId) {
        if (colors == null || colors.isEmpty()) {
            deleteAllColorsForPhone(phoneId);
            return;
        }

        Set<Long> existingColorIds = getExistingColorIds(phoneId);

        Set<Long> newColorIds = colors.stream()
                .map(Color::getCode)
                .map(this::findOrCreateColor)
                .collect(Collectors.toSet());

        Set<Long> toDelete = new HashSet<>(existingColorIds);
        toDelete.removeAll(newColorIds);

        Set<Long> toInsert = new HashSet<>(newColorIds);
        toInsert.removeAll(existingColorIds);

        if (!toDelete.isEmpty()) {
            deletePhoneColors(phoneId, toDelete);
        }

        insertPhoneColors(phoneId, toInsert);
    }

    private void deleteAllColorsForPhone(Long phoneId) {
        jdbcTemplate.update(ColorSql.DELETE_COLORS_BY_PHONE_ID, phoneId);
    }

    private Set<Long> getExistingColorIds(Long phoneId) {
        return new HashSet<>(jdbcTemplate.query(
                ColorSql.SELECT_EXIST_COLORS,
                new Object[]{phoneId},
                (rs, rowNum) -> rs.getLong(TableColumnsNames.Color.COLOR_ID)
        ));
    }

    private void deletePhoneColors(Long phoneId, Set<Long> colorIds) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(TableColumnsNames.Phone.PHONE_ID, phoneId);
        params.addValue(TableColumnsNames.Color.COLOR_IDS, colorIds);

        namedParameterJdbcTemplate.update(
                ColorSql.DELETE_PHONE_COLORS_BY_IDS,
                params
        );
    }

    private void insertPhoneColors(Long phoneId, Set<Long> colorIds) {
        for (Long colorId : colorIds) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(TableColumnsNames.Phone.PHONE_ID, phoneId);
            params.addValue(TableColumnsNames.Color.COLOR_ID, colorId);

            namedParameterJdbcTemplate.update(
                    ColorSql.INSERT_INTO_PHONE2COLOR,
                    params
            );
        }
    }

    private Long findOrCreateColor(String code) {
        Long id = jdbcTemplate.query(
                ColorSql.SELECT_EXIST_COLOR,
                new Object[]{code},
                rs -> rs.next() ? rs.getLong(TableColumnsNames.ID) : null
        );

        if (id != null) return id;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource(TableColumnsNames.Color.CODE, code);
        namedParameterJdbcTemplate.update(
                ColorSql.INSERT_COLOR,
                params,
                keyHolder,
                new String[]{TableColumnsNames.ID}
        );
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

}
