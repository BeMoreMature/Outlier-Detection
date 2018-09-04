package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.strategy.Strategy;
import com.ms.fxcashsnt.markservice.sentinel.strategy.PythonStrategy;
import com.ms.fxcashsnt.markservice.sentinel.util.Utility;

import java.util.*;
import java.util.stream.Collectors;

/**
 * user: yandongl
 * date: 8/16/2018
 */
public abstract class AbstractPythonAbnormalityDetector extends AbstractAbnormalityDetector {

    private int maxReportSize;
    private Map<String, List<Double>> forwardAnomalyDecisionListMap;

    // this function is not used in python based detector
    @Override
    public boolean isNeedReport(List<Boolean> anomalyBooleanList) {
        return false;
    }

    public void loadStrategy(String scriptPath) {
        Strategy strategy = new PythonStrategy(scriptPath);
        ((PythonStrategy) strategy).initPythonProcess();
        super.setStrategy(strategy);
    }

    @Override
    public List<Report> detectSpotAnomaly(List<String> contextList) {
        Queue<Report> reportQueue = new PriorityQueue<>(maxReportSize * 4, new Comparator<Report>() {
            @Override
            public int compare(Report o1, Report o2) {
                if (o1.getScore() > o2.getScore()) {
                    return -1;
                } else return 1;
            }
        });
        List<String> currencyPairList = spotCurveDAO.queryUniqueCurrencyPair();
        for (String currencyPair : currencyPairList) {
            for (String context : contextList) {
                this.setCurrencyPair(currencyPair);
                this.setContext(context);
//                System.out.println(currencyPair + " " + context);
                List<Boolean> anomalyBooleanList = getSpotAnomalyBooleanList();
                // does not send data to the python process, skip and continue
                if (anomalyBooleanList.size() == 0) continue;
                List<Double> decisionList = ((PythonStrategy) strategy).getDecisionValues();
                double score = 0;
                int count = 0;
                for (int i = 0; i < anomalyBooleanList.size(); i++) {
                    if (anomalyBooleanList.get(i)) {
                        score += decisionList.get(i);
                        count += 1;
                    }
                }
                // which means there is no outlier in this currency, skip it and  continue this loop.
                if (count == 0) continue;
                // calculate the average score.
                score = score / count;
                Report report = new Report(
                        currencyPair, context, "SPOT", testStartTimestamp, testEndTimestamp,
                        this.spotDataSet.getTestPointList(), anomalyBooleanList
                );
                report.setScore(score);
                reportQueue.add(report);
                if (reportQueue.size() > maxReportSize * 4) reportQueue.poll();
            }
        }
        List<Report> reportList = new ArrayList<>(reportQueue);
        Collections.sort(reportList, Comparator.comparing(Report::getScore));
        Set<String> seen = new HashSet<>();
        return reportList.stream().filter(r -> seen.add(r.getCurrencyPair())).limit(maxReportSize).collect(Collectors.toList());
    }


    @Override
    public List<Report> detectForwardAnomaly(List<String> contextList) {
        Queue<Report> reportQueue = new PriorityQueue<>(maxReportSize * 4, new Comparator<Report>() {
            @Override
            public int compare(Report o1, Report o2) {
                if (o1.getScore() > o2.getScore()) {
                    return -1;
                } else return 1;
            }
        });
        List<String> currencyPairList = spotCurveDAO.queryUniqueCurrencyPair();
        for (String currencyPair : currencyPairList) {
            for (String context : contextList) {
                this.setCurrencyPair(currencyPair);
                this.setContext(context);
                Map<String, List<Boolean>> forwardAnomalyBooleanListMap = getForwardAnomalyBooleanListMap();
                for (String tenor : forwardAnomalyBooleanListMap.keySet()) {
                    List<Boolean> anomalyBooleanList = forwardAnomalyBooleanListMap.get(tenor);
                    List<Double> decisionList = forwardAnomalyDecisionListMap.get(tenor);
                    double score = 0;
                    int count = 0;
                    for (int i = 0; i < anomalyBooleanList.size(); i++) {
                        if (anomalyBooleanList.get(i)) {
                            score += decisionList.get(i);
                            count += 1;
                        }
                    }
                    // which means there is no outlier in this currency, skip it and  continue this loop.
                    if (count == 0) continue;
                    // calculate the average score.
                    score = score / count;
                    Report report = new Report(
                            currencyPair, context, tenor, testStartTimestamp, testEndTimestamp,
                            this.forwardDataSet.getTestPointListMap().get(tenor), forwardAnomalyBooleanListMap.get(tenor)
                    );
                    report.setScore(score);
                    reportQueue.add(report);
                    if (reportQueue.size() > maxReportSize * 4) reportQueue.poll();
                }
            }
        }
        List<Report> reportList = new ArrayList<>(reportQueue);
        Collections.sort(reportList, Comparator.comparing(Report::getScore));
        Set<String> seen = new HashSet<>();
        return reportList.stream().filter(r -> seen.add(r.getCurrencyPair() + r.getTenor())).limit(maxReportSize).collect(Collectors.toList());
    }

    @Override
    public List<Boolean> getSpotAnomalyBooleanList() {
        this.spotDataSet = loadSpotDataSet();
        // check train dataset and test data set. if one of them is too small, skip
        // check if the coefficient of variation
        if (spotDataSet.getTrainPointList().size() < 100 || spotDataSet.getTestPointList().size() < 100
                || Utility.coefficientOfVariationOfPointList(spotDataSet.getTrainPointList()) < 0.01
                || Utility.coefficientOfVariationOfPointList(spotDataSet.getTestPointList()) < 0.01)
            return new LinkedList<>();
        return strategy.fit(spotDataSet.getTrainPointList()).predict(spotDataSet.getTestPointList());
    }


    @Override
    public Map<String, List<Boolean>> getForwardAnomalyBooleanListMap() {
        this.forwardDataSet = loadForwardDataSet();
        Map<String, List<Boolean>> forwardAnomalyBooleanListMap = new HashMap<>();
        forwardAnomalyDecisionListMap = new HashMap<>();
        for (String tenor : forwardDataSet.getTestPointListMap().keySet()) {
            // check train dataset and test data set. if one of them is too small, skip
            if (forwardDataSet.getTrainPointListMap().get(tenor).size() < 100 || forwardDataSet.getTestPointListMap().get(tenor).size() < 100
                    || Utility.coefficientOfVariationOfPointList(forwardDataSet.getTrainPointListMap().get(tenor)) < 0.01
                    || Utility.coefficientOfVariationOfPointList(forwardDataSet.getTestPointListMap().get(tenor)) < 0.01)
                continue;
            forwardAnomalyBooleanListMap.put(tenor,
                    strategy.fit(forwardDataSet.getTrainPointListMap().get(tenor))
                            .predict(forwardDataSet.getTestPointListMap().get(tenor))
            );
            forwardAnomalyDecisionListMap.put(tenor, ((PythonStrategy) strategy).getDecisionValues());
        }
        return forwardAnomalyBooleanListMap;
    }

    public void close() {
        ((PythonStrategy) super.getStrategy()).close();
    }

    public int getMaxReportSize() {
        return maxReportSize;
    }

    public void setMaxReportSize(int maxReportSize) {
        this.maxReportSize = maxReportSize;
    }
}
