package com.es.core.dao.stock;

import com.es.core.model.phone.Stock;
import com.es.core.util.sql.StockSql;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;


public class JdbcStockDao implements StockDao {

    @Resource
    StockRowMapper stockRowMapper;

    @Resource
    JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Stock> getStockByPhoneId(Long phoneId) {
        List<Stock> stocks = jdbcTemplate.query(
                StockSql.FIND_STOCK_STOCK_BY_PHONE_ID,
                new Object[]{phoneId},
                stockRowMapper
        );

        return stocks.stream().findFirst();
    }
}
