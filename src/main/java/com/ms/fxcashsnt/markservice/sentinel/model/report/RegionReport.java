package com.ms.fxcashsnt.markservice.sentinel.model.report;

/**
 * user: Carl,Wu
 * date: 8/8/2018
 * build format that need to insert in the table
 */

public class RegionReport {
    private String currency;
    private String tenor;
    private Double NY ;
    private Double LN ;
    private Double TK ;
    private Double HK ;

    public void setHK(Double HK) {
        this.HK = HK;
    }

    public void setLN(Double LN) {

        this.LN = LN;
    }

    public void setTK(Double TK) {

        this.TK = TK;
    }

    public void setNY(Double NY) {

        this.NY = NY;
    }

    public Double getHK() {

        return HK;
    }

    public Double getTK() {

        return TK;
    }

    public Double getLN() {

        return LN;
    }

    public Double getNY() {

        return NY;
    }

    public void setTenor(String tenor) {

        this.tenor = tenor;
    }

    public String getTenor() {

        return tenor;
    }

    public void setCurrency(String currency) {

        this.currency = currency;
    }

    public String getCurrency() {

        return currency;
    }
}
