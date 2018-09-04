package com.ms.fxcashsnt.markservice.sentinel.strategy;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.util.Utility;

import java.util.*;

/**
 * user: yandongl
 * date: 7/26/2018
 * <p>
 * This strategy is designed to find price difference across different regine.
 * If the difference is beyond threshold, there may be something wrong.
 */
public class DifferGreatlyStrategy implements Strategy {
    private double threashold;
    private boolean isAbnormal;

    public DifferGreatlyStrategy(double threashold) {
        this.threashold = threashold;
    }

    @Override
    public Strategy fit(List<Point> pointList) {
        // This strategy does not need to fit the model because it has no trainable parameters.
        return this;
    }

    @Override
    public List<Boolean> predict(List<Point> pointList) {
        if (pointList == null) return null;
        List<Boolean> anomalyBooleanList = new ArrayList<>(Arrays.asList(new Boolean[pointList.size()]));
        Collections.fill(anomalyBooleanList, Boolean.FALSE);


        double max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        int max_index = 0, min_index = 0;

        double sum = 0;
        int count = 0;

        for (int i = 0; i < pointList.size(); i++) {
            Point point = pointList.get(i);
            if (point.getTimestamp() != null) {
               if (point.getValue() > max) {
                   max = point.getValue();
                   max_index = i;
               }
               if (point.getValue() < min) {
                  min = point.getValue();
                  min_index = i;
               }
               sum += point.getValue();
               count += 1;
            }
        }

        // if the point list length is less than 3, then we cannot decide which point is abnormal.
        if (count < 3) {
            return anomalyBooleanList;
        }

        double avg = (sum - max - min) / (count - 2);

        if (Math.abs(max-avg) < Math.abs(min-avg)) {
            anomalyBooleanList.set(min_index, Boolean.TRUE);
        }
        else if(Utility.equals(Math.abs(max-avg), Math.abs(min-avg), 10e-5)) {
            // in this case, we cannot judge.
            return anomalyBooleanList;
        } else {
            anomalyBooleanList.set(max_index, Boolean.TRUE);
        }

        return anomalyBooleanList;
    }

}
