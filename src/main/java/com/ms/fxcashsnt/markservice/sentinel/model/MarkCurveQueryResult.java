package com.ms.fxcashsnt.markservice.sentinel.model;


import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardPoint;
import msjava.hdom.Element;
import msjava.hdom.Namespace;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * user: Carl, WU
 * date: 7/7/2018
 */
public class MarkCurveQueryResult implements Serializable {
    private LocalDate spotDate;
    private double spotRate;
    private double forwardPrecision;
    private String currencyPair;
    private Boolean CombinedYieldCurve;
    private String context;

    private LocalDate positionDate;

    private Instant timestamp;

    private List<ForwardPoint> forwardPointList;

    public MarkCurveQueryResult() {
    }

    public MarkCurveQueryResult(Element result) {
        Element curve = result.getChild("MarkCurve", Namespace.getNamespace("http://xml.ms.com/ns/fxmessage"));
        this.spotRate = Double.parseDouble(curve.getAttributeValue("SpotRate"));
        this.spotDate = LocalDate.parse(curve.getAttributeValue("SpotDate"));
        this.forwardPrecision = Double.parseDouble(curve.getAttributeValue("ForwardPrecision"));
        this.currencyPair = curve.getAttributeValue("Currency1") + "_" + curve.getAttributeValue("Currency2");
        this.CombinedYieldCurve = Boolean.valueOf(curve.getAttributeValue("CombinedYieldCurve"));
        this.context = curve.getAttributeValue("Context");
        this.positionDate= LocalDate.parse(curve.getAttributeValue("PositionDate"));
        this.timestamp = Instant.now();

        Iterator iter = curve.getChildren("FwdPoint", Namespace.getNamespace("http://xml.ms.com/ns/fxmessage")).iterator();
        forwardPointList = new LinkedList();
        while (iter.hasNext()) {
            Element res = (Element) iter.next();
            Boolean nodePoint = Boolean.valueOf(res.getAttributeValue("NodePoint"));
            if(nodePoint == false) continue;

            double pts = Double.parseDouble(res.getAttributeValue("Pts"));
            String tenor = res.getAttributeValue("Tenor");
            double outright = Double.parseDouble(res.getAttributeValue("Outright"));
            LocalDate valueDate = LocalDate.parse(res.getAttributeValue("ValueDate"));

            if (tenor == null || tenor.isEmpty()) {
                long diff = ChronoUnit.DAYS.between(spotDate, valueDate);
                tenor = diff + "D";
            }
            ForwardPoint fp = new ForwardPoint(tenor, pts, outright, LocalDate.now(), Instant.now());
            forwardPointList.add(fp);
        }
    }

    public LocalDate getSpotDate() {
        return spotDate;
    }

    public void setSpotDate(LocalDate spotDate) {
        this.spotDate = spotDate;
    }

    public double getSpotRate() {
        return spotRate;
    }

    public void setSpotRate(double spotRate) {
        this.spotRate = spotRate;
    }

    public LocalDate getPositionDate() {
        return positionDate;
    }

    public void setPositionDate(LocalDate positionDate) {
        this.positionDate = positionDate;
    }

    public double getForwardPrecision() {
        return forwardPrecision;
    }

    public void setForwardPrecision(double forwardPrecision) {
        this.forwardPrecision = forwardPrecision;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<ForwardPoint> getForwardPointList() {
        return forwardPointList;
    }

    public void setForwardPointList(List<ForwardPoint> forwardPointList) {
        this.forwardPointList = forwardPointList;
    }

    public void setCombinedYieldCurve(Boolean combinedYieldCurve) {
        CombinedYieldCurve = combinedYieldCurve;
    }

    public Boolean getCombinedYieldCurve() {

        return CombinedYieldCurve;
    }
}
