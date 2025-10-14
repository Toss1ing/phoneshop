package com.es.core.dao.phone;

import com.es.core.dao.color.ColorRowMapper;
import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.model.color.Color;
import com.es.core.model.phone.Phone;
import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.PhoneSql;
import com.es.core.util.sql.SqlParams;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class JdbcPhoneDao implements PhoneDao {

    @Resource
    private PhoneRowMapper phoneRowMapper;

    @Resource
    ColorRowMapper colorRowMapper;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<Phone> get(final Long key) {
        Map<Long, Phone> phoneMap = new HashMap<>();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(TableColumnsNames.Phone.PHONE_ID, key);

        namedParameterJdbcTemplate.query(
                PhoneSql.SELECT_PHONE_BY_ID_WITH_COLORS,
                params,
                rs -> {
                    long phoneId = rs.getLong(TableColumnsNames.ID);

                    Phone phone = phoneMap.get(phoneId);
                    if (phone == null) {
                        phone = phoneRowMapper.mapRow(rs, 0);
                        phoneMap.put(phoneId, phone);
                    }

                    Color color = colorRowMapper.mapRow(rs, 0);

                    if (color != null && color.getId() != null) {
                        phone.getColors().add(color);
                    }
                }
        );

        return phoneMap.values().stream().findFirst();
    }


    @Override
    public void save(final Phone phone) {
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(phone);

        if (phone.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(
                    PhoneSql.INSERT_PHONE,
                    params, keyHolder,
                    new String[]{TableColumnsNames.ID}
            );

            phone.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            namedParameterJdbcTemplate.update(PhoneSql.UPDATE_PHONE, params);
        }
    }

    @Override
    public Page<Phone> findAll(Pageable pageable, String search) {

        String sqlSelect = buildQuery(pageable, search, false);
        String sqlCount = buildQuery(pageable, search, true);

        MapSqlParameterSource selectParams = buildParams(pageable, search, false);
        MapSqlParameterSource countParams = buildParams(pageable, search, true);

        List<Phone> phones = namedParameterJdbcTemplate.query(
                sqlSelect,
                selectParams,
                phoneRowMapper
        );

        Long totalElements = namedParameterJdbcTemplate.queryForObject(
                sqlCount,
                countParams,
                Long.class
        );

        if (totalElements == null) {
            totalElements = 0L;
        }

        return new Page<>(
                phones,
                pageable.page(),
                pageable.size(),
                totalElements
        );
    }

    private String buildQuery(Pageable pageable, String search, boolean isCount) {
        StringBuilder sql = new StringBuilder();

        if (isCount) {
            sql.append(PhoneSql.COUNT_PHONES_WITH_AVAILABLE_STOCK_AND_PRICE);
        } else {
            sql.append(PhoneSql.SELECT_PHONES_WITH_AVAILABLE_STOCK_AND_PRICE);
        }

        if (isSearchPresent(search)) {
            sql.append(PhoneSql.SEARCH_BY_MODEL);
        }

        if (!isCount) {
            String orderBy = resolveSortOption(pageable.sortField()) + " " + pageable.sortOrder();
            sql.append(orderBy);

            sql.append(PhoneSql.PAGINATION);
        }
        return sql.toString();
    }

    private MapSqlParameterSource buildParams(Pageable pageable, String search, boolean isCount) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (isSearchPresent(search)) {
            params.addValue(SqlParams.SEARCH, "%" + search + "%");
        }

        if (!isCount) {
            int offset = pageable.page() * pageable.size();
            params.addValue(SqlParams.LIMIT, pageable.size());
            params.addValue(SqlParams.OFFSET, offset);
        }

        return params;
    }

    private boolean isSearchPresent(String search) {
        return search != null && !search.isBlank();
    }

    private String resolveSortOption(String sortField) {
        return switch (sortField) {
            case TableColumnsNames.Phone.BRAND -> PhoneSql.ORDER_BY_BRAND;
            case TableColumnsNames.Phone.MODEL -> PhoneSql.ORDER_BY_MODEL;
            case TableColumnsNames.Phone.PRICE -> PhoneSql.ORDER_BY_PRICE;
            case TableColumnsNames.Phone.DISPLAY_SIZE -> PhoneSql.ORDER_BY_DISPLAY_SIZE;
            default -> PhoneSql.ORDER_BY_ID;
        };
    }

}
