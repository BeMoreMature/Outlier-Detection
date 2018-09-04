package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.model.report.RegionReport;
import com.ms.fxcashsnt.markservice.sentinel.model.report.RegionReportBuilder;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.*;

/**
 * user: Carl,Wu
 * date: 7/26/2018
 */

@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DetectRegionServiceTest {
    @Autowired
    private CrossRegionDetector crossRegionDetector;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MarkCurveDownloader markCurveDownloader;
//    @Test
    public void detectRegionTest(){
        RegionReportBuilder regionReportBuilder = new RegionReportBuilder(crossRegionDetector);
        List<RegionReport> list = regionReportBuilder.build(0.2,LocalDate.now());
        for (RegionReport regionReport : list){
            System.out.println(regionReport.getCurrency()+" "+regionReport.getTenor()+" "+regionReport.getNY()+" "+regionReport.getLN()+" "+regionReport.getTK()+" "+regionReport.getHK());
        }
    }

    @Test
    public void validateDataTest() {
        markCurveDownloader.writeResponseListIntoDatabase(markCurveDownloader.downloadMarkCurve(MarkServiceConstants.EndContextList,Collections.singletonList(LocalDate.now())));
        String sql = "SELECT * FROM FwdPointTable WHERE CurrencyPair = 'USD_AED' ";
        List result = jdbcTemplate.queryForList(sql);
        result.forEach(System.out::println);
    }

    @Test
    public void validateReportTest(){
        TreeMap<String,Double> map = new TreeMap<>();
        map.put("CAD_1M",0.0);
        map.put("USD_1M",0.0);
        map.put("CAD_SPOT",0.0);
        String region = "NYFRM";
//        for(String key : map.keySet()){
//            if(region.contains(key)){
//                map.put(key,0.2);
//            }
//        }
        map.entrySet().forEach(entry -> System.out.println(entry.getKey()+" "+entry.getValue()));
    }
}
