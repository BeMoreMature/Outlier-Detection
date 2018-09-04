package com.ms.fxcashsnt.markservice.sentinel.model.spot;

import java.time.Instant;
import java.time.LocalDate;

/**
 * user: yandong.liu
 * date: 7/23/2018
 */
public class SpotPoint {
    private double spotRate;
    private LocalDate spotDate;
    private LocalDate positionDate;

    private Instant timestamp;

    public SpotPoint() {
    }

    public SpotPoint(double spotRate, LocalDate spotDate, LocalDate positionDate, Instant timestamp) {
        this.spotRate = spotRate;
        this.spotDate = spotDate;
        this.positionDate = positionDate;
        this.timestamp = timestamp;
    }

    public double getSpotRate() {
        return spotRate;
    }

    public void setSpotRate(double spotRate) {
        this.spotRate = spotRate;
    }

    public LocalDate getSpotDate() {
        return spotDate;
    }

    public void setSpotDate(LocalDate spotDate) {
        this.spotDate = spotDate;
    }

    public LocalDate getPositionDate() {
        return positionDate;
    }

    public void setPositionDate(LocalDate positionDate) {
        this.positionDate = positionDate;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
