package com.ms.fxcashsnt.markservice.sentinel.model.view;

import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;

import java.util.List;

/**
 * user: yandongl
 * date: 8/7/2018
 */
public class Detector {
    private int id;
    private String name;
    private List<Report> reports;

    public Detector() {
    }

    public Detector(int id, String name, List<Report> reports) {
        this.id = id;
        this.name = name;
        this.reports = reports;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
