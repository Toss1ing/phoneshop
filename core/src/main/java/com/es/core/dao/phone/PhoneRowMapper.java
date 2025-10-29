package com.es.core.dao.phone;

import com.es.core.model.phone.Phone;
import com.es.core.util.TableColumnsNames;
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

        phone.setId(getNullableLong(rs, TableColumnsNames.ID));
        phone.setBrand(rs.getString(TableColumnsNames.Phone.BRAND));
        phone.setModel(rs.getString(TableColumnsNames.Phone.MODEL));
        phone.setPrice(getNullableBigDecimal(rs, TableColumnsNames.Phone.PRICE));

        phone.setDisplaySizeInches(getNullableBigDecimal(rs, TableColumnsNames.Phone.DISPLAY_SIZE));
        phone.setWeightGr(getNullableInteger(rs, TableColumnsNames.Phone.WEIGHT_GR));
        phone.setLengthMm(getNullableBigDecimal(rs, TableColumnsNames.Phone.LENGTH_MM));
        phone.setHeightMm(getNullableBigDecimal(rs, TableColumnsNames.Phone.HEIGHT_MM));
        phone.setWidthMm(getNullableBigDecimal(rs, TableColumnsNames.Phone.WIDTH_MM));

        Timestamp announcedTs = rs.getTimestamp(TableColumnsNames.Phone.ANNOUNCED);
        phone.setAnnounced(announcedTs != null ? new Date(announcedTs.getTime()) : null);

        phone.setDeviceType(rs.getString(TableColumnsNames.Phone.DEVICE_TYPE));
        phone.setOs(rs.getString(TableColumnsNames.Phone.OS));
        phone.setColors(new HashSet<>());
        phone.setDisplayResolution(rs.getString(TableColumnsNames.Phone.DISPLAY_RESOLUTION));
        phone.setPixelDensity(getNullableInteger(rs, TableColumnsNames.Phone.PIXEL_DENSITY));
        phone.setDisplayTechnology(rs.getString(TableColumnsNames.Phone.DISPLAY_TECHNOLOGY));
        phone.setBackCameraMegapixels(getNullableBigDecimal(rs, TableColumnsNames.Phone.BACK_CAMERA_MP));
        phone.setFrontCameraMegapixels(getNullableBigDecimal(rs, TableColumnsNames.Phone.FRONT_CAMERA_MP));
        phone.setRamGb(getNullableBigDecimal(rs, TableColumnsNames.Phone.RAM_GB));
        phone.setInternalStorageGb(getNullableBigDecimal(rs, TableColumnsNames.Phone.STORAGE_GB));
        phone.setBatteryCapacityMah(getNullableInteger(rs, TableColumnsNames.Phone.BATTERY_MAH));
        phone.setTalkTimeHours(getNullableBigDecimal(rs, TableColumnsNames.Phone.TALK_TIME_HOURS));
        phone.setStandByTimeHours(getNullableBigDecimal(rs, TableColumnsNames.Phone.STANDBY_TIME_HOURS));
        phone.setBluetooth(rs.getString(TableColumnsNames.Phone.BLUETOOTH));
        phone.setPositioning(rs.getString(TableColumnsNames.Phone.POSITIONING));
        phone.setImageUrl(rs.getString(TableColumnsNames.Phone.IMAGE_URL));
        phone.setDescription(rs.getString(TableColumnsNames.Phone.DESCRIPTION));

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
