package org.superbiz.fetch.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class YahooIndicators {
    private List<YahooQuote> quote;
    private List<YahooUnadjClose> unadjclose;
    private List<YahooAdjClose> adjclose;

    public List<YahooQuote> getQuote() {
        return quote;
    }

    public void setQuote(List<YahooQuote> quote) {
        this.quote = quote;
    }

    public List<YahooUnadjClose> getUnadjclose() {
        return unadjclose;
    }

    public void setUnadjclose(List<YahooUnadjClose> unadjclose) {
        this.unadjclose = unadjclose;
    }

    public List<YahooAdjClose> getAdjclose() {
        return adjclose;
    }

    public void setAdjclose(List<YahooAdjClose> adjclose) {
        this.adjclose = adjclose;
    }
}
