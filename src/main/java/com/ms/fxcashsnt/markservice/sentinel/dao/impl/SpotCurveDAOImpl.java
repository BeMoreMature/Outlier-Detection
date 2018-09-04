package com.ms.fxcashsnt.markservice.sentinel.dao.impl;

import com.ms.fxcashsnt.markservice.sentinel.dao.SpotCurveDAO;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotCurve;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotPoint;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import com.ms.fxcashsnt.markservice.sentinel.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants.IntraContextList;

/**
 * user: yandong.liu
 * date: 7/23/2018
 */
@Repository(value = "spotCurveDAO")
public class SpotCurveDAOImpl implements SpotCurveDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(SpotCurveDAO.class);

    public SpotCurveDAOImpl() {
    }

    @Override
    public SpotCurve query(String currencyPair, String context, Instant startTimestamp, Instant endTimestamp) {
        String sql;
        Object [] objects;
        if(IntraContextList.contains(context)){
            sql = "SELECT CurrencyPair, Region, SpotRate, SpotDate, PositionDate, StartTime, EndTime, Cnt FROM SpotTable" +
                    " WHERE CurrencyPair = ? AND Region = ? AND StartTime >= ? AND EndTime <= ? ";
            objects = new Object[] {currencyPair, context, startTimestamp, endTimestamp};
        }else {
            sql = "SELECT CurrencyPair, Region, SpotRate, SpotDate, PositionDate, StartTime, EndTime, Cnt FROM SpotTable" +
                    " WHERE CurrencyPair = ? AND Region = ? AND PositionDate >= DATE (?) AND PositionDate <= DATE (?) ";
            objects = new Object[] {currencyPair, context, startTimestamp, endTimestamp};
        }
        logger.info(String.format("SELECT SpotTable %s %s %s %s", currencyPair, context, startTimestamp, endTimestamp));
        return jdbcTemplate.query(sql, objects, new ResultSetExtractor<SpotCurve>() {
            @Override
            public SpotCurve extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                SpotCurve spotCurve = new SpotCurve();
                spotCurve.setSpotPointList(new LinkedList<>());
                while (resultSet.next()) {
                    String currencyPair = resultSet.getString("CurrencyPair");
                    String context = resultSet.getString("Region");
                    Double spotRate = resultSet.getDouble("SpotRate");

                    LocalDate spotDate = LocalDate.parse(resultSet.getString("SpotDate"));
                    LocalDate positionDate = LocalDate.parse(resultSet.getString("PositionDate"));
                    Instant startTime = Instant.parse(resultSet.getString("StartTime"));
                    Instant endTime = Instant.parse(resultSet.getString("EndTime"));
                    int count = resultSet.getInt("Cnt");

                    spotCurve.setCurrencyPair(currencyPair);
                    spotCurve.setContext(context);
                    spotCurve.setStartTimestamp(startTimestamp);
                    spotCurve.setEndTimestamp(endTimestamp);
                    for (Instant timestamp : Utility.instantLinspace(startTime, endTime, count)) {
                        spotCurve.getSpotPointList().add(
                                new SpotPoint(spotRate, spotDate, positionDate, timestamp)
                        );
                    }
                }
                return spotCurve;
            }
        });
    }

    @Override
    public List<SpotCurve> queryForList(Instant startTimestamp, Instant endTimestamp) {
        String sql = "SELECT CurrencyPair, Region, SpotRate, SpotDate, PositionDate, StartTime, EndTime, Cnt FROM SpotTable" +
                " WHERE StartTime >= ? AND EndTime <= ? ";
        return jdbcTemplate.query(sql, new Object[]{startTimestamp, endTimestamp}, new ResultSetExtractor<List<SpotCurve>>() {
            @Override
            public List<SpotCurve> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, SpotCurve> spotCurveMap = new HashMap<>();

                while (resultSet.next()) {
                    String currencyPair = resultSet.getString("CurrencyPair");
                    String context = resultSet.getString("Region");
                    Double spotRate = resultSet.getDouble("SpotRate");
                    LocalDate spotDate = LocalDate.parse(resultSet.getString("SpotDate"));
                    LocalDate positionDate = LocalDate.parse(resultSet.getString("PositionDate"));
                    Instant startTime = Instant.parse(resultSet.getString("StartTime"));
                    Instant endTime = Instant.parse(resultSet.getString("EndTime"));
                    int count = resultSet.getInt("Cnt");

                    String key = String.join("_", currencyPair, context);
                    spotCurveMap.putIfAbsent(key, new SpotCurve());
                    SpotCurve spotCurve = spotCurveMap.get(key);
                    if (spotCurve.getSpotPointList() == null)
                        spotCurve.setSpotPointList(new LinkedList<>());

                    spotCurve.setCurrencyPair(currencyPair);
                    spotCurve.setContext(context);
                    spotCurve.setStartTimestamp(startTimestamp);
                    spotCurve.setEndTimestamp(endTimestamp);
                    for (Instant timestamp : Utility.instantLinspace(startTime, endTime, count)) {
                        spotCurve.getSpotPointList().add(
                                new SpotPoint(spotRate, spotDate, positionDate, timestamp)
                        );
                    }
                }
                return new ArrayList<>(spotCurveMap.values());
            }
        });
    }


    @Override
    public List<String> queryUniqueCurrencyPair() {
        String sql = "SELECT CurrencyPair FROM SpotTable " +
                "GROUP BY CurrencyPair";
        List<Map<String, Object>> currencyPairMapList = jdbcTemplate.queryForList(sql);
        List<String> currenryPairList = currencyPairMapList.stream().map(m -> (String)m.get("CurrencyPair")).collect(Collectors.toList());
        HashSet <String> removeCurrencyPairSet = new HashSet<>();
        for(String key : currenryPairList){
            for(String ignoreCurrency : MarkServiceConstants.IgnoreCurrencyList){
                if(key.contains(ignoreCurrency)){
                    removeCurrencyPairSet.add(key);
                }
            }
        }
        currenryPairList.removeAll(removeCurrencyPairSet);
        return currenryPairList;
    }
}
