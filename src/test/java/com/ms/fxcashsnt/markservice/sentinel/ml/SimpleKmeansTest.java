package com.ms.fxcashsnt.markservice.sentinel.ml;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

//@ContextConfiguration(locations = {"classpath:spring-core.xml"})
//@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleKmeansTest {
    private static final Logger logger = LoggerFactory.getLogger(MarkCurveDownloader.class);
//    @Test
    public void testKmeans(){
        SimpleKmeans test = new SimpleKmeans();
        test.loadArff("C:\\Users\\carwu\\Desktop\\HKD_LNEOD_20days.csv");
        Report report = test.clusterData();
        int i =0 ;
        for (Boolean flag : report.getBooleanList()){
            if(flag == true){
                System.out.println(i);
            }
            i++;
        }
        List<Report> reportList = new LinkedList<>();
        report.setCurrencyPair("USD_HKD");
        report.setContext("LNEOD");

        reportList.add(report);
        logger.info("ReportList DONE");

        try {
            test.addColumn("C:\\Users\\carwu\\Desktop\\", "HKD_LNEOD_20days.csv", reportList.get(0).getBooleanList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
