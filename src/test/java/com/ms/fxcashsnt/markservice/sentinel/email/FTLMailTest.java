package com.ms.fxcashsnt.markservice.sentinel.email;

import com.ms.fxcashsnt.markservice.sentinel.detector.CrossRegionDetector;
import com.ms.fxcashsnt.markservice.sentinel.mail.FTLMail;
import com.ms.fxcashsnt.markservice.sentinel.model.report.*;
import com.ms.fxcashsnt.markservice.sentinel.detector.ChangeSoFastDetector;
import com.ms.fxcashsnt.markservice.sentinel.detector.RemainUnchangedDetector;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * user: Carl,Wu
 * date: 8/3/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class FTLMailTest {
    @Autowired
    private CrossRegionDetector crossRegionDetector;
    @Autowired
    private FTLMail ftlMail;
    @Autowired
    private ChangeSoFastDetector changeSoFastDetector;
    @Autowired
    private RemainUnchangedDetector remainUnchangedDetector;
    @Test
    public void bolgFTLTest(){
        RegionReportBuilder regionReportBuilder = new RegionReportBuilder(crossRegionDetector);
        List<RegionReport> regionReportList = regionReportBuilder.build(0.2,java.time.LocalDate.now());

        changeSoFastDetector.setThreashold(0.2);
        changeSoFastDetector.setTrainStartTimestamp(Instant.now());
        changeSoFastDetector.setTrainEndTimestamp(Instant.now());
        changeSoFastDetector.setTestStartTimestamp(Instant.now().minus(10, ChronoUnit.DAYS));
        changeSoFastDetector.setTestEndTimestamp(Instant.now());
        changeSoFastDetector.loadStrategy();
        List<Report> spotReportList = changeSoFastDetector.detectSpotAnomaly(MarkServiceConstants.IntraContextList);
        List<Report> forwardReportList = changeSoFastDetector.detectForwardAnomaly(MarkServiceConstants.IntraContextList);

        remainUnchangedDetector.setThreshold(0.98);
        remainUnchangedDetector.setTrainStartTimestamp(Instant.now().minus(3, ChronoUnit.DAYS));
        remainUnchangedDetector.setTrainEndTimestamp(Instant.now());
        remainUnchangedDetector.setTestStartTimestamp(Instant.now().minus(3, ChronoUnit.DAYS));
        remainUnchangedDetector.setTestEndTimestamp(Instant.now());
        remainUnchangedDetector.loadStrategy();
        List<Report> spotReportList2 = remainUnchangedDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
        List<Report> forwardReportList2 = remainUnchangedDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);

//        ftlMail.getTemplate(regionReportList, changeSoFastReportList, remainUnchangedReportList);
    }
}
