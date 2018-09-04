package com.ms.fxcashsnt.markservice.sentinel.email;

import com.ms.fxcashsnt.markservice.sentinel.detector.CrossRegionDetector;
import com.ms.fxcashsnt.markservice.sentinel.mail.FTLMail;
import com.ms.fxcashsnt.markservice.sentinel.mail.EmailNotifier;
import com.ms.fxcashsnt.markservice.sentinel.mail.Region;
import com.ms.fxcashsnt.markservice.sentinel.model.report.*;
import com.ms.fxcashsnt.markservice.sentinel.model.view.Detector;
import com.ms.fxcashsnt.markservice.sentinel.detector.ChangeSoFastDetector;
import com.ms.fxcashsnt.markservice.sentinel.detector.RemainUnchangedDetector;
import com.ms.fxcashsnt.markservice.sentinel.util.DetectorConstants;
import com.ms.fxcashsnt.markservice.sentinel.util.ReportCache;
import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.Session;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
/**
 * user: Carl,Wu
 * date: 8/3/2018
 */
//@ContextConfiguration(locations = {"classpath:spring-core.xml"})
//@RunWith(SpringJUnit4ClassRunner.class)
public class mailSenderTest {
    @Autowired
    private EmailNotifier emailNotifier;
    @Autowired
    private CrossRegionDetector crossRegionDetector;
    @Autowired
    private ChangeSoFastDetector changeSoFastDetector;
    @Autowired
    private RemainUnchangedDetector remainUnchangedDetector;
    @Autowired
    private FTLMail ftlMail;
    @Autowired
    private ReportCache reportCache;

//    @Test
    public void sendEmailTest(){
        System.out.println("SimpleEmail Start");

        String smtpHostServer = "mta-hub";
//        String emailID = "Carl.Wu@morganstanley.com";

        Properties props = System.getProperties();

        props.put("mail.smtp.host", smtpHostServer);

        Session session = Session.getInstance(props, null);

        RegionReportBuilder regionReportBuilder = new RegionReportBuilder(crossRegionDetector);
        List<RegionReport> regionReportList = regionReportBuilder.build(0.1, java.time.LocalDate.now());

        if (reportCache.isEmpty()) {
            reportCache.refreshAll();
        }
        List<Detector> detectorList = new LinkedList<>();
        detectorList.add(new Detector(1, DetectorConstants.CHANGE_SO_FAST, reportCache.get(DetectorConstants.CHANGE_SO_FAST)));
        detectorList.add(new Detector(2, DetectorConstants.REMAIN_UNCHANGED, reportCache.get(DetectorConstants.REMAIN_UNCHANGED)));
        detectorList.add(new Detector(3, DetectorConstants.CROSS_REGION, reportCache.get(DetectorConstants.CROSS_REGION)));
        detectorList.add(new Detector(4, DetectorConstants.ELLIPTIC_ENVELOPE, reportCache.get(DetectorConstants.ELLIPTIC_ENVELOPE)));
        detectorList.add(new Detector(5, DetectorConstants.ISOLATION_FOREST, reportCache.get(DetectorConstants.ISOLATION_FOREST)));
        detectorList.add(new Detector(6, DetectorConstants.ONE_CLASS_SVM, reportCache.get(DetectorConstants.ONE_CLASS_SVM)));
        detectorList.add(new Detector(7, DetectorConstants.LOCAL_OUTLIER_FACTOR, reportCache.get(DetectorConstants.LOCAL_OUTLIER_FACTOR)));
        detectorList.add(new Detector(8,DetectorConstants.SMOOTHED_Z_SCORE, reportCache.get(DetectorConstants.SMOOTHED_Z_SCORE)));

        emailNotifier.sendEmail(session, Region.SH,LocalDate.now(), ftlMail.getTemplate(regionReportList, detectorList.get(0).getReports(),detectorList.get(1).getReports(), detectorList.get(3).getReports(),detectorList.get(4).getReports(),
                detectorList.get(5).getReports(), detectorList.get(6).getReports(), detectorList.get(7).getReports()));
    }
}
