package com.ms.fxcashsnt.markservice.sentinel.ml;

import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.detector.SmoothedZScoreDetector;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ContextConfiguration(locations = {"classpath:spring-core.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SmoothedZScoreTest {
    @Autowired
    private SmoothedZScoreDetector smoothedZScoreDetector;
    @Test
    public void testLoading(){
            smoothedZScoreDetector.setMaxReportSize(50);
            smoothedZScoreDetector.setTrainStartTimestamp(Instant.now().minus(3000, ChronoUnit.DAYS));
            smoothedZScoreDetector.setTrainEndTimestamp(Instant.now().minus(0, ChronoUnit.DAYS));
            smoothedZScoreDetector.setTestStartTimestamp(Instant.now().minus(3000, ChronoUnit.DAYS));
            smoothedZScoreDetector.setTestEndTimestamp(Instant.now().minus(0, ChronoUnit.DAYS));
            smoothedZScoreDetector.setLag(30);
            smoothedZScoreDetector.setThreshold(3.5);
            smoothedZScoreDetector.setInfluence(0.5);
            smoothedZScoreDetector.loadStrategy();

            List<Report> spotReportList = smoothedZScoreDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
            List<Report> forwardReportList = smoothedZScoreDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);

            spotReportList.forEach(System.out::println);
    }
}
