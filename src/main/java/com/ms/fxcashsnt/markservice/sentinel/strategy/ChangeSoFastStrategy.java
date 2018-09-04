package com.ms.fxcashsnt.markservice.sentinel.strategy;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;

import java.util.LinkedList;
import java.util.List;

/**
 * user: yandongl
 * date: 7/26/2018
 * <p>
 * This strategy is used to find the point which changes so fast compared to the former point. The threshold is pre-defined.
 * <p>
 * The minimum size of pointList is 2.
 */
public class ChangeSoFastStrategy implements Strategy {
    private double threashold;

    public ChangeSoFastStrategy(double threashold) {
        this.threashold = threashold;
    }

    @Override
    public Strategy fit(List<Point> pointList) {
        // This strategy does not need to fit the model because it has no trainable parameters.
        return this;
    }

    @Override
    public List<Boolean> predict(List<Point> pointList) {
        List<Boolean> anomalyBooleanList = new LinkedList<>();
        // The first point is always normal.
        anomalyBooleanList.add(false);
        for (int i = 1; i < pointList.size(); i++) {
            anomalyBooleanList.add(Math.abs(pointList.get(i).getValue() / pointList.get(i - 1).getValue() - 1) > threashold);
        }

        return anomalyBooleanList;
    }

}
