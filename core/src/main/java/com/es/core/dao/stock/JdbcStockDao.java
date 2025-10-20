package com.es.core.dao.stock;

import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.StockSql;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;


public class JdbcStockDao implements StockDao {

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean updateReservedByPhoneId(Long phoneId, int quantity) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(TableColumnsNames.Phone.PHONE_ID, phoneId)
                .addValue(TableColumnsNames.Stock.RESERVED_QUANTITY, quantity);

        int updatedRowsCount = namedParameterJdbcTemplate.update(
                StockSql.UPDATE_RESERVED_WHERE_STOCK_AVAILABLE_BY_PHONE_ID,
                params
        );

        return updatedRowsCount > 0;
    }

    @Override
    public int[] updateReservedItems(Map<Long, Integer> items) {
        MapSqlParameterSource[] params = items.entrySet().stream()
                .map(mapEntry -> new MapSqlParameterSource()
                        .addValue(TableColumnsNames.Phone.PHONE_ID, mapEntry.getKey())
                        .addValue(TableColumnsNames.Stock.RESERVED_QUANTITY, mapEntry.getValue())
                ).toArray(MapSqlParameterSource[]::new);

        return namedParameterJdbcTemplate.batchUpdate(
                StockSql.UPDATE_RESERVED_WHERE_STOCK_AVAILABLE_BY_PHONE_ID,
                params
        );
    }

    @Override
    public boolean decreaseReservedByPhoneId(Long phoneId, int quantity) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(TableColumnsNames.Phone.PHONE_ID, phoneId)
                .addValue(TableColumnsNames.Stock.RESERVED_QUANTITY, quantity);

        int updatedRowsCount = namedParameterJdbcTemplate.update(
                StockSql.DECREASE_RESERVED_BY_PHONE_ID,
                params
        );

        return updatedRowsCount > 0;
    }

}
