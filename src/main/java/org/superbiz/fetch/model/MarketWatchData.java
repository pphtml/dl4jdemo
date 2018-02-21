package org.superbiz.fetch.model;

import org.nd4j.shade.jackson.annotation.JsonInclude;
import org.nd4j.shade.jackson.databind.annotation.JsonDeserialize;
import org.nd4j.shade.jackson.databind.annotation.JsonSerialize;
import org.superbiz.util.DiffAttribute;
import org.superbiz.util.LocalDateDeserializer;
import org.superbiz.util.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketWatchData {
    private String symbol;
    private BigDecimal targetPrice;
    private BigDecimal quartersEstimate;
    private BigDecimal yearsEstimate;
    private BigDecimal medianPeOnCy;
    private BigDecimal nextFiscalYear;
    private BigDecimal medianPeNextFy;
    private BigDecimal lastQuarterEarnings;
    private BigDecimal yearAgoEarnings;
    private String recommendation; // ENUM?
    private Integer numberOfRatings;
    private Integer buy;
    private Integer overweight;
    private Integer hold;
    private Integer underweight;
    private Integer sell;
    @DiffAttribute(ignoreForComparison = true)
    private String history;
    @DiffAttribute(setOnlyIfChanging = true)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate lastUpdated;

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public BigDecimal getQuartersEstimate() {
        return quartersEstimate;
    }

    public BigDecimal getYearsEstimate() {
        return yearsEstimate;
    }

    public BigDecimal getMedianPeOnCy() {
        return medianPeOnCy;
    }

    public BigDecimal getNextFiscalYear() {
        return nextFiscalYear;
    }

    public BigDecimal getMedianPeNextFy() {
        return medianPeNextFy;
    }

    public BigDecimal getLastQuarterEarnings() {
        return lastQuarterEarnings;
    }

    public BigDecimal getYearAgoEarnings() {
        return yearAgoEarnings;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public Integer getNumberOfRatings() {
        return numberOfRatings;
    }

    public Integer getBuy() {
        return buy;
    }

    public Integer getOverweight() {
        return overweight;
    }

    public Integer getHold() {
        return hold;
    }

    public Integer getUnderweight() {
        return underweight;
    }

    public Integer getSell() {
        return sell;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    public static final class MarketWatchDataBuilder {
        private String symbol;
        private BigDecimal targetPrice;
        private BigDecimal quartersEstimate;
        private BigDecimal yearsEstimate;
        private BigDecimal medianPeOnCy;
        private BigDecimal nextFiscalYear;
        private BigDecimal medianPeNextFy;
        private BigDecimal lastQuarterEarnings;
        private BigDecimal yearAgoEarnings;
        private String recommendation; // ENUM?
        private Integer numberOfRatings;
        private Integer buy;
        private Integer overweight;
        private Integer hold;
        private Integer underweight;
        private Integer sell;
        private String history;
        private LocalDate lastUpdated;

        private MarketWatchDataBuilder() {
        }

        public static MarketWatchDataBuilder aMarketWatchData() {
            return new MarketWatchDataBuilder();
        }

        public MarketWatchDataBuilder withSymbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public MarketWatchDataBuilder withTargetPrice(BigDecimal targetPrice) {
            this.targetPrice = targetPrice;
            return this;
        }

        public MarketWatchDataBuilder withQuartersEstimate(BigDecimal quartersEstimate) {
            this.quartersEstimate = quartersEstimate;
            return this;
        }

        public MarketWatchDataBuilder withYearsEstimate(BigDecimal yearsEstimate) {
            this.yearsEstimate = yearsEstimate;
            return this;
        }

        public MarketWatchDataBuilder withMedianPeOnCy(BigDecimal medianPeOnCy) {
            this.medianPeOnCy = medianPeOnCy;
            return this;
        }

        public MarketWatchDataBuilder withNextFiscalYear(BigDecimal nextFiscalYear) {
            this.nextFiscalYear = nextFiscalYear;
            return this;
        }

        public MarketWatchDataBuilder withMedianPeNextFy(BigDecimal medianPeNextFy) {
            this.medianPeNextFy = medianPeNextFy;
            return this;
        }

        public MarketWatchDataBuilder withLastQuarterEarnings(BigDecimal lastQuarterEarnings) {
            this.lastQuarterEarnings = lastQuarterEarnings;
            return this;
        }

        public MarketWatchDataBuilder withYearAgoEarnings(BigDecimal yearAgoEarnings) {
            this.yearAgoEarnings = yearAgoEarnings;
            return this;
        }

        public MarketWatchDataBuilder withRecommendation(String recommendation) {
            this.recommendation = recommendation;
            return this;
        }

        public MarketWatchDataBuilder withNumberOfRatings(Integer numberOfRatings) {
            this.numberOfRatings = numberOfRatings;
            return this;
        }

        public MarketWatchDataBuilder withBuy(Integer buy) {
            this.buy = buy;
            return this;
        }

        public MarketWatchDataBuilder withOverweight(Integer overweight) {
            this.overweight = overweight;
            return this;
        }

        public MarketWatchDataBuilder withHold(Integer hold) {
            this.hold = hold;
            return this;
        }

        public MarketWatchDataBuilder withUnderweight(Integer underweight) {
            this.underweight = underweight;
            return this;
        }

        public MarketWatchDataBuilder withSell(Integer sell) {
            this.sell = sell;
            return this;
        }

        public MarketWatchDataBuilder withHistory(String history) {
            this.history = history;
            return this;
        }

        public MarketWatchDataBuilder withLastUpdated(LocalDate lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public MarketWatchData build() {
            MarketWatchData marketWatchData = new MarketWatchData();
            marketWatchData.setLastUpdated(lastUpdated);
            marketWatchData.yearsEstimate = this.yearsEstimate;
            marketWatchData.yearAgoEarnings = this.yearAgoEarnings;
            marketWatchData.medianPeOnCy = this.medianPeOnCy;
            marketWatchData.targetPrice = this.targetPrice;
            marketWatchData.hold = this.hold;
            marketWatchData.underweight = this.underweight;
            marketWatchData.symbol = this.symbol;
            marketWatchData.recommendation = this.recommendation;
            marketWatchData.medianPeNextFy = this.medianPeNextFy;
            marketWatchData.quartersEstimate = this.quartersEstimate;
            marketWatchData.history = this.history;
            marketWatchData.overweight = this.overweight;
            marketWatchData.nextFiscalYear = this.nextFiscalYear;
            marketWatchData.numberOfRatings = this.numberOfRatings;
            marketWatchData.buy = this.buy;
            marketWatchData.sell = this.sell;
            marketWatchData.lastQuarterEarnings = this.lastQuarterEarnings;
            return marketWatchData;
        }
    }
}
