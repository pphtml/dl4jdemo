package org.superbiz.fetch.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TickData {
    private Long timestamp;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;
    private List<Event> events;

    public TickData(Long timestamp, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, Long volume,
                    List<Event> events) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.events = events;
    }

    public TickData() {
    }

    public static TickData from(Long timestamp, BigDecimal open, BigDecimal high, BigDecimal low,
                                BigDecimal close, Long volume) {
        return new TickData(timestamp, round(open), round(high), round(low), round(close), volume, null);
    }

    public static TickData from(Long timestamp, BigDecimal open, BigDecimal high, BigDecimal low,
                                BigDecimal close, Long volume, List<Event> events) {
        return new TickData(timestamp, round(open), round(high), round(low), round(close), volume, events);
    }

    private static BigDecimal round(BigDecimal priceValue) {
        if (priceValue == null) {
            return null;
        } else {
            BigDecimal roundedValue = priceValue.setScale(4, RoundingMode.HALF_UP);
            String valueAsString = roundedValue.toString();
            if (valueAsString.matches("\\d+\\.\\d\\d00")) {
                return roundedValue.setScale(2, RoundingMode.UNNECESSARY);
            } else if (valueAsString.matches("\\d+\\.\\d\\d\\d0")) {
                return roundedValue.setScale(3, RoundingMode.UNNECESSARY);
            } else {
                return roundedValue;
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TickData{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", open=").append(open);
        sb.append(", high=").append(high);
        sb.append(", low=").append(low);
        sb.append(", close=").append(close);
        sb.append(", volume=").append(volume);
        sb.append(", events=").append(events);
        sb.append('}');
        return sb.toString();
    }

    @JsonProperty("t")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("o")
    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    @JsonProperty("h")
    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    @JsonProperty("l")
    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    @JsonProperty("c")
    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    @JsonProperty("v")
    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("e")
    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
