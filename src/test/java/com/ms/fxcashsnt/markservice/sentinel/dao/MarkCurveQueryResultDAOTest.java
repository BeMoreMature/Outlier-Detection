package com.ms.fxcashsnt.markservice.sentinel.dao;

import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardPoint;
import com.ms.fxcashsnt.markservice.sentinel.model.MarkCurveQueryResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * user: yandong.liu
 * date: 7/20/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class MarkCurveQueryResultDAOTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MarkCurveQueryResultDAO markCurveQueryResultDAO;

    @Test
    @Transactional
    @Rollback(true)
    public void testAddMarkCurveResultDAO() {
        Instant now = Instant.now();
        MarkCurveQueryResult markCurveQueryResult = new MarkCurveQueryResult();
        markCurveQueryResult.setSpotDate(LocalDate.of(2018, 7, 18));
        markCurveQueryResult.setSpotRate(6.6758);
        markCurveQueryResult.setForwardPrecision(0.0001);
        markCurveQueryResult.setCurrencyPair("USD_CNY");
        markCurveQueryResult.setContext("ASIA");
        markCurveQueryResult.setTimestamp(now);
        List<ForwardPoint> forwardPointLinkedList = new LinkedList<>();
        forwardPointLinkedList.add(new ForwardPoint("1W", 124.1190476, 6.688211904760, LocalDate.of(2018, 7, 25), Instant.now()));
        forwardPointLinkedList.add(new ForwardPoint("3M", 467.5683229, 6.722556832290, LocalDate.of(2018, 10, 18), Instant.now()));
        markCurveQueryResult.setForwardPointList(forwardPointLinkedList);
        markCurveQueryResultDAO.batchSave(Collections.singletonList(markCurveQueryResult));

        String spotQuerySql = "SELECT count(id) FROM SpotTable WHERE CurrencyPair = ? AND Region = ?";
        Integer spotNumber = jdbcTemplate.queryForObject(spotQuerySql, new Object[] {markCurveQueryResult.getCurrencyPair(), markCurveQueryResult.getContext()}, Integer.class);
        markCurveQueryResultDAO.printAllRecord();
        Assert.assertEquals(spotNumber.intValue(), 1); // A new record is inserted into SpotTable.

        String forwardQuerySql = "SELECT count(id) FROM FwdPointTable WHERE CurrencyPair = ? AND Region = ?";
        Integer forwardNumber = jdbcTemplate.queryForObject(forwardQuerySql, new Object[] {markCurveQueryResult.getCurrencyPair(), markCurveQueryResult.getContext()}, Integer.class);
        Assert.assertEquals(forwardNumber.intValue(), 2); // Two new records because of two forward points

        markCurveQueryResult.getForwardPointList().get(0).setOutright(6.3);
        markCurveQueryResultDAO.batchSave(Collections.singletonList(markCurveQueryResult));
        spotNumber = jdbcTemplate.queryForObject(spotQuerySql, new Object[] {markCurveQueryResult.getCurrencyPair(), markCurveQueryResult.getContext()}, Integer.class);
        Assert.assertEquals(spotNumber.intValue(), 1); // One record because two object have the same value

        forwardNumber = jdbcTemplate.queryForObject(forwardQuerySql, new Object[] {markCurveQueryResult.getCurrencyPair(), markCurveQueryResult.getContext()}, Integer.class);
        Assert.assertEquals(forwardNumber.intValue(),3); // Three record because the outright value changes.
    }

    @Before
    @After
    public void removeSqliteTestDatabaseFile() {
        jdbcTemplate.execute("DELETE FROM SpotTable WHERE 1=1");
        jdbcTemplate.execute("DELETE FROM FwdPointTable WHERE 1=1");
        File sqliteDatabaseFile = new File("mark_history_test.sqlite");
        sqliteDatabaseFile.deleteOnExit();
    }
}
