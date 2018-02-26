package org.superbiz.fetch.model;

import java.util.List;

public class YahooData {
    YahooMeta meta;
    List<Long> timestamp;
    YahooIndicators indicators;
    YahooEvents events;

    public YahooMeta getMeta() {
        return meta;
    }

    public void setMeta(YahooMeta meta) {
        this.meta = meta;
    }

    public List<Long> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(List<Long> timestamp) {
        this.timestamp = timestamp;
    }

    public YahooIndicators getIndicators() {
        return indicators;
    }

    public void setIndicators(YahooIndicators indicators) {
        this.indicators = indicators;
    }

    public YahooEvents getEvents() {
        return events;
    }

    public void setEvents(YahooEvents events) {
        this.events = events;
    }
}
