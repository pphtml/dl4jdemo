package org.superbiz.fetch.model;

import java.math.BigDecimal;

public class YahooSplit {
    //           "splits": {
    //            "1519675800": {
    //              "date": 1519675800,
    //              "numerator": 4,
    //              "denominator": 5,
    //              "splitRatio": "5/4"
    //            }
    //          }

    private Long date;
    private Integer numerator;
    private Integer denominator;
    private String splitRatio;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getNumerator() {
        return numerator;
    }

    public void setNumerator(Integer numerator) {
        this.numerator = numerator;
    }

    public Integer getDenominator() {
        return denominator;
    }

    public void setDenominator(Integer denominator) {
        this.denominator = denominator;
    }

    public String getSplitRatio() {
        return splitRatio;
    }

    public void setSplitRatio(String splitRatio) {
        this.splitRatio = splitRatio;
    }
}
