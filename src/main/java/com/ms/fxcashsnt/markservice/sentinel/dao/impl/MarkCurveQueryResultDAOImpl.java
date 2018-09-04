package com.ms.fxcashsnt.markservice.sentinel.dao.impl;


import com.ms.fxcashsnt.markservice.sentinel.dao.MarkCurveQueryResultDAO;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardPoint;
import com.ms.fxcashsnt.markservice.sentinel.model.MarkCurveQueryResult;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import com.ms.fxcashsnt.markservice.sentinel.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * user: yandong.liu
 * date: 7/17/2018
 */
@Transactional
@Repository(value = "markCurveQueryResultDAO")
public class MarkCurveQueryResultDAOImpl implements MarkCurveQueryResultDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * We maintain the auto increment primary key id in SpotTable and ForwardTable in java rather than in database.
     */
    private int spotId;
    private int forwardId;
    /**
     * for spotInsert, key is currencyPair and context(region)
     * value is ID in the table, spotRate, timestamp
     * for forwardInsert, key is currencyPair, context, Tenor
     * value is ID, Outright, timestamp (the prefix is PositionDate)
     */
    private Map<String, CacheElement> spotInsertMap;
    private Map<String, CacheElement> forwardInsertMap;

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkCurveDownloader.class);

    @Autowired
    public MarkCurveQueryResultDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        createTable();
        // keep id can increase continuously after interrupt
        Integer spotIdInDatabase = getJdbcTemplate().queryForObject("SELECT max(id) FROM SpotTable", Integer.class);
        this.spotId = 1 + (spotIdInDatabase == null ? 0 : spotIdInDatabase);

        Integer forwardIdInDatabase = getJdbcTemplate().queryForObject("SELECT max(id) FROM FwdPointTable", Integer.class);
        this.forwardId = 1 + (forwardIdInDatabase == null ? 0 : forwardIdInDatabase);
        LOGGER.info("GET SPOT TABLE ID" + spotId);
        LOGGER.info("GET FORWARD TABLE ID" + forwardId);
        this.spotInsertMap = new HashMap<>();
        this.forwardInsertMap = new HashMap<>();
    }

    @Override
    public void batchSave(List<MarkCurveQueryResult> resultList) {
        batchSaveSpotRate(resultList);
        batchSaveForwardRate(resultList);
    }

    /**
     * create spotTable and FwdPointTable
     */
    public void createTable() {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS SpotTable (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CurrencyPair VARCHAR(20), Region VARCHAR(10), " +
                "PositionDate DATE, SpotDate DATE, SpotRate DOUBLE, " +
                "StartTime TIMESTAMP, EndTime TIMESTAMP, Cnt INT)");
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS FwdPointTable (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CurrencyPair VARCHAR(20), Region VARCHAR(10), " +
                "PositionDate DATE, Tenor VARCHAR(10), Pts DOUBLE, " +
                "OutRight DOUBLE, StartTime TIMESTAMP, " +
                "EndTime TIMESTAMP, Cnt INT)");
        jdbcTemplate.update("CREATE INDEX currencypair_context_spot_index on  SpotTable(CurrencyPair, Region)");
        jdbcTemplate.update("CREATE INDEX currencypair_context_fwd_index on  FwdPointTable(CurrencyPair, Region)");
        LOGGER.info("CREATE TABLE SUCCESSFUL");
    }

    public void batchSaveSpotRate(List<MarkCurveQueryResult> resultList) {
        List<Object[]> insertBatchArgs = new LinkedList<>();
        List<Object[]> updateBatchArgs = new LinkedList<>();
        String insertSql = "INSERT INTO SpotTable (ID, CurrencyPair, Region, PositionDate, " +
                "SpotDate, SpotRate, StartTime, EndTime, Cnt) VALUES (?, ?, ?, date(?), date(?), ?, ?, ?, ?)";
        String updateSql = "UPDATE SpotTable SET EndTime = ? , Cnt = Cnt + 1 WHERE ID = ?";

        for (MarkCurveQueryResult result : resultList) {
            String key = String.join("_", result.getCurrencyPair(), result.getContext());
            // if key is not exist or instant timestamp is the next day, add one record
            // another condition is if it is a End of day record, there is no need to compress
            if (!spotInsertMap.containsKey(key)
                    || Utility.isBeforeBasedOnDate(spotInsertMap.get(key).getTimestamp(), result.getTimestamp())
                    || MarkServiceConstants.EndContextList.contains(result.getContext())
                    || !Utility.equals(spotInsertMap.get(key).getValue(), result.getSpotRate(), 1e-5)) {
                spotInsertMap.put(key, new CacheElement(spotId, result.getSpotRate(), result.getTimestamp()));
                // Just insert into db
                insertBatchArgs.add(new Object[]{spotId, result.getCurrencyPair(), result.getContext(), result.getPositionDate(),
                        result.getSpotDate(), result.getSpotRate(), result.getTimestamp(), result.getTimestamp(), 1});
                spotId += 1;
            } else {
                // Otherwise, merge with the last record
                updateBatchArgs.add(new Object[]{result.getTimestamp(), spotInsertMap.get(key).getId()});
            }
        }
        getJdbcTemplate().batchUpdate(insertSql, insertBatchArgs);
        getJdbcTemplate().batchUpdate(updateSql, updateBatchArgs);
        LOGGER.info("SPOT TABLE DONE");
    }

    public void batchSaveForwardRate(List<MarkCurveQueryResult> resultList) {
        List<Object[]> insertBatchArgs = new LinkedList<>();
        List<Object[]> updateBatchArgs = new LinkedList<>();
        String insertSql = "INSERT INTO FwdPointTable (ID, CurrencyPair, Region, PositionDate, " +
                "Tenor, Pts, OutRight, StartTime, EndTime, Cnt) VALUES (?, ?, ?, date(?), ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE FwdPointTable SET EndTime = ? , Cnt=Cnt+1 WHERE ID= ?";

        for (MarkCurveQueryResult result : resultList) {
            // for each FwdPoint in the result, if key is not exist or instant timestamp is the next day, add one record
            for (ForwardPoint forwardPoint : result.getForwardPointList()) {
                String key = String.join("_", result.getCurrencyPair(), result.getContext(), forwardPoint.getTenor());
                if (!forwardInsertMap.containsKey(key)
                        || Utility.isBeforeBasedOnDate(forwardInsertMap.get(key).getTimestamp(), result.getTimestamp())
                        || MarkServiceConstants.EndContextList.contains(result.getContext())
                        || !Utility.equals(forwardInsertMap.get(key).getValue(), forwardPoint.getOutright(), 1e-5)) {
                    forwardInsertMap.put(key, new CacheElement(forwardId, forwardPoint.getOutright(), result.getTimestamp()));
                    // Just insert into db
                    insertBatchArgs.add(new Object[]{forwardId, result.getCurrencyPair(), result.getContext(), result.getPositionDate(),
                            forwardPoint.getTenor(), forwardPoint.getPts(), forwardPoint.getOutright(), result.getTimestamp(), result.getTimestamp(), 1});
                    forwardId += 1;
                } else {
                    // Otherwise, merge with the last record
                    updateBatchArgs.add(new Object[]{result.getTimestamp(), forwardInsertMap.get(key).getId()});
                }
            }
        }
        getJdbcTemplate().batchUpdate(insertSql, insertBatchArgs);
        getJdbcTemplate().batchUpdate(updateSql, updateBatchArgs);
        LOGGER.info("FWDPOINTTABLE DONE");

    }

    /**
     * print the all records in the table
     */
    public void printAllRecord() {
        printTable("SpotTable");
        printTable("FwdPointTable");
    }

    private void printTable(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        List records = getJdbcTemplate().queryForList(sql);
        Iterator iter = records.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

    private JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public class CacheElement {
        private int id;
        private double value;
        private Instant timestamp;


        public CacheElement(int id, double value, Instant timestamp) {
            this.id = id;
            this.value = value;

            this.timestamp = timestamp;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }


        public Instant getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
        }
    }
}
