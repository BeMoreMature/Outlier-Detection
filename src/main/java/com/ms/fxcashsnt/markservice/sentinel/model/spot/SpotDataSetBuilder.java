package com.ms.fxcashsnt.markservice.sentinel.model.spot;

import com.ms.fxcashsnt.markservice.sentinel.dao.SpotCurveDAO;
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
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * user: yandongl
 * date: 7/30/2018
 */

@Component(value = "spotDataSetBuilder")
@Scope(value = "prototype")
public class SpotDataSetBuilder {
    private String currencyPair;
    private String context;
    private Instant trainStartTimestamp;
    private Instant trainEndTimestamp;

    private Instant testStartTimestamp;
    private Instant testEndTimestamp;

    @Autowired
    private SpotCurveDAO spotCurveDAO;

    public SpotDataSetBuilder() {
    }

    public SpotDataSetBuilder setTrainStartTimestamp(Instant trainStartTimestamp) {
        this.trainStartTimestamp = trainStartTimestamp;
        return this;
    }

    public SpotDataSetBuilder setTrainEndTimestamp(Instant trainEndTimestamp) {
        this.trainEndTimestamp = trainEndTimestamp;
        return this;
    }

    public SpotDataSetBuilder setTestStartTimestamp(Instant testStartTimestamp) {
        this.testStartTimestamp = testStartTimestamp;
        return this;
    }

    public SpotDataSetBuilder setTestEndTimestamp(Instant testEndTimestamp) {
        this.testEndTimestamp = testEndTimestamp;
        return this;
    }

    public void setSpotCurveDAO(SpotCurveDAO spotCurveDAO) {
        this.spotCurveDAO = spotCurveDAO;
    }


    public SpotDataSetBuilder setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
        return this;
    }

    public SpotDataSetBuilder setContext(String context) {
        this.context = context;
        return this;
    }

    public SpotDataSet build() {
        SpotDataSet spotDataSet = new SpotDataSet(currencyPair, context, trainStartTimestamp, trainEndTimestamp, testStartTimestamp, testEndTimestamp);

        SpotCurve trainSpotCurve = spotCurveDAO.query(currencyPair, context, trainStartTimestamp, trainEndTimestamp);
        List<Point> trainPointList = trainSpotCurve.getSpotPointList().stream()
                .map(p -> new Point(p.getSpotRate(),
                        MarkServiceConstants.IntraContextList.contains(context) ? p.getTimestamp() : p.getPositionDate().atStartOfDay().toInstant(ZoneOffset.UTC)))
                .sorted(Comparator.comparing(Point::getTimestamp))
                .collect(Collectors.toList());
//        trainPointList.sort(Comparator.comparing(Point::getTimestamp));
        spotDataSet.setTrainPointList(Utility.removeDuplicatedAccordingToTimestamp(trainPointList));

        SpotCurve testSpotCurve = spotCurveDAO.query(currencyPair, context, testStartTimestamp, testEndTimestamp);
        List<Point> testPointList = testSpotCurve.getSpotPointList().stream()
                .map(p -> new Point(p.getSpotRate(),
                        MarkServiceConstants.IntraContextList.contains(context) ? p.getTimestamp() : p.getPositionDate().atStartOfDay().toInstant(ZoneOffset.UTC)))
                .sorted(Comparator.comparing(Point::getTimestamp))
                .collect(Collectors.toList());
//        testPointList.sort(Comparator.comparing(Point::getTimestamp));
        spotDataSet.setTestPointList(Utility.removeDuplicatedAccordingToTimestamp(testPointList));

        return spotDataSet;
    }
}
