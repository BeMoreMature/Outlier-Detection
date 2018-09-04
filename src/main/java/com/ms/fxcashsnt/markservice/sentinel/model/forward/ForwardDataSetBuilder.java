package com.ms.fxcashsnt.markservice.sentinel.model.forward;

import com.ms.fxcashsnt.markservice.sentinel.dao.ForwardCurveDAO;
import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import com.ms.fxcashsnt.markservice.sentinel.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * user: yandongl
 * date: 7/30/2018
 */
@Component(value = "forwardDataSetBuilder")
@Scope(value = "prototype")
public class ForwardDataSetBuilder {
    private String currencyPair;
    private String context;

    private Instant trainStartTimestamp;
    private Instant trainEndTimestamp;

    private Instant testStartTimestamp;
    private Instant testEndTimestamp;

    @Autowired
    private ForwardCurveDAO forwardCurveDAO;

    public ForwardDataSetBuilder() {
    }

    public ForwardDataSetBuilder setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
        return this;
    }

    public ForwardDataSetBuilder setContext(String context) {
        this.context = context;
        return this;
    }

    public ForwardDataSetBuilder setTrainStartTimestamp(Instant trainStartTimestamp) {
        this.trainStartTimestamp = trainStartTimestamp;
        return this;
    }

    public ForwardDataSetBuilder setTrainEndTimestamp(Instant trainEndTimestamp) {
        this.trainEndTimestamp = trainEndTimestamp;
        return this;
    }

    public ForwardDataSetBuilder setTestStartTimestamp(Instant testStartTimestamp) {
        this.testStartTimestamp = testStartTimestamp;
        return this;
    }

    public ForwardDataSetBuilder setTestEndTimestamp(Instant testEndTimestamp) {
        this.testEndTimestamp = testEndTimestamp;
        return this;
    }

    public void setForwardCurveDAO(ForwardCurveDAO forwardCurveDAO) {
        this.forwardCurveDAO = forwardCurveDAO;
    }

    public ForwardDataSet build() {
        ForwardDataSet forwardDataSet = new ForwardDataSet(currencyPair, context, trainStartTimestamp, trainEndTimestamp, testStartTimestamp, testEndTimestamp);

        ForwardCurve trainForwardCurve = forwardCurveDAO.query(currencyPair, context, trainStartTimestamp, trainEndTimestamp);
        Map<String, List<Point>> trainPointListMap = trainForwardCurve.getForwardPointMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> Utility.removeDuplicatedAccordingToTimestamp(x.getValue().stream().map(
                        p -> new Point(p.getPts(), MarkServiceConstants.IntraContextList.contains(context) ? p.getTimestmap() : p.getPositionDate().atStartOfDay().toInstant(ZoneOffset.UTC))
                ).sorted(Comparator.comparing(Point::getTimestamp)).collect(Collectors.toList()))));
        forwardDataSet.setTrainPointListMap(trainPointListMap);

        ForwardCurve testForwardCurve = forwardCurveDAO.query(currencyPair, context, testStartTimestamp, testEndTimestamp);
        Map<String, List<Point>> testPointListMap = testForwardCurve.getForwardPointMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> Utility.removeDuplicatedAccordingToTimestamp(x.getValue().stream().map(
                        p -> new Point(p.getPts(), MarkServiceConstants.IntraContextList.contains(context) ? p.getTimestmap() : p.getPositionDate().atStartOfDay().toInstant(ZoneOffset.UTC))
                ).sorted(Comparator.comparing(Point::getTimestamp)).collect(Collectors.toList()))));
        forwardDataSet.setTestPointListMap(testPointListMap);

        return forwardDataSet;
    }
}
