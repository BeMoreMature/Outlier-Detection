package com.ms.fxcashsnt.markservice.sentinel.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * user: yandong.liu
 * date: 7/23/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CurveQueryDAOTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    public void queryCurveTest() {
        long start = System.currentTimeMillis();
        String sql = "select count(id) from FwdPointTable where CurrencyPair=? union select count(id) from SpotTable where CurrencyPair=?" +
                "union select count(id) from FwdPointTable where CurrencyPair='GBP_USD'";
        for (int i = 0; i < 121; i++) {
            List list = jdbcTemplate.queryForList(sql, "USD_CNY", "USD_CNY");
            list.forEach(System.out::println);
        }
        System.out.println(System.currentTimeMillis()-start);
    }
}
