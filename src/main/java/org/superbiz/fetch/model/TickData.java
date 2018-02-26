package org.superbiz.fetch.model;

import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TickData {
    private Long timestamp;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;

    public TickData(Long timestamp, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, Long volume) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public static TickData from(Long timestamp, BigDecimal open, BigDecimal high, BigDecimal low,
                                BigDecimal close, Long volume) {
        return new TickData(timestamp, round(open), round(high), round(low), round(close), volume);
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
}
