package com.ms.fxcashsnt.markservice.sentinel.dao.impl;

import com.ms.fx.common.domain.currency.CurrencyPair;
import com.ms.fxcashsnt.markservice.sentinel.dao.ForwardCurveDAO;
import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardCurve;
import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardPoint;
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
@Repository(value = "forwardCurveDAO")
public class ForwardCurveDAOImpl implements ForwardCurveDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(ForwardCurveDAO.class);

    public ForwardCurveDAOImpl() {
    }

    @Override
    public ForwardCurve query(String currencyPair, String context, Instant startTimestamp, Instant endTimestamp) {
        String sql;
        Object[] objects;
        if (IntraContextList.contains(context)) {
            sql = "SELECT CurrencyPair, Region, Tenor, Pts, OutRight, PositionDate, StartTime, EndTime, Cnt FROM FwdPointTable " +
                    "WHERE CurrencyPair = ? AND Region = ? AND StartTime >= ? AND EndTime <= ? ";
            objects = new Object[]{currencyPair, context, startTimestamp, endTimestamp};
        } else {
            sql = "SELECT CurrencyPair, Region, Tenor, Pts, OutRight, PositionDate, StartTime, EndTime, Cnt FROM FwdPointTable " +
                    "WHERE CurrencyPair = ? AND Region = ? AND PositionDate >= DATE (?) AND PositionDate <= DATE (?) ";
            objects = new Object[]{currencyPair, context, startTimestamp, endTimestamp};
        }
        logger.info(String.format("SELECT FwdPointTable %s %s %s %s", currencyPair, context, startTimestamp, endTimestamp));
        return jdbcTemplate.query(sql, objects, new ResultSetExtractor<ForwardCurve>() {
            @Override
            public ForwardCurve extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                ForwardCurve forwardCurve = new ForwardCurve();
                forwardCurve.setForwardPointMap(new HashMap<>());
                while (resultSet.next()) {
                    String currencyPair = resultSet.getString("CurrencyPair");
                    String context = resultSet.getString("Region");
                    String tenor = resultSet.getString("Tenor");
                    double pts = resultSet.getDouble("Pts");
                    double outright = resultSet.getDouble("OutRight");
                    LocalDate positionDate = LocalDate.parse(resultSet.getString("PositionDate"));
                    Instant startTime = Instant.parse(resultSet.getString("StartTime"));
                    Instant endTime = Instant.parse(resultSet.getString("EndTime"));
                    int count = resultSet.getInt("Cnt");

                    forwardCurve.setCurrencyPair(currencyPair);
                    forwardCurve.setContext(context);
                    forwardCurve.setStartTimestamp(startTimestamp);
                    forwardCurve.setEndTimestamp(endTimestamp);
                    forwardCurve.getForwardPointMap().putIfAbsent(tenor, new LinkedList<>());
                    for (Instant timestamp : Utility.instantLinspace(startTime, endTime, count)) {
                        forwardCurve.getForwardPointMap().get(tenor).add(
                                new ForwardPoint(tenor, pts, outright, positionDate, timestamp)
                        );
                    }
                }
                return forwardCurve;
            }
        });
    }

    @Override
    public List<ForwardCurve> queryForList(Instant startTimestamp, Instant endTimestamp) {
        String sql = "SELECT CurrencyPair, Region, Tenor, Pts, OutRight, PositionDate, StartTime, EndTime, Cnt FROM FwdPointTable " +
                "WHERE StartTime >= ? AND EndTime <= ? ";
        return jdbcTemplate.query(sql, new Object[]{startTimestamp, endTimestamp}, new ResultSetExtractor<List<ForwardCurve>>() {
            @Override
            public List<ForwardCurve> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, ForwardCurve> forwardCurveMap = new HashMap<>();

                while (resultSet.next()) {
                    String currencyPair = resultSet.getString("CurrencyPair");
                    String context = resultSet.getString("Region");
                    String tenor = resultSet.getString("Tenor");
                    double pts = resultSet.getDouble("Pts");
                    double outright = resultSet.getDouble("OutRight");
                    LocalDate positionDate = LocalDate.parse(resultSet.getString("PositionDate"));
                    Instant startTime = Instant.parse(resultSet.getString("StartTime"));
                    Instant endTime = Instant.parse(resultSet.getString("EndTime"));
                    int count = resultSet.getInt("Cnt");

                    String key = String.join("_", currencyPair, context);
                    forwardCurveMap.putIfAbsent(key, new ForwardCurve());
                    ForwardCurve forwardCurve = forwardCurveMap.get(key);
                    if (forwardCurve.getForwardPointMap() == null) {
                        forwardCurve.setForwardPointMap(new HashMap<String, List<ForwardPoint>>());
                    }
                    forwardCurve.getForwardPointMap().putIfAbsent(tenor, new LinkedList<>());

                    forwardCurve.setCurrencyPair(currencyPair);
                    forwardCurve.setContext(context);
                    forwardCurve.setStartTimestamp(startTimestamp);
                    forwardCurve.setEndTimestamp(endTimestamp);
                    for (Instant timestamp : Utility.instantLinspace(startTime, endTime, count)) {
                        forwardCurve.getForwardPointMap().get(tenor).add(
                                new ForwardPoint(tenor, pts, outright, positionDate, timestamp)
                        );
                    }
                }
                return new ArrayList<>(forwardCurveMap.values());
            }
        });
    }

    @Override
    public List<String> queryUniqueCurrencyPair() {
        String sql = "SELECT CurrencyPair FROM FwdPointTable " +
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
