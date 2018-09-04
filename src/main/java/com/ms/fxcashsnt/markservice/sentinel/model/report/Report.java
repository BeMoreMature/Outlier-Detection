package com.ms.fxcashsnt.markservice.sentinel.model.report;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * user: yandongl
 * date: 7/31/2018
 * This class is used to save anomaly detector results and generate figures in the web browser.
 */
public class Report implements Serializable {
    private String currencyPair;
    private String context;
    private String tenor;

    /**
     * This score field is used to evaluate the rank of this report.
     */
    private double score;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone = "UTC")
    private Instant startTimestamp;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone = "UTC")
    private Instant endTimestamp;

    private List<Point> pointList;
    private List<Boolean> booleanList;

    public Report() {
    }

    public Report(String currencyPair, String context, String tenor, Instant startTimestamp, Instant endTimestamp, List<Point> pointList, List<Boolean> booleanList) {
        this.currencyPair = currencyPair;
        this.context = context;
        this.tenor = tenor;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.pointList = pointList;
        this.booleanList = booleanList;
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

    public String getTenor() {
        return tenor;
    }

    public void setTenor(String tenor) {
        this.tenor = tenor;
    }

    public Instant getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Instant startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Instant getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Instant endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public List<Boolean> getBooleanList() {
        return booleanList;
    }

    public void setBooleanList(List<Boolean> booleanList) {
        this.booleanList = booleanList;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
