package com.ms.fxcashsnt.markservice.sentinel.model.forward;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * user: yandongl
 * date: 7/30/2018
 */
public class ForwardDataSet {
    private String currencyPair;
    private String context;

    private Instant trainStartTimestamp;
    private Instant trainEndTimestamp;

    private Instant testStartTimestamp;
    private Instant testEndTimestamp;

    private Map<String, List<Point>> trainPointListMap;
    private Map<String, List<Point>> testPointListMap;

    public ForwardDataSet(String currencyPair, String context, Instant trainStartTimestamp, Instant trainEndTimestamp, Instant testStartTimestamp, Instant testEndTimestamp) {
        this.currencyPair = currencyPair;
        this.context = context;
        this.trainStartTimestamp = trainStartTimestamp;
        this.trainEndTimestamp = trainEndTimestamp;
        this.testStartTimestamp = testStartTimestamp;
        this.testEndTimestamp = testEndTimestamp;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Instant getTrainStartTimestamp() {
        return trainStartTimestamp;
    }

    public void setTrainStartTimestamp(Instant trainStartTimestamp) {
        this.trainStartTimestamp = trainStartTimestamp;
    }

    public Instant getTrainEndTimestamp() {
        return trainEndTimestamp;
    }

    public void setTrainEndTimestamp(Instant trainEndTimestamp) {
        this.trainEndTimestamp = trainEndTimestamp;
    }

    public Instant getTestStartTimestamp() {
        return testStartTimestamp;
    }

    public void setTestStartTimestamp(Instant testStartTimestamp) {
        this.testStartTimestamp = testStartTimestamp;
    }

    public Instant getTestEndTimestamp() {
        return testEndTimestamp;
    }

    public void setTestEndTimestamp(Instant testEndTimestamp) {
        this.testEndTimestamp = testEndTimestamp;
    }

    public Map<String, List<Point>> getTrainPointListMap() {
        return trainPointListMap;
    }

    public void setTrainPointListMap(Map<String, List<Point>> trainPointListMap) {
        this.trainPointListMap = trainPointListMap;
    }

    public Map<String, List<Point>> getTestPointListMap() {
        return testPointListMap;
    }

    public void setTestPointListMap(Map<String, List<Point>> testPointListMap) {
        this.testPointListMap = testPointListMap;
    }
}
