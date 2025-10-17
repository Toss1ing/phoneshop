package com.es.core.dao.color;

import com.es.core.model.color.Color;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColorRowMapper implements RowMapper<Color> {

    @Override
    public Color mapRow(ResultSet rs, int rowNum) throws SQLException {
        Color color = new Color();

        Long id = getNullableLong(rs, hasColumn(rs, "colorId") ? "colorId" : "id");
        String code = rs.getString(hasColumn(rs, "colorCode") ? "colorCode" : "code");

        color.setId(id);
        color.setCode(code);

        return color;
    }

    private Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
