package com.es.core.dao.order;

import com.es.core.model.order.Order;
import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.OrderSql;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Objects;
import java.util.Optional;

public class JdbcOrderDao implements OrderDao {

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveOrder(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(order);

        namedParameterJdbcTemplate.update(
                OrderSql.INSERT_ORDER_ITEM,
                params,
                keyHolder,
                new String[]{TableColumnsNames.ID}
        );

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        order.setId(id);
    }

    @Override
    public Optional<Order> findOrderBySecureId(String secureId) {

        MapSqlParameterSource params = new MapSqlParameterSource(TableColumnsNames.Order.SECURE_ID, secureId);

        return namedParameterJdbcTemplate.query(
                OrderSql.SELECT_ORDER_BY_SECURE_ID,
                params,
                new OrderRowMapper()
        );
    }

}
