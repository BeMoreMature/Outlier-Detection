package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.model.report.ReportBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ReportBuilderTest {
    @Autowired
    private CrossRegionDetector crossRegionDetector;
    @Test
    public void testBuilder(){
        ReportBuilder reportBuilder = new ReportBuilder(crossRegionDetector);
        List<Report> list = reportBuilder.build(0.2,java.time.LocalDate.now());

    }
}
