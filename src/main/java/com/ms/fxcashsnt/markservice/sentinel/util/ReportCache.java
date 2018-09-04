package com.ms.fxcashsnt.markservice.sentinel.util;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.model.report.ReportBuilder;
import com.ms.fxcashsnt.markservice.sentinel.detector.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * user: yandongl
 * date: 8/7/2018
 */
@Component(value = "reportCache")
public class ReportCache {
    @Autowired
    private CrossRegionDetector crossRegionDetector;
    @Autowired
    private ChangeSoFastDetector changeSoFastDetector;
    @Autowired
    private RemainUnchangedDetector remainUnchangedDetector;
    @Autowired
    private EllipticEnvelopeDetector ellipticEnvelopeDetector;
    @Autowired
    private IsolationForestDetector isolationForestDetector;
    @Autowired
    private OneClassSvmDetector oneClassSvmDetector;
    @Autowired
    private LocalOutlierFactorDetector localOutlierFactorDetector;
    @Autowired
    private  SmoothedZScoreDetector smoothedZScoreDetector;

    private Map<String, List<Report>> reportListMap;
    private ExecutorService executor;
    private static final Logger logger = LoggerFactory.getLogger(MarkCurveDownloader.class);

    public ReportCache() {
        reportListMap = new HashMap<>();
    }

    public void refreshAll() {
        reportListMap.put(DetectorConstants.CHANGE_SO_FAST, loadChangeSoFastReportList());
        reportListMap.put(DetectorConstants.REMAIN_UNCHANGED, loadRemainUnchangedReportList());
        reportListMap.put(DetectorConstants.CROSS_REGION, loadCrossReginReportList());
        reportListMap.put(DetectorConstants.ELLIPTIC_ENVELOPE, loadEllipticEnvelopeReportList());
        reportListMap.put(DetectorConstants.ISOLATION_FOREST, loadIsolationForestReportList());
        reportListMap.put(DetectorConstants.ONE_CLASS_SVM, loadOneClassSvmReportList());
        reportListMap.put(DetectorConstants.LOCAL_OUTLIER_FACTOR, loadLocalOutlierFactorReportList());
        reportListMap.put(DetectorConstants.SMOOTHED_Z_SCORE, loadSmoothedZScoreReportList());
    }

