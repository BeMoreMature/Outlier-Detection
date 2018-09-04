package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.strategy.WekaStrategy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * user: yandongl
 * date: 8/15/2018
 */
@Component(value = "smoothedZScoreDetector")
public class SmoothedZScoreDetector extends AbstractAbnormalityDetector {
    private int maxReportSize;
    private int lag;
    private double threshold;
    private double influence;

    public SmoothedZScoreDetector() {
        super();
    }

    @Override
    public void loadStrategy() {
        super.setStrategy(new WekaStrategy(lag, threshold, influence));
    }

    @Override
    boolean isNeedReport(List<Boolean> anomalyBooleanList) {
//        return anomalyBooleanList.stream()
//                .filter(Boolean::booleanValue)
//                .count() / anomalyBooleanList.size() > maxAnomalySize;
        return anomalyBooleanList.stream()
                .anyMatch(Boolean::booleanValue);
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
        for (String currencyPair: currencyPairList) {
            for (String context: contextList) {
                this.setCurrencyPair(currencyPair);
                this.setContext(context);
                List<Boolean> anomalyBooleanList = getSpotAnomalyBooleanList();
                Double score = Double.valueOf(anomalyBooleanList.stream().filter(p -> p == true).count());
                if(isNeedReport(anomalyBooleanList)) {
                    Report report = new Report(
                            currencyPair, context, "SPOT", testStartTimestamp, testEndTimestamp,
                            this.spotDataSet.getTestPointList(), anomalyBooleanList
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
    public List<Report> detectForwardAnomaly(List<String> contextList) {
        Queue<Report> reportQueue = new PriorityQueue<>(maxReportSize * 4, new Comparator<Report>() {
            @Override
            public int compare(Report o1, Report o2) {
                if (o1.getScore() > o2.getScore()) {
                    return -1;
                } else return 1;
            }
        });
        List<String> currencyPairList = forwardCurveDAO.queryUniqueCurrencyPair();
        for (String currencyPair: currencyPairList) {
            for (String context: contextList) {
                this.setCurrencyPair(currencyPair);
                this.setContext(context);
                Map<String, List<Boolean>> forwardAnomalyBooleanListMap = getForwardAnomalyBooleanListMap();
                for (String tenor: forwardAnomalyBooleanListMap.keySet()) {
                    List<Boolean> anomalyBooleanList = forwardAnomalyBooleanListMap.get(tenor);
                    Double score = Double.valueOf(anomalyBooleanList.stream().filter(p -> p == true).count());
                    if (isNeedReport(forwardAnomalyBooleanListMap.get(tenor))) {
                        Report report = new Report(
                                currencyPair, context, tenor, testStartTimestamp, testEndTimestamp,
                                this.forwardDataSet.getTestPointListMap().get(tenor), anomalyBooleanList
                        );
                        report.setScore(score);
                        reportQueue.add(report);
                        if (reportQueue.size() > maxReportSize * 4) reportQueue.poll();
                    }
                }
            }
        }
        List<Report> reportList = new ArrayList<>(reportQueue);
        Collections.sort(reportList, Comparator.comparing(Report::getScore));
        Set<String> seen = new HashSet<>();
        return reportList.stream().filter(r -> seen.add(r.getCurrencyPair() + r.getTenor())).limit(maxReportSize).collect(Collectors.toList());
    }

    public void setMaxReportSize(int maxReportSize) {
        this.maxReportSize = maxReportSize;
    }

    public int getMaxReportSize() {

        return maxReportSize;
    }

    public void setInfluence(double influence) {
        this.influence = influence;
    }

    public void setThreshold(double threshold) {

        this.threshold = threshold;
    }

    public void setLag(int lag) {

        this.lag = lag;
    }

    public double getInfluence() {

        return influence;
    }

    public double getThreshold() {

        return threshold;
    }

    public int getLag() {

        return lag;
    }
}
