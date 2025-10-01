package com.es.core.dao.phone;

import com.es.core.model.phone.Phone;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;

public class PhoneRowMapper implements RowMapper<Phone> {

    @Override
    public Phone mapRow(ResultSet rs, int rowNum) throws SQLException {
        Phone phone = new Phone();

        phone.setId(getNullableLong(rs, "id"));
        phone.setBrand(rs.getString("brand"));
        phone.setModel(rs.getString("model"));
        phone.setPrice(getNullableBigDecimal(rs, "price"));

        phone.setDisplaySizeInches(getNullableBigDecimal(rs, "displaySizeInches"));
        phone.setWeightGr(getNullableInteger(rs, "weightGr"));
        phone.setLengthMm(getNullableBigDecimal(rs, "lengthMm"));
        phone.setHeightMm(getNullableBigDecimal(rs, "heightMm"));
        phone.setWidthMm(getNullableBigDecimal(rs, "widthMm"));

        Timestamp announcedTs = rs.getTimestamp("announced");
        phone.setAnnounced(announcedTs != null ? new Date(announcedTs.getTime()) : null);

        phone.setDeviceType(rs.getString("deviceType"));
        phone.setOs(rs.getString("os"));
        phone.setColors(new HashSet<>());
        phone.setDisplayResolution(rs.getString("displayResolution"));
        phone.setPixelDensity(getNullableInteger(rs, "pixelDensity"));
        phone.setDisplayTechnology(rs.getString("displayTechnology"));
        phone.setBackCameraMegapixels(getNullableBigDecimal(rs, "backCameraMegapixels"));
        phone.setFrontCameraMegapixels(getNullableBigDecimal(rs, "frontCameraMegapixels"));
        phone.setRamGb(getNullableBigDecimal(rs, "ramGb"));
        phone.setInternalStorageGb(getNullableBigDecimal(rs, "internalStorageGb"));
        phone.setBatteryCapacityMah(getNullableInteger(rs, "batteryCapacityMah"));
        phone.setTalkTimeHours(getNullableBigDecimal(rs, "talkTimeHours"));
        phone.setStandByTimeHours(getNullableBigDecimal(rs, "standByTimeHours"));
        phone.setBluetooth(rs.getString("bluetooth"));
        phone.setPositioning(rs.getString("positioning"));
        phone.setImageUrl(rs.getString("imageUrl"));
        phone.setDescription(rs.getString("description"));

        return phone;
    }

    private Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private BigDecimal getNullableBigDecimal(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return rs.wasNull() ? null : value;
    }

    private Integer getNullableInteger(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
}