    public void refreshAllConcurrent() throws InterruptedException {
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
            executor.submit(() -> reportListMap.put(DetectorConstants.CHANGE_SO_FAST, loadChangeSoFastReportList()));
            executor.submit(() -> reportListMap.put(DetectorConstants.REMAIN_UNCHANGED, loadRemainUnchangedReportList()));
            executor.submit(() -> reportListMap.put(DetectorConstants.CROSS_REGION, loadCrossReginReportList()));
            executor.submit(() -> reportListMap.put(DetectorConstants.ELLIPTIC_ENVELOPE, loadEllipticEnvelopeReportList()));
            executor.submit(() -> reportListMap.put(DetectorConstants.ISOLATION_FOREST, loadIsolationForestReportList()));
            executor.submit(() -> reportListMap.put(DetectorConstants.ONE_CLASS_SVM, loadOneClassSvmReportList()));
            executor.submit(() -> reportListMap.put(DetectorConstants.LOCAL_OUTLIER_FACTOR, loadLocalOutlierFactorReportList()));
            executor.submit(() -> reportListMap.put(DetectorConstants.SMOOTHED_Z_SCORE, loadSmoothedZScoreReportList()));
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
            executor = null;
        }
    }

    public List<Report> loadChangeSoFastReportList() {
        changeSoFastDetector.setThreashold(0.2);
        changeSoFastDetector.setTrainStartTimestamp(Instant.now());
        changeSoFastDetector.setTrainEndTimestamp(Instant.now());
        changeSoFastDetector.setTestStartTimestamp(Instant.now().minus(5010, ChronoUnit.DAYS));
        changeSoFastDetector.setTestEndTimestamp(Instant.now());
        changeSoFastDetector.loadStrategy();
        List<Report> spotReportList = changeSoFastDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
//        List<Report> forwardReportList = changeSoFastDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);
        return spotReportList;

//        return Stream.concat(spotReportList.stream(), forwardReportList.stream()).collect(Collectors.toList());
    }

    public List<Report> loadRemainUnchangedReportList() {
        remainUnchangedDetector.setThreshold(0.98);
        remainUnchangedDetector.setTrainStartTimestamp(Instant.now().minus(30, ChronoUnit.DAYS));
        remainUnchangedDetector.setTrainEndTimestamp(Instant.now());
        remainUnchangedDetector.setTestStartTimestamp(Instant.now().minus(30, ChronoUnit.DAYS));
        remainUnchangedDetector.setTestEndTimestamp(Instant.now());
        remainUnchangedDetector.loadStrategy();
        List<Report> spotReportList = remainUnchangedDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
        List<Report> forwardReportList = remainUnchangedDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);

        return Stream.concat(spotReportList.stream(), forwardReportList.stream()).collect(Collectors.toList());
    }

    public List<Report> loadCrossReginReportList() {
        ReportBuilder reportBuilder = new ReportBuilder(crossRegionDetector);
        List<Report> reportList = new LinkedList<>();
        for (int i = 1; i < 3; i++) {
            reportList.addAll(reportBuilder.build(0.2, LocalDate.now().minus(i, ChronoUnit.DAYS)));
        }
        return reportList;
    }


    private Report compressReprot(Report report, int maxSize) {
        if (report.getPointList().size() < maxSize) return report;
        List<Point> pointList = new LinkedList<>();
        List<Boolean> booleanList = new LinkedList<>();
        // remain unchanged detecter's report
        if (report.getBooleanList().stream().allMatch(b -> b)) {
            int step = report.getPointList().size() / maxSize;
            for (int i = 0; i < report.getBooleanList().size(); i++) {
                if (i % step == 0) {
                    pointList.add(report.getPointList().get(i));
                    booleanList.add(report.getBooleanList().get(i));
                }
            }
            report.setPointList(pointList);
            report.setBooleanList(booleanList);
            return report;
        }

        int step = report.getPointList().size() / maxSize;
        int cnt = 0;
        for (int i = 0; i < report.getPointList().size(); i++) {
            cnt++;
            // if this point is a outlier, we keep it
            if (report.getBooleanList().get(i)) {
                // if this point remain unchanged, skip it
                if (cnt < step && i - 1 >= 0 && i + 1 < report.getBooleanList().size() && report.getBooleanList().get(i - 1) && report.getBooleanList().get(i + 1)
                        && Utility.equals(report.getPointList().get(i - 1).getValue(), report.getPointList().get(i).getValue(), 1e-5)
                        && Utility.equals(report.getPointList().get(i + 1).getValue(), report.getPointList().get(i).getValue(), 1e-5))
                    continue;
                cnt = 0;
                pointList.add(report.getPointList().get(i));
                booleanList.add(report.getBooleanList().get(i));
            } else {
                if (cnt >= step) {
                    cnt = 0;
                    pointList.add(report.getPointList().get(i));
                    booleanList.add(report.getBooleanList().get(i));
                }
            }

        }
        report.setPointList(pointList);
        report.setBooleanList(booleanList);
        return report;
    }


    public void compress(int maxSize) {
        for (String detector : reportListMap.keySet()) {
            List<Report> reportList = new LinkedList<>();
            for (Report report : reportListMap.get(detector)) {
                reportList.add(compressReprot(report, maxSize));
            }
            reportListMap.put(detector, reportList);
        }
    }

    public List<Report> loadEllipticEnvelopeReportList() {

        ellipticEnvelopeDetector.setMaxReportSize(50);
        ellipticEnvelopeDetector.setTrainStartTimestamp(Instant.now().minus(5000, ChronoUnit.DAYS));
        ellipticEnvelopeDetector.setTrainEndTimestamp(Instant.now().minus(0, ChronoUnit.DAYS));
        ellipticEnvelopeDetector.setTestStartTimestamp(Instant.now().minus(5000, ChronoUnit.DAYS));
        ellipticEnvelopeDetector.setTestEndTimestamp(Instant.now());
        ellipticEnvelopeDetector.loadStrategy();

        List<Report> spotReportList = ellipticEnvelopeDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
        List<Report> forwardReportList = ellipticEnvelopeDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);
        ellipticEnvelopeDetector.close();
        return Stream.concat(spotReportList.stream(), forwardReportList.stream()).collect(Collectors.toList());
//        return spotReportList;
    }

    public List<Report> loadIsolationForestReportList() {
        isolationForestDetector.setMaxReportSize(50);
        isolationForestDetector.setTrainStartTimestamp(Instant.now().minus(5001, ChronoUnit.DAYS));
        isolationForestDetector.setTrainEndTimestamp(Instant.now().minus(0, ChronoUnit.DAYS));
        isolationForestDetector.setTestStartTimestamp(Instant.now().minus(5000, ChronoUnit.DAYS));
        isolationForestDetector.setTestEndTimestamp(Instant.now());
        isolationForestDetector.loadStrategy();

        List<Report> spotReportList = isolationForestDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
        List<Report> forwardReportList = isolationForestDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);
        isolationForestDetector.close();
        return Stream.concat(spotReportList.stream(), forwardReportList.stream()).collect(Collectors.toList());
