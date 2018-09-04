package com.ms.fxcashsnt.markservice.sentinel;

import com.ms.fxcashsnt.markservice.sentinel.dao.MarkCurveQueryResultDAO;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotDataSet;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotDataSetBuilder;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

/**
 * user: Carl, WU
 * date: 7/18/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SqliteInsertTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MarkCurveQueryResultDAO markCurveQueryResultDAO;

    @Autowired
    private MarkCurveDownloader markCurveDownloader;

    @Test
    public void SqliteOperationTest() {
//        String sql = "DELETE FROM SpotTable WHERE CurrencyPair = 'USD_RSD' AND Region = 'LNFRM'";
//        jdbcTemplate.execute(sql);
        markCurveDownloader.downloadEODMarkCurve();
        for (int i = 0; i < 10; i++)
            markCurveDownloader.writeResponseListIntoDatabase(markCurveDownloader.downloadMarkCurve(MarkServiceConstants.IntraContextList,Collections.singletonList(LocalDate.now())));
    }

//    @Test
    public void downloaderWorkTest() {
        markCurveDownloader.work();
    }

//    @Test
    public void selectTableTest() {
        String sql = "SELECT * FROM FwdPointTable where CurrencyPair = 'AUD_USD' AND Region = 'HKFRM' AND StartTime >= 2004-12-14T06:31:00.945Z";
//        String sql = "SELECT CurrencyPair, Region FROM FwdPointTable " +
//                "GROUP BY CurrencyPair, Region HAVING COUNT(*) = 1 " +
//                "ORDER BY CurrencyPair, Region";
//        String sql = "SELECT * FROM SpotTable WHERE CurrencyPair = 'USD_HKD'  AND Region = 'LNEOD'";
//        String sql = "SELECT * FROM FwdPointTable WHERE CurrencyPair = 'AUD_USD' AND Region = 'NYEOD' AND StartTime >= ?";
//        String sql = "DELETE FROM SpotTable WHERE CurrencyPair = 'USD_RSD' AND Region = 'LNFRM'";
//        String sql = "DELETE FROM FwdPointTable WHERE Pts IS NULL";
//        jdbcTemplate.execute(sql);
//        jdbcTemplate.execute(sql);
//        String sql = "SELECT * FROM SpotTable";
        List result = jdbcTemplate.queryForList(sql);
        result.forEach(System.out::println);
        System.out.println(result.size());
    }

}
