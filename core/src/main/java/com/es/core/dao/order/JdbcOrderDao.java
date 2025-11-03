package com.es.core.dao.order;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;
import com.es.core.util.ExceptionMessage;
import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.OrderSql;
import com.es.core.util.sql.SqlParams;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JdbcOrderDao implements OrderDao {

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Resource
    private OrderExtractor orderExtractor;

    @Resource
    private OrderRowMapper orderRowMapper;

    @Override
    public void saveOrder(Order order) {
        Objects.requireNonNull(order, ExceptionMessage.ORDER_EMPTY_MESSAGE);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(order);

        namedParameterJdbcTemplate.update(
                OrderSql.INSERT_ORDER,
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

        return Objects.requireNonNull(namedParameterJdbcTemplate.query(
                OrderSql.SELECT_ORDER_BY_SECURE_ID,
                params,
                orderExtractor
        )).stream().findFirst();
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {

        MapSqlParameterSource params = new MapSqlParameterSource(TableColumnsNames.ID, orderId);

        return Objects.requireNonNull(namedParameterJdbcTemplate.query(
                OrderSql.SELECT_ORDER_BY_ID,
                params,
                orderExtractor
        )).stream().findFirst();
    }

    @Override
    public Page<Order> findAllOrders(Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SqlParams.LIMIT, pageable.getSize())
                .addValue(SqlParams.OFFSET, pageable.getSize() * pageable.getPage());

        List<Order> orders = namedParameterJdbcTemplate.query(
                OrderSql.SELECT_ORDERS,
                params,
                orderRowMapper
        );

        long count = Optional.ofNullable(
                namedParameterJdbcTemplate.queryForObject(
                        OrderSql.COUNT_ORDERS,
                        new MapSqlParameterSource(),
                        Long.class
                )
        ).orElse(0L);

        return new Page<>(orders, pageable.getPage(), pageable.getSize(), count);
    }

    @Override
    public int updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(TableColumnsNames.ID, orderId)
                .addValue(TableColumnsNames.Order.STATUS, orderStatus.name());

        return namedParameterJdbcTemplate.update(OrderSql.UPDATE_ORDER_STATUS_BY_ID, params);
    }

}
