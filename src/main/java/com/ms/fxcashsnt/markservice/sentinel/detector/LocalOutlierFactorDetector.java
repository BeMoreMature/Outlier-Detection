package com.ms.fxcashsnt.markservice.sentinel.detector;

import org.springframework.stereotype.Component;

/**
 * user: yandongl
 * date: 8/20/2018
 */
@Component(value = "localOutlierFactorDetector")
public class LocalOutlierFactorDetector extends AbstractPythonAbnormalityDetector {
    public LocalOutlierFactorDetector() {
    }

    @Override
    public void loadStrategy() {
        super.loadStrategy("local_outlier_factor.py");
    }
}
