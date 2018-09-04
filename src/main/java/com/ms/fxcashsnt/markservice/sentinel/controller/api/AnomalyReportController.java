package com.ms.fxcashsnt.markservice.sentinel.controller.api;

import com.ms.fxcashsnt.markservice.sentinel.model.view.Detector;
import com.ms.fxcashsnt.markservice.sentinel.util.DetectorConstants;
import com.ms.fxcashsnt.markservice.sentinel.util.ReportCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * user: yandongl
 * date: 8/7/2018
 */
@RestController
@RequestMapping("sentinel/api")
public class AnomalyReportController {
    @Autowired
    private ReportCache reportCache;

    private Logger logger = LoggerFactory.getLogger(AnomalyReportController.class);

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/detector", produces = "application/json")
    public List<Detector> getDetectors() {
        List<Detector> detectorList = new LinkedList<>();

//
        if (reportCache.isEmpty()) {
            reportCache.tryLoad();
            reportCache.getReportListMap().put(DetectorConstants.CROSS_REGION, reportCache.loadCrossReginReportList());
            if (reportCache.isEmpty()) {
                try {
                    reportCache.refreshAllConcurrent();
//                    reportCache.refreshAll();
                    reportCache.compress(200);
                    reportCache.dump();
                } catch (InterruptedException e) {
                    logger.error("Cannot get detector reports in time.");
                }
            }
        }

        reportCache.compress(200);
        detectorList.add(new Detector(1, DetectorConstants.CHANGE_SO_FAST, reportCache.get(DetectorConstants.CHANGE_SO_FAST)));
        detectorList.add(new Detector(2, DetectorConstants.REMAIN_UNCHANGED, reportCache.get(DetectorConstants.REMAIN_UNCHANGED)));
        detectorList.add(new Detector(3, DetectorConstants.CROSS_REGION, reportCache.get(DetectorConstants.CROSS_REGION)));
        detectorList.add(new Detector(4, DetectorConstants.ELLIPTIC_ENVELOPE, reportCache.get(DetectorConstants.ELLIPTIC_ENVELOPE)));
        detectorList.add(new Detector(5, DetectorConstants.ISOLATION_FOREST, reportCache.get(DetectorConstants.ISOLATION_FOREST)));
        detectorList.add(new Detector(6, DetectorConstants.ONE_CLASS_SVM, reportCache.get(DetectorConstants.ONE_CLASS_SVM)));
        detectorList.add(new Detector(7, DetectorConstants.LOCAL_OUTLIER_FACTOR, reportCache.get(DetectorConstants.LOCAL_OUTLIER_FACTOR)));
        detectorList.add(new Detector(8, DetectorConstants.SMOOTHED_Z_SCORE, reportCache.get(DetectorConstants.SMOOTHED_Z_SCORE)));
        return detectorList;
    }


}
