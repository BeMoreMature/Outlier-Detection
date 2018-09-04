package com.ms.fxcashsnt.markservice.sentinel.mail;

import com.ms.fxcashsnt.markservice.sentinel.model.report.RegionReport;
import com.ms.fxcashsnt.markservice.sentinel.model.report.RegionReportBuilder;
import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
/**
 * user: Carl,Wu
 * date: 8/3/2018
 */
public class FTLMail {
    // directory for template
    public String path;
    public FTLMail(String path){
        this.path = path;
    }
    public Writer getTemplate(List<RegionReport> regionReportList, List<Report> changeSoFastReportList, List<Report> remainUnchangedReportList, List<Report> ellipticEnvelopReportList, List<Report> isolationForestReportList,
                              List<Report> oneClassSvmReportList, List<Report> localOutlierFactorReportList, List<Report> smoothedZScoreReportList){
        Writer out = new StringWriter();
        try {
            //Instantiate Configuration class
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
            cfg.setDirectoryForTemplateLoading(new File(path));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            //Create Data Model
            Map<String, Object> map = new HashMap<>();

            map.put("regionReportList", regionReportList);
            map.put("changeSoFastReportList",changeSoFastReportList);
            map.put("remainUnchangedReportList",remainUnchangedReportList);
            map.put("ellipticEnvelopReportList", ellipticEnvelopReportList);
            map.put("isolationForestReportList", isolationForestReportList);
            map.put("oneClassSvmReportList", oneClassSvmReportList);
            map.put("localOutlierFactorReportList", localOutlierFactorReportList);
            map.put("smoothedZScoreReportList", smoothedZScoreReportList);

            //Instantiate template
            Template template = cfg.getTemplate("mail-template.ftl");

            template.process(map, out);

            //Console output
//            Writer console = new OutputStreamWriter(System.out);
//            template.process(map, console);
//            console.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }catch (TemplateException e) {
            e.printStackTrace();
        }
        return out;
    }
}
