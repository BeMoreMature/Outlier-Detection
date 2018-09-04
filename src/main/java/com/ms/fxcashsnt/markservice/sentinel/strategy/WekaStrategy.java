package com.ms.fxcashsnt.markservice.sentinel.strategy;

import com.ms.fxcashsnt.markservice.sentinel.ml.SmoothedZScore;
import com.ms.fxcashsnt.markservice.sentinel.model.Point;

import java.util.HashMap;
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
public class WekaStrategy implements Strategy {
    private int lag;
    private double threshold;
    private double influence;
    public WekaStrategy(int lag, double threshold, double influence) {
        this.threshold = threshold;
        this.lag = lag;
        this.influence =influence;
    }

    @Override
    public Strategy fit(List<Point> pointList) {
        // This strategy does not need to fit the model because it has no trainable parameters.
        return this;
    }

    @Override
    public List<Boolean> predict(List<Point> pointList) {
        List<Double> valueList = new LinkedList<>();
        List<Boolean> anomalyBooleanList = new LinkedList<>();
        for (Point point : pointList) {
            valueList.add(point.getValue());
        }
        if(valueList.size() < 100){
            for(int i = 0 ;i < valueList.size(); i++){
                anomalyBooleanList.add(false);
            }
        }else{
            SmoothedZScore smoothedZScore = new SmoothedZScore();
            smoothedZScore.thresholdingAlgo(valueList, lag, threshold, influence);
            anomalyBooleanList.addAll(smoothedZScore.getBooleanList());
        }

        return anomalyBooleanList;
    }

}
