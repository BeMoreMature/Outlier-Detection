package com.ms.fxcashsnt.markservice.sentinel.model.report;

import com.ms.fxcashsnt.markservice.sentinel.detector.CrossRegionDetector;

import java.time.LocalDate;
import java.util.*;
/**
 * user: Carl,Wu
 * date: 8/8/2018
 * build format that need to insert in the table
 */
public class RegionReportBuilder {
    private CrossRegionDetector crossRegionDetector;
    public RegionReportBuilder(CrossRegionDetector crossRegionDetector){
        this.crossRegionDetector = crossRegionDetector;
    }

    public List<RegionReport> build(Double threshold, LocalDate positionDate){
        TreeMap<String,Map> map = this.crossRegionDetector.detectRegion(threshold, positionDate);
        List<RegionReport> res = new ArrayList<>();
        map.entrySet().forEach ( entry ->{
            RegionReport regionReport = new RegionReport();
            String [] currencyTenor = entry.getKey().split("_");
            regionReport.setCurrency(currencyTenor[0]);
            regionReport.setTenor(currencyTenor[1]);

            HashMap <String,Double> regionMap = new HashMap<>(entry.getValue());
            regionMap.entrySet().forEach(regionEntry -> {
                if (regionEntry.getKey().contains("NY")){
                    regionReport.setNY(regionEntry.getValue());
                }else  if (regionEntry.getKey().contains("LN")){
                    regionReport.setLN(regionEntry.getValue());
                }else if (regionEntry.getKey().contains("TK")){
                    regionReport.setTK(regionEntry.getValue());
                }else if (regionEntry.getKey().contains("HK")){
                    regionReport.setHK(regionEntry.getValue());
                }
            });
            res.add(regionReport);
        });
        return res;
    }
}
