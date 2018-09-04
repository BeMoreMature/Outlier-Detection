package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.dao.ForwardCurveDAO;
import com.ms.fxcashsnt.markservice.sentinel.dao.SpotCurveDAO;
import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardDataSet;
import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardDataSetBuilder;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotDataSet;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotDataSetBuilder;
import com.ms.fxcashsnt.markservice.sentinel.strategy.Strategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * user: yandongl
 * date: 7/31/2018
 */
public abstract class AbstractAbnormalityDetector {
    protected String currencyPair;
    protected String context;
    protected Instant trainStartTimestamp;
    protected Instant trainEndTimestamp;
    protected Instant testStartTimestamp;
    protected Instant testEndTimestamp;

    protected Strategy strategy;
    protected SpotDataSet spotDataSet;
    protected ForwardDataSet forwardDataSet;

    @Autowired
    protected SpotDataSetBuilder spotDataSetBuilder;
    @Autowired
    protected ForwardDataSetBuilder forwardDataSetBuilder;
    @Autowired
    protected SpotCurveDAO spotCurveDAO;
    @Autowired
    protected ForwardCurveDAO forwardCurveDAO;

    public AbstractAbnormalityDetector() {
    }

    protected SpotDataSet loadSpotDataSet() {
        return spotDataSetBuilder
                .setCurrencyPair(currencyPair)
                .setContext(context)
                .setTrainStartTimestamp(trainStartTimestamp)
                .setTrainEndTimestamp(trainEndTimestamp)
                .setTestStartTimestamp(testStartTimestamp)
                .setTestEndTimestamp(testEndTimestamp).build();
    }

    protected ForwardDataSet loadForwardDataSet() {
        return forwardDataSetBuilder
                .setCurrencyPair(currencyPair)
                .setContext(context)
                .setTrainStartTimestamp(trainStartTimestamp)
                .setTrainEndTimestamp(trainEndTimestamp)
                .setTestStartTimestamp(testStartTimestamp)
                .setTestEndTimestamp(testEndTimestamp).build();
    }

    public List<Report> detectSpotAnomaly(List<String> contextList) {
        List<Report> reportList = new LinkedList<>();
        List<String> currencyPairList = spotCurveDAO.queryUniqueCurrencyPair();
        for (String currencyPair: currencyPairList) {
            for (String context: contextList) {
                this.setCurrencyPair(currencyPair);
                this.setContext(context);
                List<Boolean> anomalyBooleanList = getSpotAnomalyBooleanList();
                if(isNeedReport(anomalyBooleanList)) {
                   reportList.add(new Report(
                           currencyPair, context, "SPOT", testStartTimestamp, testEndTimestamp,
                           this.spotDataSet.getTestPointList(), anomalyBooleanList
                   ));
                }
            }
        }
        return reportList;
    }
    public List<Report> detectForwardAnomaly(List<String> contextList) {
        List<Report> reportList = new LinkedList<>();
        List<String> currencyPairList = forwardCurveDAO.queryUniqueCurrencyPair();
        for (String currencyPair: currencyPairList) {
            for (String context: contextList) {
                this.setCurrencyPair(currencyPair);
                this.setContext(context);
                Map<String, List<Boolean>> forwardAnomalyBooleanListMap = getForwardAnomalyBooleanListMap();
                for (String tenor: forwardAnomalyBooleanListMap.keySet()) {
                    if (isNeedReport(forwardAnomalyBooleanListMap.get(tenor))) {
                        reportList.add(new Report(
                                currencyPair, context, tenor, testStartTimestamp, testEndTimestamp,
                                this.forwardDataSet.getTestPointListMap().get(tenor), forwardAnomalyBooleanListMap.get(tenor)
                        ));
                    }
                }
            }
        }
        return reportList;
    }

    abstract void loadStrategy();

    abstract boolean isNeedReport(List<Boolean> anomalyBooleanList);

    public List<Boolean> getSpotAnomalyBooleanList() {
        this.spotDataSet = loadSpotDataSet();
        return strategy.fit(spotDataSet.getTrainPointList()).predict(spotDataSet.getTestPointList());
    }

    public Map<String, List<Boolean>> getForwardAnomalyBooleanListMap() {
        this.forwardDataSet = loadForwardDataSet();
        Map<String, List<Boolean>> forwardAnomalyBooleanListMap = new HashMap<>();
        for (String tenor: forwardDataSet.getTestPointListMap().keySet()) {
            forwardAnomalyBooleanListMap.put(tenor,
                    strategy.fit(forwardDataSet.getTrainPointListMap().get(tenor))
                            .predict(forwardDataSet.getTestPointListMap().get(tenor))
            );
        }
        return forwardAnomalyBooleanListMap;
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

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public SpotDataSetBuilder getSpotDataSetBuilder() {
        return spotDataSetBuilder;
    }

    public void setSpotDataSetBuilder(SpotDataSetBuilder spotDataSetBuilder) {
        this.spotDataSetBuilder = spotDataSetBuilder;
    }
}
