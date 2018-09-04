package com.ms.fxcashsnt.markservice.sentinel.model.spot;

import com.ms.fxcashsnt.markservice.sentinel.dao.SpotCurveDAO;
import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * user: yandongl
 * date: 7/30/2018
 */
public class SpotDataSet {
    private String currencyPair;
    private String context;

    private Instant trainStartTimestamp;
    private Instant trainEndTimestamp;

    private Instant testStartTimestamp;
    private Instant testEndTimestamp;

    private List<Point> trainPointList;
    private List<Point> testPointList;

    public SpotDataSet(String currencyPair, String context, Instant trainStartTimestamp, Instant trainEndTimestamp, Instant testStartTimestamp, Instant testEndTimestamp) {
        this.currencyPair = currencyPair;
        this.context = context;
        this.trainStartTimestamp = trainStartTimestamp;
        this.trainEndTimestamp = trainEndTimestamp;
        this.testStartTimestamp = testStartTimestamp;
        this.testEndTimestamp = testEndTimestamp;
    }

    public void setTrainPointList(List<Point> trainPointList) {
        this.trainPointList = trainPointList;
    }

    public void setTestPointList(List<Point> testPointList) {
        this.testPointList = testPointList;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public String getContext() {
        return context;
    }

    public Instant getTrainStartTimestamp() {
        return trainStartTimestamp;
    }

    public Instant getTrainEndTimestamp() {
        return trainEndTimestamp;
    }

    public Instant getTestStartTimestamp() {
        return testStartTimestamp;
    }

    public Instant getTestEndTimestamp() {
        return testEndTimestamp;
    }

    public List<Point> getTrainPointList() {
        return trainPointList;
    }

    public List<Point> getTestPointList() {
        return testPointList;
    }


}


