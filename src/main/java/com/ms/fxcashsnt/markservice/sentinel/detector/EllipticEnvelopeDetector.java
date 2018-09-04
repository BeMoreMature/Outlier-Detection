package com.ms.fxcashsnt.markservice.sentinel.detector;

import org.springframework.stereotype.Component;

/**
 * user: yandongl
 * date: 8/15/2018
 */
@Component(value = "ellipticEnvelopeDetector")
public class EllipticEnvelopeDetector extends AbstractPythonAbnormalityDetector {

    public EllipticEnvelopeDetector() {
    }

    @Override
    public void loadStrategy() {
        super.loadStrategy("elliptic_envelope.py");
    }


}
