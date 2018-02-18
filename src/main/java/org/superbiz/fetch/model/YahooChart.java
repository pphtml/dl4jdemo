package org.superbiz.fetch.model;

import java.util.List;

public class YahooChart {
    List<YahooData> result;
    YahooError error;

    public List<YahooData> getResult() {
        return result;
    }

    public void setResult(List<YahooData> result) {
        this.result = result;
    }

    public YahooError getError() {
        return error;
    }

    public void setError(YahooError error) {
        this.error = error;
    }
}
