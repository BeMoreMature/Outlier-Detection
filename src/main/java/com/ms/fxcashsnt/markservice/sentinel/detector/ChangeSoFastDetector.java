package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.strategy.ChangeSoFastStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * user: yandongl
 * date: 7/31/2018
 */
@Component(value = "spotChangeSoFastDetector")
public class ChangeSoFastDetector extends AbstractAbnormalityDetector {
    private double threashold;

    public ChangeSoFastDetector() {
        super();
    }

    @Override
    public void loadStrategy() {
        super.setStrategy(new ChangeSoFastStrategy(threashold));
    }

    @Override
    public boolean isNeedReport(List<Boolean> anomalyBooleanList) {
        return anomalyBooleanList.stream()
                .anyMatch(Boolean::booleanValue);
    }

    public double getThreashold() {
        return threashold;
    }

    public void setThreashold(double threashold) {
        this.threashold = threashold;
        this.loadStrategy();
    }
}
