package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.strategy.PythonStrategy;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * user: yandongl
 * date: 7/31/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DetectorTest {
    @Autowired
    ChangeSoFastDetector changeSoFastDetector;

    @Autowired
    RemainUnchangedDetector remainUnchangedDetector;

    @Autowired
    private EllipticEnvelopeDetector ellipticEnvelopeDetector;
//    @Test
    public void changeSoFastDetectorTest() {
        changeSoFastDetector.setThreashold(0.1);
        changeSoFastDetector.setTrainStartTimestamp(Instant.now());
        changeSoFastDetector.setTrainEndTimestamp(Instant.now());
        changeSoFastDetector.setTestStartTimestamp(Instant.now().minus(10, ChronoUnit.DAYS));
        changeSoFastDetector.setTestEndTimestamp(Instant.now());
        changeSoFastDetector.loadStrategy();
//        List<Report> reportList = changeSoFastDetector.detectSpotAnomaly(MarkServiceConstants.IntraContextList);
        List<Report> reportList = changeSoFastDetector.detectForwardAnomaly(MarkServiceConstants.IntraContextList);
        for(Report report : reportList){
            System.out.println(report.getCurrencyPair()+" "+report.getContext()+" "+report.getTenor());
            for (Point point : report.getPointList()){
                System.out.println("Point "+point.getValue()+" "+point.getTimestamp());
            }
        }
    }

    @Test
    public void remainUnchasngedDetectorTest() {
        remainUnchangedDetector.setThreshold(0.98);
        remainUnchangedDetector.setTrainStartTimestamp(Instant.now().minus(5, ChronoUnit.DAYS));
        remainUnchangedDetector.setTrainEndTimestamp(Instant.now());
        remainUnchangedDetector.setTestStartTimestamp(Instant.now().minus(5, ChronoUnit.DAYS));
        remainUnchangedDetector.setTestEndTimestamp(Instant.now());
        remainUnchangedDetector.loadStrategy();
        List<Report> reportList = remainUnchangedDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
        List<Report> reportList2 = remainUnchangedDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);
        System.out.println(reportList);
    }

    @Test
    public void timeReportBuilderTest(){
        changeSoFastDetector.setThreashold(0.2);
        changeSoFastDetector.setTrainStartTimestamp(Instant.now());
        changeSoFastDetector.setTrainEndTimestamp(Instant.now());
        changeSoFastDetector.setTestStartTimestamp(Instant.now().minus(10, ChronoUnit.DAYS));
        changeSoFastDetector.setTestEndTimestamp(Instant.now());
        changeSoFastDetector.loadStrategy();
        List<Report> spotReportList = changeSoFastDetector.detectSpotAnomaly(MarkServiceConstants.IntraContextList);
        List<Report> forwardReportList = changeSoFastDetector.detectForwardAnomaly(MarkServiceConstants.IntraContextList);


    }


    @Test
    public void EllipticEnvelopeDetectorTest() {
        ellipticEnvelopeDetector.setMaxReportSize(100);
        ellipticEnvelopeDetector.setTrainStartTimestamp(Instant.now().minus(60, ChronoUnit.DAYS));
        ellipticEnvelopeDetector.setTrainEndTimestamp(Instant.now().minus(0, ChronoUnit.DAYS));
        ellipticEnvelopeDetector.setTestStartTimestamp(Instant.now().minus(60, ChronoUnit.DAYS));
        ellipticEnvelopeDetector.setTestEndTimestamp(Instant.now());
        ellipticEnvelopeDetector.loadStrategy();

        List<Report> spotReportList = ellipticEnvelopeDetector.detectSpotAnomaly(MarkServiceConstants.IntraContextList);
//        List<Report> forwardReportList = ellipticEnvelopeDetector.detectForwardAnomaly(MarkServiceConstants.IntraContextList);
        ellipticEnvelopeDetector.close();
//        return spotReportList;
        System.out.println(spotReportList);
//        System.out.println(forwardReportList);
    }


//    @Test
    public void UniversalPythonStrategyTest() {
        String scriptPath = "C:\\Users\\yandongl\\scripts\\elliptic_envelope.py";
        PythonStrategy strategy = new PythonStrategy(scriptPath);
        strategy.initPythonProcess();
        List<Point> pointList = new LinkedList<>();
        pointList.add(new Point(1, Instant.now()));
        pointList.add(new Point(2, Instant.now()));
        strategy.fit(pointList);
        pointList.add(new Point(100, Instant.now()));
        List<Boolean> booleanList = strategy.predict(pointList);
        strategy.close();
        System.out.println(booleanList);
    }


    @Test
    public void emptyStringSplitTest() {
        String boolString = "";
        String[] boolStrings = boolString.split(",");
        System.out.println(boolStrings.length);

        System.out.println(Arrays.stream(boolStrings).map(flag -> flag.equals("1")).collect(Collectors.toList()));
    }

}
