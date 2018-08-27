package org.superbiz.fetch.model;

import java.math.BigDecimal;
import java.util.List;

public class YahooMeta {
    private String currency;
    private String symbol;
    private String exchangeName; //": "NMS",
    private String instrumentType; //": "EQUITY",
    private long firstTradeDate; //": 863683200,
    private int gmtoffset; //": -18000,
    private String timezone; //": "EST",
    private String exchangeTimezoneName; //": "America/New_York",
    private BigDecimal chartPreviousClose; //": 1177.62,
    private BigDecimal previousClose; //": 1461.76,
    private int scale; //": 3,
    private YahooCurrentTradingPeriod currentTradingPeriod;
    private List<List<YahooTradingPeriod>> tradingPeriods;
    private String dataGranularity;
    private List<String> validRanges; // ["1d","5d","1mo","3mo","6mo","1y","2y","5y","10y","ytd","max"]
    // private Integer priceHint

//    org.superbiz.fetch.ParsingException: processData exception: com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "priceHint" (class org.superbiz.fetch.model.YahooMeta), not marked as ignorable (15 known properties: "instrumentType", "firstTradeDate", "chartPreviousClose", "dataGranularity", "exchangeTimezoneName", "currentTradingPeriod", "scale", "exchangeName", "currency", "validRanges", "symbol", "timezone", "gmtoffset", "tradingPeriods", "previousClose"])
// at [Source: java.io.StringReader@24361cfc; line: 1, column: 284] (through reference chain: org.superbiz.fetch.model.YahooResult["chart"]->org.superbiz.fetch.model.YahooChart["result"]->java.util.ArrayList[0]->org.superbiz.fetch.model.YahooData["meta"]->org.superbiz.fetch.model.YahooMeta["priceHint"])
//	at com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.from(UnrecognizedPropertyException.java:62)
//	at com.fasterxml.jackson.databind.DeserializationContext.handleUnknownProperty(DeserializationContext.java:834)
//	at com.fasterxml.jackson.databind.deser.std.StdDeserializer.handleUnknownProperty(StdDeserializer.java:1093)
//	at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.handleUnknownProperty(BeanDeserializerBase.java:1485)


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public long getFirstTradeDate() {
        return firstTradeDate;
    }

    public void setFirstTradeDate(long firstTradeDate) {
        this.firstTradeDate = firstTradeDate;
    }

    public int getGmtoffset() {
        return gmtoffset;
    }

    public void setGmtoffset(int gmtoffset) {
        this.gmtoffset = gmtoffset;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getExchangeTimezoneName() {
        return exchangeTimezoneName;
    }

    public void setExchangeTimezoneName(String exchangeTimezoneName) {
        this.exchangeTimezoneName = exchangeTimezoneName;
    }

    public BigDecimal getChartPreviousClose() {
        return chartPreviousClose;
    }

    public void setChartPreviousClose(BigDecimal chartPreviousClose) {
        this.chartPreviousClose = chartPreviousClose;
    }

    public BigDecimal getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(BigDecimal previousClose) {
        this.previousClose = previousClose;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public YahooCurrentTradingPeriod getCurrentTradingPeriod() {
        return currentTradingPeriod;
    }

    public void setCurrentTradingPeriod(YahooCurrentTradingPeriod currentTradingPeriod) {
        this.currentTradingPeriod = currentTradingPeriod;
    }

    public List<List<YahooTradingPeriod>> getTradingPeriods() {
        return tradingPeriods;
    }

    public void setTradingPeriods(List<List<YahooTradingPeriod>> tradingPeriods) {
        this.tradingPeriods = tradingPeriods;
    }

    public String getDataGranularity() {
        return dataGranularity;
    }

    public void setDataGranularity(String dataGranularity) {
        this.dataGranularity = dataGranularity;
    }

    public List<String> getValidRanges() {
        return validRanges;
    }

    public void setValidRanges(List<String> validRanges) {
        this.validRanges = validRanges;
    }
}
