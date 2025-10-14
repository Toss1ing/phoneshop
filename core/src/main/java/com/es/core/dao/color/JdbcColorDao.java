package com.es.core.dao.color;

import com.es.core.model.color.Color;
import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.ColorSql;
import com.es.core.util.sql.SqlParams;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JdbcColorDao implements ColorDao {

    @Resource
    private ColorRowMapper colorRowMapper;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<Long, Set<Color>> findColorsForPhoneIds(List<Long> phoneIds) {
        if (phoneIds.isEmpty()) {
            return new HashMap<>();
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(SqlParams.PHONE_IDS, phoneIds);

        Map<Long, Set<Color>> colorsMap = new HashMap<>();

        namedParameterJdbcTemplate.query(
                ColorSql.SELECT_COLORS_BY_PHONE_IDS,
                params,
                rs -> {
                    Long phoneId = rs.getLong(TableColumnsNames.Phone.PHONE_ID);

                    Color color = new Color();
                    color.setId(rs.getLong(TableColumnsNames.ID));
                    color.setCode(rs.getString(TableColumnsNames.Color.CODE));

                    colorsMap.computeIfAbsent(phoneId, k -> new HashSet<>()).add(color);
                }
        );

        return colorsMap;
    }

    @Override
    @Transactional
    public void saveColorsByPhoneId(Set<Color> colors, Long phoneId) {
        if (colors == null || colors.isEmpty()) {
            deleteAllColorsForPhone(phoneId);
            return;
        }

        Set<Long> createdOrFoundColorsId = findOrCreateColors(
                colors.stream().map(Color::getCode).collect(Collectors.toSet())
        );

        Set<Long> existingColorIdsInPhone = getExistColorsPhone2Color(phoneId);

        Set<Long> colorIdsToDelete = new HashSet<>(existingColorIdsInPhone);
        colorIdsToDelete.removeAll(createdOrFoundColorsId);

        Set<Long> colorIdsToInsert = new HashSet<>(createdOrFoundColorsId);
        colorIdsToInsert.removeAll(existingColorIdsInPhone);

        deletePhoneColors(phoneId, colorIdsToDelete);
        insertPhoneColors(phoneId, colorIdsToInsert);
    }

    private void deleteAllColorsForPhone(Long phoneId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(TableColumnsNames.Phone.PHONE_ID, phoneId);

        namedParameterJdbcTemplate.update(
                ColorSql.DELETE_COLORS_BY_PHONE_ID,
                params
        );
    }

    private Set<Long> findOrCreateColors(Set<String> colorsCodeSet) {

        if (colorsCodeSet == null || colorsCodeSet.isEmpty()) {
            return Collections.emptySet();
        }

        MapSqlParameterSource params = new MapSqlParameterSource(
                SqlParams.COLORS_CODE,
                colorsCodeSet
        );
        List<Color> existingColors = namedParameterJdbcTemplate.query(
                ColorSql.SELECT_COLORS_BY_COLOR_CODES,
                params,
                colorRowMapper
        );

        Set<String> existingCodes = existingColors.stream()
                .map(Color::getCode)
                .collect(Collectors.toSet());

        Set<Long> existingIds = existingColors.stream()
                .map(Color::getId)
                .collect(Collectors.toSet());

        Set<String> colorsToInsert = new HashSet<>(colorsCodeSet);
        colorsToInsert.removeAll(existingCodes);

        if (!colorsToInsert.isEmpty()) {
            List<MapSqlParameterSource> batchParams = colorsToInsert.stream()
                    .map(code -> new MapSqlParameterSource(
                            TableColumnsNames.Color.CODE,
                            code
                    )).toList();

            namedParameterJdbcTemplate.batchUpdate(ColorSql.INSERT_COLORS,
                    batchParams.toArray(new MapSqlParameterSource[0]));

            MapSqlParameterSource newParams = new MapSqlParameterSource(
                    SqlParams.COLORS_CODE,
                    colorsToInsert
            );
            List<Color> insertedColors = namedParameterJdbcTemplate.query(
                    ColorSql.SELECT_COLORS_BY_COLOR_CODES,
                    newParams,
                    colorRowMapper
            );

            insertedColors.forEach(color -> existingIds.add(color.getId()));
        }

        return existingIds;
    }


    private Set<Long> getExistColorsPhone2Color(Long phoneId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(TableColumnsNames.Phone.PHONE_ID, phoneId);

        List<Long> colorIds = namedParameterJdbcTemplate.query(
                ColorSql.SELECT_COLORS_BY_PHONE_ID,
                params,
                (rs, rowNum) -> rs.getLong(TableColumnsNames.Color.COLOR_ID)
        );

        return new HashSet<>(colorIds);
    }


    private void deletePhoneColors(Long phoneId, Set<Long> toDelete) {
        if (toDelete.isEmpty()) {
            return;
        }

        List<MapSqlParameterSource> batchParams = toDelete.stream()
                .map(colorId -> new MapSqlParameterSource()
                        .addValue(TableColumnsNames.Color.COLOR_ID, colorId)
                        .addValue(TableColumnsNames.Phone.PHONE_ID, phoneId)
                ).toList();

        namedParameterJdbcTemplate.batchUpdate(
                ColorSql.DELETE_PHONE_COLOR_RELATION,
                batchParams.toArray(new MapSqlParameterSource[0])
        );

    }

    private void insertPhoneColors(Long phoneId, Set<Long> toInsert) {
        if (toInsert.isEmpty()) {
            return;
        }

        List<MapSqlParameterSource> batchParams = toInsert.stream()
                .map(colorId -> new MapSqlParameterSource()
                        .addValue(TableColumnsNames.Color.COLOR_ID, colorId)
                        .addValue(TableColumnsNames.Phone.PHONE_ID, phoneId)
                ).toList();

        namedParameterJdbcTemplate.batchUpdate(
                ColorSql.INSERT_PHONE_COLOR_RELATION,
                batchParams.toArray(new MapSqlParameterSource[0])
        );
    }

}
