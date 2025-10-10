package com.es.core.dao.phone;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.model.color.Color;
import com.es.core.model.phone.Phone;
import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.PhoneSql;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class JdbcPhoneDao implements PhoneDao {

    @Resource
    private PhoneRowMapper phoneRowMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<Phone> get(final Long key) {
        List<Phone> phones = jdbcTemplate.query(PhoneSql.SELECT_PHONE_BY_ID, new Object[]{key}, phoneRowMapper);

        Phone phone = phones.stream().findFirst().orElse(null);

        if (phone == null) {
            return Optional.empty();
        }

        List<Color> colors = jdbcTemplate.query(
                PhoneSql.SELECT_COLORS_BY_PHONE_ID,
                new Object[]{key},
                new BeanPropertyRowMapper<>(Color.class)
        );

        phone.setColors(new HashSet<>(colors));

        return Optional.of(phone);
    }

    @Override
    public void save(final Phone phone) {
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(phone);

        if (phone.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(PhoneSql.INSERT_PHONE, params, keyHolder, new String[]{"id"});
            phone.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            namedParameterJdbcTemplate.update(PhoneSql.UPDATE_PHONE, params);
        }
    }

    @Override
    public Page<Phone> findAll(Pageable pageable, String search) {
        int offset = pageable.page() * pageable.size();

        StringBuilder sqlSelect = new StringBuilder(PhoneSql.SELECT_PHONES_WITH_AVAILABLE_STOCK_AND_PRICE);
        StringBuilder sqlCount = new StringBuilder(PhoneSql.COUNT_PHONES_WITH_AVAILABLE_STOCK_AND_PRICE);

        List<Object> selectParams = new ArrayList<>();
        List<Object> countParams = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sqlSelect.append(PhoneSql.SEARCH_BY_MODEL);
            sqlCount.append(PhoneSql.SEARCH_BY_MODEL);

            String searchPattern = "%" + search.toLowerCase() + "%";
            selectParams.add(searchPattern);
            countParams.add(searchPattern);
        }

        String orderBy = resolveSortOption(pageable.sortField()) + " " + pageable.sortOrder();
        sqlSelect.append(orderBy);

        sqlSelect.append(PhoneSql.PAGINATION);
        selectParams.add(pageable.size());
        selectParams.add(offset);

        List<Phone> phones = jdbcTemplate.query(
                sqlSelect.toString(),
                selectParams.toArray(),
                phoneRowMapper
        );

        Long totalElements = jdbcTemplate.queryForObject(
                sqlCount.toString(),
                countParams.toArray(),
                Long.class
        );

        if (totalElements == null) {
            totalElements = 0L;
        }

        return new Page<>(phones, pageable.page(), pageable.size(), totalElements);
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
