package com.es.core.dao.orderItem;

import com.es.core.model.order.OrderItem;
import com.es.core.model.phone.Phone;
import com.es.core.dao.phone.PhoneRowMapper;
import com.es.core.util.TableColumnsNames;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRowMapper implements RowMapper<OrderItem> {

    private final PhoneRowMapper phoneRowMapper;

    public OrderItemRowMapper(PhoneRowMapper phoneRowMapper) {
        this.phoneRowMapper = phoneRowMapper;
    }

    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem orderItem = new OrderItem();

        Phone phone = phoneRowMapper.mapRow(rs, rowNum);
        orderItem.setPhone(phone);

        orderItem.setQuantity(rs.getInt(TableColumnsNames.OrderItem.QUANTITY));

        return orderItem;
    }
}
