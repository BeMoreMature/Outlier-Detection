package com.ms.fxcashsnt.markservice.sentinel;

import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import weka.classifiers.trees.J48;

/**
 * user: yandong.liu
 * date: 7/18/2018
 */
public class Main {
    public static void main(String[] args) {
        String springConfigXML = "spring-core.xml";
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(springConfigXML);

        MarkCurveDownloader markCurveDownloader = (MarkCurveDownloader) context.getBean("markCurveDownloader");
        markCurveDownloader.downloadEODMarkCurve();
        markCurveDownloader.work();
    }
}
