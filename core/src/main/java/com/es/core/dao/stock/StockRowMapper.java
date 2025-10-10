package com.es.core.dao.stock;

import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockRowMapper implements RowMapper<Stock> {

    @Override
    public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
        Stock stock = new Stock();

        Phone phone = new Phone();
        phone.setId(getNullableLong(rs, "phoneId"));
        stock.setPhone(phone);

        stock.setStock(getNullableInteger(rs, "stock"));
        stock.setReserved(getNullableInteger(rs, "reserved"));

        return stock;
    }

    private Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private Integer getNullableInteger(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
}
