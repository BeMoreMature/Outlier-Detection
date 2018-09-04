package com.ms.fxcashsnt.markservice.sentinel.strategy;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This strategy is used to detect anomaly in which most of the points remain unchanged
 * during a specific period.
 */
public class RemainUnchangedStrategy implements Strategy {
    private double mostFrequentNumber;
    private Logger logger = LoggerFactory.getLogger(RemainUnchangedStrategy.class);

    public RemainUnchangedStrategy() {
    }

    @Override
    public Strategy fit(List<Point> pointList) {
        if (pointList == null || pointList.size() == 0) {
            mostFrequentNumber = 0;
            logger.info("Train point list is empty now. This may have an effect on the predict process.");
            return this;
        }
        pointList.sort(Comparator.comparingDouble(Point::getValue));

        mostFrequentNumber = pointList.get(0).getValue();
        int count = 1, max_count = 1;
        for (int i = 1; i < pointList.size(); i++) {
            if (Utility.equals(pointList.get(i).getValue(), pointList.get(i - 1).getValue(), 1e-5)) {
                count += 1;
            } else {
                if (count > max_count) {
                    max_count = count;
                    mostFrequentNumber = pointList.get(i - 1).getValue();
                }
                count = 1;
            }
        }

        if (count > max_count) {
            max_count = count;
            mostFrequentNumber = pointList.get(pointList.size() - 1).getValue();
        }

        return this;
    }

    @Override
    public List<Boolean> predict(List<Point> pointList) {
        List<Boolean> anomalyBooleanList = pointList.stream()
                .map(p -> Utility.equals(p.getValue(), mostFrequentNumber, 1e-5))
                .collect(Collectors.toList());

        return anomalyBooleanList;
    }


}
