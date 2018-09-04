package com.ms.fxcashsnt.markservice.sentinel.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * A general Point class used for different strategies.
 */
public class Point implements Serializable {
    private double value;
    private Instant timestamp;

    public Point(){};
    public Point(double value, Instant timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
