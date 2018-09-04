package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.util.DetectorConstants;
import com.ms.fxcashsnt.markservice.sentinel.util.ReportCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * user: yandongl
 * date: 8/20/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ReportCacheTest {
    @Autowired
    private ReportCache reportCache;

    @Test
    public void compressTest() {
        if (reportCache.isEmpty()) reportCache.getReportListMap().put(DetectorConstants.ELLIPTIC_ENVELOPE, reportCache.loadRemainUnchangedReportList());
        reportCache.compress(5);
    }


    @Test
    public void dumpAndLoadTest() {
        reportCache.getReportListMap().put("aaa", null);
        reportCache.getReportListMap().put("bbb", null);
        reportCache.dump();
        reportCache.getReportListMap().clear();

        reportCache.tryLoad();
        reportCache.getReportListMap().keySet().forEach(System.out::println);
    }
}
