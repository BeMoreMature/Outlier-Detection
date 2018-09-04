package com.ms.fxcashsnt.markservice.sentinel.model.forward;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * user: yandong.liu
 * date: 7/23/2018
 */
public class ForwardCurve {
    private String currencyPair;
    private String context;

    /**
     * start timestamp of this curve. Start timestamp and end timestamp defines the range of this curve.
     */
    private Instant startTimestamp;
    /**
     * end time stamp of this curve.
     */
    private Instant endTimestamp;
    /**
     * This list contains all forward points lying in the range defined above.
     */
    private Map<String, List<ForwardPoint>> forwardPointMap;


    public ForwardCurve() {
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

    public Instant getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Instant startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Instant getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Instant endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Map<String, List<ForwardPoint>> getForwardPointMap() {
        return forwardPointMap;
    }

    public void setForwardPointMap(Map<String, List<ForwardPoint>> forwardPointMap) {
        this.forwardPointMap = forwardPointMap;
    }
}
