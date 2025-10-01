package com.es.core.dao.phone;

import com.es.core.model.color.Color;
import com.es.core.model.phone.Phone;
import com.es.core.util.PhoneSql;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (phones.isEmpty()) {
            return Optional.empty();
        }

        Phone phone = phones.get(0);

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
    public List<Phone> findAll(int offset, int limit) {
        List<Phone> phones = jdbcTemplate.query(PhoneSql.SELECT_PHONES_PAGINATED, new Object[]{limit, offset}, phoneRowMapper);

        if (phones.isEmpty()) {
            return phones;
        }

        List<Long> phoneIds = phones.stream().map(Phone::getId).toList();
        Map<Long, Set<Color>> colorsForPhoneMap = getColorsForPhones(phoneIds);

        for (Phone phone : phones) {
            phone.setColors(colorsForPhoneMap.getOrDefault(phone.getId(), new HashSet<>()));
        }

        return phones;
    }

    private Map<Long, Set<Color>> getColorsForPhones(List<Long> phoneIds) {
        if (phoneIds.isEmpty()) {
            return new HashMap<>();
        }

        String inClause = phoneIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sqlColors = String.format(PhoneSql.SELECT_COLORS_BY_PHONE_IDS, inClause);

        Map<Long, Set<Color>> colorsMap = new HashMap<>();
        jdbcTemplate.query(sqlColors, phoneIds.toArray(), rs -> {
            Long phoneId = rs.getLong("phoneId");

            Color color = new Color();
            color.setId(rs.getLong("id"));
            color.setCode(rs.getString("code"));

            colorsMap.computeIfAbsent(phoneId, k -> new HashSet<>()).add(color);
        });

        return colorsMap;
    }
}
