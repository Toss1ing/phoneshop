package com.es.core.dao.color;

import com.es.core.model.color.Color;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColorRowMapper implements RowMapper<Color> {

    @Override
    public Color mapRow(ResultSet rs, int rowNum) throws SQLException {
        Color color = new Color();

        color.setId(getNullableLong(rs, "id"));
        color.setCode(rs.getString("code"));

        return color;
    }

    private Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }
}
