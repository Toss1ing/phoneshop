package com.es.core.dao.order;

import com.es.core.model.order.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderRowMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {

        Order order = OrderMapperUtil.mapBaseOrder(rs);
        order.setOrderItems(new ArrayList<>());

        return order;
    }
}
