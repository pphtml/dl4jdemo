package org.superbiz.dao;

import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;
import org.superbiz.fetch.model.MarketWatchData;
import org.superbiz.util.DateConverter;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

import static org.superbiz.fetch.model.MarketWatchData.MarketWatchDataBuilder.aMarketWatchData;
import static org.superbiz.model.jooq.Tables.MARKETWATCH;

public class MarketWatchDAO {
    @Inject
    ConnAndDSLProvider connAndDSLProvider;

    public Map<String,MarketWatchData> readMap() {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Result<Record> marketRecords = dsl.getDsl()
                    .select()
                    .from(MARKETWATCH)
                    .fetch();
            final Map<String, MarketWatchData> marketWatchDataMap = marketRecords.stream()
                    .map(m -> recordToMarketWatchData(m))
                    .collect(Collectors.toMap(m -> m.getSymbol(), m -> m));
            return marketWatchDataMap;
        }
    }

    private MarketWatchData recordToMarketWatchData(Record m) {
        return aMarketWatchData()
                .withSymbol(m.getValue(MARKETWATCH.SYMBOL))
                .withRecommendation(m.getValue(MARKETWATCH.RECOMMENDATION))
                .withTargetPrice(m.getValue(MARKETWATCH.TARGET_PRICE))
                .withNumberOfRatings(m.getValue(MARKETWATCH.NUMBER_OF_RATINGS))
                .withQuartersEstimate(m.getValue(MARKETWATCH.QUARTERS_ESTIMATE))
                .withYearsEstimate(m.getValue(MARKETWATCH.YEARS_ESTIMATE))
                .withLastQuarterEarnings(m.getValue(MARKETWATCH.LAST_QUARTER_EARNINGS))
                .withMedianPeOnCy(m.getValue(MARKETWATCH.MEDIAN_PE_ON_CY))
                .withYearAgoEarnings(m.getValue(MARKETWATCH.YEAR_AGO_EARNINGS))
                .withNextFiscalYear(m.getValue(MARKETWATCH.NEXT_FISCAL_YEAR))
                .withMedianPeNextFy(m.getValue(MARKETWATCH.MEDIAN_PE_NEXT_FY))
                .withBuy(m.getValue(MARKETWATCH.BUY))
                .withOverweight(m.getValue(MARKETWATCH.OVERWEIGHT))
                .withHold(m.getValue(MARKETWATCH.HOLD))
                .withUnderweight(m.getValue(MARKETWATCH.UNDERWEIGHT))
                .withSell(m.getValue(MARKETWATCH.SELL))
                .withHistory(m.getValue(MARKETWATCH.HISTORY))
                .withLastUpdated(m.getValue(MARKETWATCH.LAST_UPDATED).toLocalDate())
                .build();
    }

    public void storeToDB(MarketWatchData marketWatchData) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            dsl.getDsl().insertInto(MARKETWATCH,
                    MARKETWATCH.SYMBOL,
                    MARKETWATCH.QUARTERS_ESTIMATE,
                    MARKETWATCH.YEARS_ESTIMATE,
                    MARKETWATCH.MEDIAN_PE_ON_CY,
                    MARKETWATCH.NEXT_FISCAL_YEAR,
                    MARKETWATCH.MEDIAN_PE_NEXT_FY,
                    MARKETWATCH.LAST_QUARTER_EARNINGS,
                    MARKETWATCH.YEAR_AGO_EARNINGS,
                    MARKETWATCH.RECOMMENDATION,
                    MARKETWATCH.NUMBER_OF_RATINGS,
                    MARKETWATCH.BUY,
                    MARKETWATCH.OVERWEIGHT,
                    MARKETWATCH.HOLD,
                    MARKETWATCH.UNDERWEIGHT,
                    MARKETWATCH.SELL,
                    MARKETWATCH.TARGET_PRICE,
                    MARKETWATCH.LAST_UPDATED)
                    .values(marketWatchData.getSymbol(), marketWatchData.getQuartersEstimate(), marketWatchData.getYearsEstimate(),
                            marketWatchData.getMedianPeOnCy(), marketWatchData.getNextFiscalYear(), marketWatchData.getMedianPeNextFy(),
                            marketWatchData.getLastQuarterEarnings(), marketWatchData.getYearAgoEarnings(), marketWatchData.getRecommendation(),
                            marketWatchData.getNumberOfRatings(), marketWatchData.getBuy(), marketWatchData.getOverweight(),
                            marketWatchData.getHold(), marketWatchData.getUnderweight(), marketWatchData.getSell(),
                            marketWatchData.getTargetPrice(), DateConverter.from(marketWatchData.getLastUpdated()))
                    .execute();
        }
    }

    public void updateAll(MarketWatchData marketWatchData) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            dsl.getDsl().update(MARKETWATCH)
                    .set(MARKETWATCH.LAST_UPDATED, DateConverter.from(marketWatchData.getLastUpdated()))
                    .set(MARKETWATCH.QUARTERS_ESTIMATE, marketWatchData.getQuartersEstimate())
                    .set(MARKETWATCH.YEARS_ESTIMATE, marketWatchData.getYearsEstimate())
                    .set(MARKETWATCH.MEDIAN_PE_ON_CY, marketWatchData.getMedianPeOnCy())
                    .set(MARKETWATCH.NEXT_FISCAL_YEAR, marketWatchData.getNextFiscalYear())
                    .set(MARKETWATCH.MEDIAN_PE_NEXT_FY, marketWatchData.getMedianPeNextFy())
                    .set(MARKETWATCH.LAST_QUARTER_EARNINGS, marketWatchData.getLastQuarterEarnings())
                    .set(MARKETWATCH.YEAR_AGO_EARNINGS, marketWatchData.getYearAgoEarnings())
                    .set(MARKETWATCH.RECOMMENDATION, marketWatchData.getRecommendation())
                    .set(MARKETWATCH.NUMBER_OF_RATINGS, marketWatchData.getNumberOfRatings())
                    .set(MARKETWATCH.BUY, marketWatchData.getBuy())
                    .set(MARKETWATCH.OVERWEIGHT, marketWatchData.getOverweight())
                    .set(MARKETWATCH.HOLD, marketWatchData.getHold())
                    .set(MARKETWATCH.UNDERWEIGHT, marketWatchData.getUnderweight())
                    .set(MARKETWATCH.SELL, marketWatchData.getSell())
                    .set(MARKETWATCH.TARGET_PRICE, marketWatchData.getTargetPrice())
                    .set(MARKETWATCH.HISTORY, marketWatchData.getHistory())
                    .where(MARKETWATCH.SYMBOL.eq(marketWatchData.getSymbol()))
                    .execute();
        }
    }

    public void updateLastUpdate(MarketWatchData marketWatchData) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            dsl.getDsl().update(MARKETWATCH)
                    .set(MARKETWATCH.LAST_UPDATED, DateConverter.from(marketWatchData.getLastUpdated()))
                    .where(MARKETWATCH.SYMBOL.eq(marketWatchData.getSymbol()))
                    .execute();
        }
    }
}
