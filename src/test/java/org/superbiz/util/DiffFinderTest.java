package org.superbiz.util;

import org.junit.Test;
import org.superbiz.fetch.model.MarketWatchData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.superbiz.fetch.model.MarketWatchData.MarketWatchDataBuilder.aMarketWatchData;

public class DiffFinderTest {

    @Test
    public void computeDiff() {
        String symbol = "AMZN";
        MarketWatchData oldMarketWatchData = aMarketWatchData()
                .withSymbol(symbol)
                .withRecommendation("Average Recommendation")
                .withTargetPrice(new BigDecimal("1234.50"))
                .withNumberOfRatings(48)
                .withQuartersEstimate(new BigDecimal("123.00"))
                .withYearsEstimate(new BigDecimal("123.00"))
                .withLastQuarterEarnings(new BigDecimal("123.00"))
                .withMedianPeOnCy(new BigDecimal("123.00"))
                .withYearAgoEarnings(new BigDecimal("123.00"))
                .withNextFiscalYear(new BigDecimal("123.00"))
                .withMedianPeNextFy(new BigDecimal("123.00"))
                .withBuy(20)
                .withOverweight(10)
                .withHold(2)
                .withUnderweight(0)
                .withSell(1)
                .withLastUpdated(LocalDate.parse("2017-01-01"))
                .withHistory("blah blah blah")
                .build();

        MarketWatchData newMarketWatchData = aMarketWatchData()
                .withSymbol(symbol)
                .withRecommendation("Average Recommendation")
                .withTargetPrice(new BigDecimal("1234.60"))
                .withNumberOfRatings(48)
                .withQuartersEstimate(new BigDecimal("123.00"))
                .withYearsEstimate(new BigDecimal("123.00"))
                .withLastQuarterEarnings(new BigDecimal("123.00"))
                .withMedianPeOnCy(new BigDecimal("123.00"))
                .withYearAgoEarnings(new BigDecimal("123.00"))
                .withNextFiscalYear(new BigDecimal("123.00"))
                .withMedianPeNextFy(new BigDecimal("123.00"))
                .withBuy(20)
                .withOverweight(10)
                .withHold(2)
                .withUnderweight(0)
                .withSell(1)
                .withLastUpdated(LocalDate.parse("2017-01-02"))
                .build();

        Optional<MarketWatchData> diffMarketWatchData = DiffFinder.computeDiff(oldMarketWatchData, newMarketWatchData);

        assertTrue(diffMarketWatchData.isPresent());
        assertEquals(LocalDate.parse("2017-01-01"), diffMarketWatchData.get().getLastUpdated());
        assertNull(diffMarketWatchData.get().getHistory());
        assertNull(diffMarketWatchData.get().getBuy());
        assertNotNull(diffMarketWatchData.get().getTargetPrice());
        assertEquals(new BigDecimal("1234.50"), diffMarketWatchData.get().getTargetPrice());
    }

    @Test
    public void computeDiffInsignificatOnly() {
        String symbol = "AMZN";
        MarketWatchData oldMarketWatchData = aMarketWatchData()
                .withSymbol(symbol)
                .withRecommendation("Average Recommendation")
                .withTargetPrice(new BigDecimal("1234.50"))
                .withNumberOfRatings(48)
                .withQuartersEstimate(new BigDecimal("123.00"))
                .withYearsEstimate(new BigDecimal("123.00"))
                .withLastQuarterEarnings(new BigDecimal("123.00"))
                .withMedianPeOnCy(new BigDecimal("123.00"))
                .withYearAgoEarnings(new BigDecimal("123.00"))
                .withNextFiscalYear(new BigDecimal("123.00"))
                .withMedianPeNextFy(new BigDecimal("123.00"))
                .withBuy(20)
                .withOverweight(10)
                .withHold(2)
                .withUnderweight(0)
                .withSell(1)
                .withLastUpdated(LocalDate.parse("2017-01-01"))
                .withHistory("blah blah blah")
                .build();

        MarketWatchData newMarketWatchData = aMarketWatchData()
                .withSymbol(symbol)
                .withRecommendation("Average Recommendation")
                .withTargetPrice(new BigDecimal("1234.50"))
                .withNumberOfRatings(48)
                .withQuartersEstimate(new BigDecimal("123.00"))
                .withYearsEstimate(new BigDecimal("123.00"))
                .withLastQuarterEarnings(new BigDecimal("123.00"))
                .withMedianPeOnCy(new BigDecimal("123.00"))
                .withYearAgoEarnings(new BigDecimal("123.00"))
                .withNextFiscalYear(new BigDecimal("123.00"))
                .withMedianPeNextFy(new BigDecimal("123.00"))
                .withBuy(20)
                .withOverweight(10)
                .withHold(2)
                .withUnderweight(0)
                .withSell(1)
                .withLastUpdated(LocalDate.parse("2017-01-02"))
                .build();

        Optional<MarketWatchData> diffMarketWatchData = DiffFinder.computeDiff(oldMarketWatchData, newMarketWatchData);

        assertFalse(diffMarketWatchData.isPresent());
    }
}