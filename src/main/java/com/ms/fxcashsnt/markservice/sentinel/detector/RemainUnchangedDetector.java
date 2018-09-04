package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.strategy.RemainUnchangedStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * user: yandongl
 * date: 7/31/2018
 */
@Component(value = "remainUnchangedDetector")
public class RemainUnchangedDetector extends AbstractAbnormalityDetector {
    private double threshold;

    public RemainUnchangedDetector() {
        super();
    }

    @Override
    public void loadStrategy() {
        super.setStrategy(new RemainUnchangedStrategy());
    }

    @Override
    boolean isNeedReport(List<Boolean> anomalyBooleanList) {
        if (anomalyBooleanList.size() < 2 ) return false;
        return anomalyBooleanList.stream()
                .filter(Boolean::booleanValue)
                .count() / anomalyBooleanList.size() > threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
