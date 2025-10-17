package com.es.core.dao.stock;

import com.es.core.model.phone.Stock;
import com.es.core.util.TableColumnsNames;
import com.es.core.util.sql.StockSql;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;


public class JdbcStockDao implements StockDao {

    @Resource
    StockRowMapper stockRowMapper;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<Stock> getStockByPhoneId(Long phoneId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(TableColumnsNames.Phone.PHONE_ID, phoneId);

        List<Stock> stocks = namedParameterJdbcTemplate.query(
                StockSql.FIND_STOCK_STOCK_BY_PHONE_ID,
                params,
                stockRowMapper
        );

        return stocks.stream().findFirst();
    }
}
