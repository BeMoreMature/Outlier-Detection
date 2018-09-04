package com.ms.fxcashsnt.markservice.sentinel.detector;

import org.springframework.stereotype.Component;

/**
 * user: yandongl
 * date: 8/20/2018
 */
@Component(value = "oneClassSvmDetector")
public class OneClassSvmDetector extends AbstractPythonAbnormalityDetector {
    public OneClassSvmDetector() {
    }

    @Override
    public void loadStrategy() {
        super.loadStrategy("one_class_svm.py");
    }

}
