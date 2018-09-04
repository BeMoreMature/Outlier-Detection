package com.ms.fxcashsnt.markservice.sentinel.util;

import com.ms.fxcashsnt.markservice.sentinel.model.MarkCurveQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Transactional
@Repository(value = "saveCSVToDatabase")
public class SaveCSVToDatabase {
    @Autowired
    private JdbcTemplate curveJdbcTemplate;

    @Autowired
    private MarkCurveDownloader markCurveDownloader;

    private int spotId;
    private int forwardId;

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkCurveDownloader.class);

    private HashSet <String> currenryPairSet = new HashSet<>();

    public SaveCSVToDatabase(JdbcTemplate curveJdbcTemplate){
        this.curveJdbcTemplate = curveJdbcTemplate;
        createTable();
        Integer spotIdInDatabase = getJdbcTemplate().queryForObject("SELECT max(id) FROM SpotTable", Integer.class);
        this.spotId = 1 + (spotIdInDatabase == null ? 0 : spotIdInDatabase);

        Integer forwardIdInDatabase = getJdbcTemplate().queryForObject("SELECT max(id) FROM FwdPointTable", Integer.class);
        this.forwardId = 1 + (forwardIdInDatabase == null ? 0 : forwardIdInDatabase);
        LOGGER.info("GET SPOT TABLE ID" + spotId);
        LOGGER.info("GET FORWARD TABLE ID" + forwardId);
    }
    /**
     * create spotTable and FwdPointTable
     */
    public void createTable() {
        curveJdbcTemplate.update("CREATE TABLE IF NOT EXISTS SpotTable (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CurrencyPair VARCHAR(20), Region VARCHAR(10), " +
                "PositionDate DATE, SpotDate DATE, SpotRate DOUBLE, " +
                "StartTime TIMESTAMP, EndTime TIMESTAMP, Cnt INT)");
        curveJdbcTemplate.update("CREATE TABLE IF NOT EXISTS FwdPointTable (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CurrencyPair VARCHAR(20), Region VARCHAR(10), " +
                "PositionDate DATE, Tenor VARCHAR(10), Pts DOUBLE, " +
                "OutRight DOUBLE, StartTime TIMESTAMP, " +
                "EndTime TIMESTAMP, Cnt INT)");
        LOGGER.info("CREATE TABLE SUCCESSFUL");
    }

    public void save(String path, String region) {
        saveSpotRate(path, region);
        saveForwardRate(path, region);
    }

    public void saveSpotRate(String path, String region){
        List<Object[]> insertBatchArgs = new LinkedList<>();
        String insertSql = "INSERT INTO SpotTable (ID, CurrencyPair, Region, PositionDate, " +
                "SpotDate, SpotRate, StartTime, EndTime, Cnt) VALUES (?, ?, ?, date(?), date(?), ?, ?, ?, ?)";
        BufferedReader br = null;
        String line = "";
        getAllCurrencyPair();
        try {
            br = new BufferedReader(new FileReader(path));
            int count = 0;
            while((line = br.readLine()) != null){
                count++;
                String [] element = line.split(",");
                if(element.length < 5){
                    LOGGER.warn("MISSING VALUE AT THE ROW "+count);
                    continue;
                }
                String currency = "";
                for(String currencyPair : currenryPairSet){
                    if(currencyPair.contains(element[1])){
                        currency = currencyPair;
                    }
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDate positionDate = LocalDate.parse(element[0],formatter);
                LocalDate spotDate = LocalDate.parse(element[2],formatter);
                if(element[3].equals("SPOT")){
                    insertBatchArgs.add(new Object[]{spotId, currency, region, positionDate, spotDate, element[4], positionDate.atStartOfDay().toInstant(ZoneOffset.UTC), positionDate.atStartOfDay().toInstant(ZoneOffset.UTC), 1});
                    spotId += 1;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        getJdbcTemplate().batchUpdate(insertSql,insertBatchArgs);
        LOGGER.info("SPOT TABLE DONE");
    }

    public void saveForwardRate(String path, String region){
        List<Object[]> insertBatchArgs = new LinkedList<>();
        String insertSql = "INSERT INTO FwdPointTable (ID, CurrencyPair, Region, PositionDate, " +
                "Tenor, Pts, OutRight, StartTime, EndTime, Cnt) VALUES (?, ?, ?, date(?), ?, ?, ?, ?, ?, ?)";
        BufferedReader br = null;
        String line = "";
        getAllCurrencyPair();
        try {
            br = new BufferedReader(new FileReader(path));
            int count = 0;
            while((line = br.readLine()) != null){
                count++;
                String [] element = line.split(",");
                if(element.length < 5){
                    LOGGER.warn("MISSING VALUE AT THE ROW "+count);
                    continue;
                }
                String currency = "";
                for(String currencyPair : currenryPairSet){
                    if(currencyPair.contains(element[1])){
                        currency = currencyPair;
                    }
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDate positionDate = LocalDate.parse(element[0],formatter);
                if(element[3].equals("SPOT")) continue;
                if(!element[3].isEmpty()){
                    insertBatchArgs.add(new Object[]{forwardId, currency, region, positionDate, element[3], element[4],element[4], positionDate.atStartOfDay().toInstant(ZoneOffset.UTC),positionDate.atStartOfDay().toInstant(ZoneOffset.UTC), 1});
                    forwardId += 1;
                }else{
                    LocalDate valueDate = LocalDate.parse(element[2],formatter);
                    long diff = ChronoUnit.DAYS.between(positionDate, valueDate);
                    String tenor = diff + "D";
                    insertBatchArgs.add(new Object[]{forwardId, element[1], region, positionDate, tenor, element[4],element[4], positionDate.atStartOfDay().toInstant(ZoneOffset.UTC), positionDate.atStartOfDay().toInstant(ZoneOffset.UTC), 1});
                    forwardId += 1;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for(int i = 0;i < 100; i++){
            int step = insertBatchArgs.size()/100;
            List<Object[]> list = new ArrayList<>();
            for( int j = 0;j < step;j++){
                if(i*step + j >= insertBatchArgs.size()) continue;
                list.add(insertBatchArgs.get(i*step+j));
            }
            System.out.println(i+" "+step);
            getJdbcTemplate().batchUpdate(insertSql,list);
        }
//        getJdbcTemplate().batchUpdate(insertSql,insertBatchArgs);
        LOGGER.info("FWDPOINTTABLE DONE");
    }

    private JdbcTemplate getJdbcTemplate() {
        return curveJdbcTemplate;
    }

    public void getAllCurrencyPair(){
        List <MarkCurveQueryResponse> list = markCurveDownloader.downloadMarkCurve(MarkServiceConstants.EndContextList, Collections.singletonList(LocalDate.now()));
        // find all possible currencyPair
        for(MarkCurveQueryResponse response : list){
            currenryPairSet.addAll(response.getMarkCurveQueryResultMap().keySet());
        }
    }
}
