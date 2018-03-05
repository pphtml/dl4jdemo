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
    private Map <Long, YahooSplit> splits;

    public Map<Long, YahooDividend> getDividends() {
        return dividends;
    }

    public void setDividends(Map<Long, YahooDividend> dividends) {
        this.dividends = dividends;
    }

    public Map<Long, YahooSplit> getSplits() {
        return splits;
    }

    public void setSplits(Map<Long, YahooSplit> splits) {
        this.splits = splits;
    }
}
