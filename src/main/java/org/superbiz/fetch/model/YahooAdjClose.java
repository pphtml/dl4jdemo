package org.superbiz.fetch.model;

import java.math.BigDecimal;
import java.util.List;

public class YahooAdjClose {
    private List<BigDecimal> adjclose;

    public List<BigDecimal> getAdjclose() {
        return adjclose;
    }

    public void setAdjclose(List<BigDecimal> adjclose) {
        this.adjclose = adjclose;
    }
}
