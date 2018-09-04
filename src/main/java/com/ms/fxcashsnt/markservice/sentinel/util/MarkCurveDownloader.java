package com.ms.fxcashsnt.markservice.sentinel.util;

import com.ms.fxcashsnt.markservice.sentinel.model.MarkCurveQueryRequest;
import com.ms.fxcashsnt.markservice.sentinel.model.MarkCurveQueryResponse;
import com.ms.fxcashsnt.markservice.sentinel.dao.MarkCurveQueryResultDAO;
import msjava.base.messaging.ResponseContext;
import msjava.cxfutils.client.messaging.SOAPHDOMMessageSender;
import msjava.hdom.Document;
import msjava.hdom.messaging.HDOMRequestSender;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.interceptor.Fault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

/**
 * user: yandong.liu
 * date: 7/16/2018
 */
public class MarkCurveDownloader {
    /**
     * ktcp address of mark detector
     */
    private String markServiceAddress;
    /**
     * this folder is used to save mark curve history data
     */
    private String markCurveHistoryFolderPath;
    /**
     * the interval of sending query messages, measured in seconds
     */
    private int queryInterval;
    /**
     * whether the downloader is running. default value is true
     */
    private boolean running;
    /**
     * use this DAO to save mark curve query response into database
     */
    private MarkCurveQueryResultDAO markCurveQueryResultDAO;
    /**
     * lazy-init soap message sender
     */
    private HDOMRequestSender soapRequestSender;

    private ExecutorService executor;

    private static final Logger logger = LoggerFactory.getLogger(MarkCurveDownloader.class);

    public MarkCurveDownloader() {
        this.running = true;

    }

    public MarkCurveDownloader(String markServiceAddress, String markCurveHistoryFolderPath, int queryInterval, MarkCurveQueryResultDAO markCurveQueryResultDAO) {
        this.markServiceAddress = markServiceAddress;
        this.markCurveHistoryFolderPath = markCurveHistoryFolderPath;
        this.queryInterval = queryInterval;
        this.markCurveQueryResultDAO = markCurveQueryResultDAO;
        this.running = true;

    }

    public List<MarkCurveQueryResponse> downloadMarkCurve(List<String> contextList, List<LocalDate> positionDateList) {
        List<MarkCurveQueryResponse> markCurveQueryResponsesList = new ArrayList<>();
        try {
            for (LocalDate positionDate : positionDateList) {
                for (String context : contextList) {
                    Document requestDoc = new MarkCurveQueryRequest(positionDate, context).toHdomDocument();
                    logger.info("CREATE REQUEST DOCUMENT");
                    ResponseContext<Document> responseContext = getSoapRequestSender().request(requestDoc);
                    Document responseDoc = responseContext.getMessage();
                    logger.info("GET RESPONSE DOCUMENT");
                    MarkCurveQueryResponse markCurveQueryResponse = new MarkCurveQueryResponse(responseDoc);
                    logger.info("PARSE responseDoc to Object MarkCurveQueryResponse");
                    markCurveQueryResponsesList.add(markCurveQueryResponse);
                }
            }
        } catch (TimeoutException e) {
            logger.error(e.getMessage(), e);
        } catch (EndpointException e) {
            logger.error(e.getMessage(), e);
        } catch (org.apache.cxf.interceptor.Fault e) {
            logger.error(e.getMessage(), e);
        }
        return markCurveQueryResponsesList;
    }

    public void writeResponseListIntoDatabase(List<MarkCurveQueryResponse> markCurveQueryResponseList) {
        markCurveQueryResultDAO.batchSave(
                markCurveQueryResponseList.stream()
                        .flatMap(response -> response.getMarkCurveQueryResultMap().values().stream())
                        .collect(Collectors.toList())
        );
    }

    public void work() {
        while (running) {
            long start = System.currentTimeMillis();
            try {
                writeResponseListIntoDatabase(downloadMarkCurve(MarkServiceConstants.IntraContextList,
                        Collections.singletonList(LocalDate.now())));
                Thread.sleep(Math.max(0, queryInterval * 1000 - (System.currentTimeMillis() - start)));
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void workDaemon() {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> work());
        }
        System.out.println("I AM WORKING>>>>>>>>>>>>>>>>>");
    }

    /**
     * download and save endOfDay in the past 10 days
     */
    public void downloadEODMarkCurve() {
        LocalDate now = LocalDate.now();
        List<LocalDate> localDateList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            localDateList.add(now.minus(i, ChronoUnit.DAYS));
        }
        writeResponseListIntoDatabase(downloadMarkCurve(MarkServiceConstants.EndContextList, localDateList));
    }

    public void close() {
        this.running = false;
    }

    public String getMarkServiceAddress() {
        return markServiceAddress;
    }

    public void setMarkServiceAddress(String markServiceAddress) {
        this.markServiceAddress = markServiceAddress;
    }

    public String getMarkCurveHistoryFolderPath() {
        return markCurveHistoryFolderPath;
    }

    public void setMarkCurveHistoryFolderPath(String markCurveHistoryFolderPath) {
        this.markCurveHistoryFolderPath = markCurveHistoryFolderPath;
    }

    public int getQueryInterval() {
        return queryInterval;
    }

    public void setQueryInterval(int queryInterval) {
        this.queryInterval = queryInterval;
    }

    public MarkCurveQueryResultDAO getMarkCurveQueryResultDAO() {
        return markCurveQueryResultDAO;
    }

    public void setMarkCurveQueryResultDAO(MarkCurveQueryResultDAO markCurveQueryResultDAO) {
        this.markCurveQueryResultDAO = markCurveQueryResultDAO;
    }

    public HDOMRequestSender getSoapRequestSender() throws EndpointException {
        // lazy init and singleton
        if (soapRequestSender == null) {
            soapRequestSender = new SOAPHDOMMessageSender(markServiceAddress);
        }
        return soapRequestSender;
    }
}
