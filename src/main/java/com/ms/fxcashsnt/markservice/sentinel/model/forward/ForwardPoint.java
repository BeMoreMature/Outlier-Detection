package com.ms.fxcashsnt.markservice.sentinel.model.forward;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * user: Carl, WU
 * date: 7/18/2018
 */
public class ForwardPoint implements Serializable {

    private String tenor;
    private double pts;
    private double outright;
    private LocalDate positionDate;

    private Instant timestmap;

    public ForwardPoint(String tenor, double pts, double outright, LocalDate positionDate, Instant timestmap) {
        this.tenor = tenor;
        this.pts = pts;
        this.outright = outright;
        this.positionDate = positionDate;
        this.timestmap = timestmap;
    }

    public String getTenor() {
        return tenor;
    }

    public void setTenor(String tenor) {
        this.tenor = tenor;
    }

    public double getPts() {
        return pts;
    }

    public void setPts(double pts) {
        this.pts = pts;
    }

    public double getOutright() {
        return outright;
    }

    public void setOutright(double outright) {
        this.outright = outright;
    }

    public LocalDate getPositionDate() {
        return positionDate;
    }

    public void setPositionDate(LocalDate positionDate) {
        this.positionDate = positionDate;
    }

    public Instant getTimestmap() {
        return timestmap;
    }

    public void setTimestmap(Instant timestmap) {
        this.timestmap = timestmap;
    }
}
