package org.superbiz.fetch.model;

import java.util.Map;

public class YahooEvents {
    //           "dividends": {
    //            "1514557800": {
    //              "amount": 0.149,
    //              "date": 1514557800
    //            }
    //          }
    private Map <Long, YahooDividend> dividends;

    public Map<Long, YahooDividend> getDividends() {
        return dividends;
    }

    public void setDividends(Map<Long, YahooDividend> dividends) {
        this.dividends = dividends;
    }
}