//        return spotReportList;
    }

    public List<Report> loadOneClassSvmReportList() {
        oneClassSvmDetector.setMaxReportSize(50);
        oneClassSvmDetector.setTrainStartTimestamp(Instant.now().minus(5005, ChronoUnit.DAYS));
        oneClassSvmDetector.setTrainEndTimestamp(Instant.now().minus(0, ChronoUnit.DAYS));
        oneClassSvmDetector.setTestStartTimestamp(Instant.now().minus(5000, ChronoUnit.DAYS));
        oneClassSvmDetector.setTestEndTimestamp(Instant.now());
        oneClassSvmDetector.loadStrategy();

        List<Report> spotReportList = oneClassSvmDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
        List<Report> forwardReportList = oneClassSvmDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);
        oneClassSvmDetector.close();
        return Stream.concat(spotReportList.stream(), forwardReportList.stream()).collect(Collectors.toList());
//        return spotReportList;
    }

    public List<Report> loadLocalOutlierFactorReportList() {
        localOutlierFactorDetector.setMaxReportSize(50);
        localOutlierFactorDetector.setTrainStartTimestamp(Instant.now().minus(5003, ChronoUnit.DAYS));
        localOutlierFactorDetector.setTrainEndTimestamp(Instant.now().minus(0, ChronoUnit.DAYS));
        localOutlierFactorDetector.setTestStartTimestamp(Instant.now().minus(5000, ChronoUnit.DAYS));
        localOutlierFactorDetector.setTestEndTimestamp(Instant.now());
        localOutlierFactorDetector.loadStrategy();

        List<Report> spotReportList = localOutlierFactorDetector.detectSpotAnomaly(MarkServiceConstants.EndContextList);
        List<Report> forwardReportList = localOutlierFactorDetector.detectForwardAnomaly(MarkServiceConstants.EndContextList);
        localOutlierFactorDetector.close();
        return Stream.concat(spotReportList.stream(), forwardReportList.stream()).collect(Collectors.toList());
//        return spotReportList;
    }

    public List<Report> loadSmoothedZScoreReportList(){
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
        return Stream.concat(spotReportList.stream(), forwardReportList.stream()).collect(Collectors.toList());
    }
    public void dump() {
        try {
//            FileOutputStream fos = new FileOutputStream("src\\main\\resources\\report_cache.ser");
            FileOutputStream fos = new FileOutputStream("C:\\Users\\yandongl\\Documents\\My Documents\\sentinel\\sentinel\\trunk\\src\\src\\main\\resources\\report_cache.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            logger.info("Dump report cache into the file system.");
            oos.writeObject(reportListMap);
            oos.close();
        } catch (IOException e) {
            logger.error("Cannot save report cache into file.", e);
        }
    }

    public void tryLoad() {
        if (new File("C:\\Users\\yandongl\\Documents\\My Documents\\sentinel\\sentinel\\trunk\\src\\src\\main\\resources\\report_cache.ser").exists()) {
            try {
                FileInputStream fis = new FileInputStream("C:\\Users\\yandongl\\Documents\\My Documents\\sentinel\\sentinel\\trunk\\src\\src\\main\\resources\\report_cache.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                logger.info("Loading report cache from the file system.");
                reportListMap = (Map<String, List<Report>>) ois.readObject();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Cannot load report cache from file.", e);
            }
        }
    }

    public List<Report> get(String key) {
        return reportListMap.get(key);
    }

    public boolean isEmpty() {
        return reportListMap.isEmpty();
    }

    public void setCrossRegionDetector(CrossRegionDetector crossRegionDetector) {
        this.crossRegionDetector = crossRegionDetector;
    }

    public CrossRegionDetector getCrossRegionDetector() {

        return crossRegionDetector;
    }

    public ChangeSoFastDetector getChangeSoFastDetector() {
        return changeSoFastDetector;
    }

    public void setChangeSoFastDetector(ChangeSoFastDetector changeSoFastDetector) {
        this.changeSoFastDetector = changeSoFastDetector;
    }

    public RemainUnchangedDetector getRemainUnchangedDetector() {
        return remainUnchangedDetector;
    }

    public Map<String, List<Report>> getReportListMap() {
        return reportListMap;
    }

    public void setReportListMap(Map<String, List<Report>> reportListMap) {
        this.reportListMap = reportListMap;
    }

    public void setRemainUnchangedDetector(RemainUnchangedDetector remainUnchangedDetector) {
        this.remainUnchangedDetector = remainUnchangedDetector;
    }
}
