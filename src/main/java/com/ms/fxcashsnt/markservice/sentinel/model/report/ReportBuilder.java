package com.ms.fxcashsnt.markservice.sentinel.model.report;

import com.ms.fxcashsnt.markservice.sentinel.detector.CrossRegionDetector;
import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.strategy.DifferGreatlyStrategy;
import com.ms.fxcashsnt.markservice.sentinel.strategy.Strategy;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

/**
 * user: Carl,Wu
 * date: 8/8/2018
 * convert result of region to list of report
 */
public class ReportBuilder {
    private CrossRegionDetector crossRegionDetector;
    public ReportBuilder(CrossRegionDetector detectRegionService){
        this.crossRegionDetector = detectRegionService;
    }
    public List<Report> build(Double threshold, LocalDate positionDate){
        TreeMap<String,Map> map = this.crossRegionDetector.detectRegion(threshold, positionDate);
        List<Report> reportList = new ArrayList<>();
        Strategy differGreatlyStrategy = new DifferGreatlyStrategy(0.2);
        map.entrySet().forEach ( entry ->{
            Report report = new Report();
            String [] currencyTenor = entry.getKey().split("_");
            report.setCurrencyPair(currencyTenor[0]);
            report.setTenor(currencyTenor[1]);

            HashMap<String,Double> regionMap = new HashMap<>(entry.getValue());
            Point[] pointArray = new Point[4];
            for(int i=0;i<pointArray.length;i++){
                pointArray[i]=new Point();
            }
            regionMap.entrySet().forEach(regionEntry -> {
                if (regionEntry.getKey().contains("NY")){
                    pointArray[0] = (new Point(regionEntry.getValue(), positionDate.atStartOfDay().toInstant(ZoneOffset.UTC)));
                }else  if (regionEntry.getKey().contains("LN")){
                    pointArray[1] = (new Point(regionEntry.getValue(), positionDate.atStartOfDay().toInstant(ZoneOffset.UTC)));
                }else if (regionEntry.getKey().contains("TK")){
                    pointArray[2] = (new Point(regionEntry.getValue(), positionDate.atStartOfDay().toInstant(ZoneOffset.UTC)));
                }else if (regionEntry.getKey().contains("HK")){
                    pointArray[3] = (new Point(regionEntry.getValue(), positionDate.atStartOfDay().toInstant(ZoneOffset.UTC)));
                }
            });
            List<Point> pointList = Arrays.asList(pointArray);
            report.setPointList(pointList);
            report.setBooleanList(differGreatlyStrategy.predict(pointList));
            reportList.add(report);
        });
        return reportList;
    }
}
