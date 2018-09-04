package com.ms.fxcashsnt.markservice.sentinel.detector;

import org.springframework.stereotype.Component;

/**
 * user: yandongl
 * date: 8/20/2018
 */
@Component(value = "isolationForest")
public class IsolationForestDetector extends AbstractPythonAbnormalityDetector {
    public IsolationForestDetector() {
    }

    @Override
    public void loadStrategy() {
        super.loadStrategy("isolation_forest.py");
    }

}
