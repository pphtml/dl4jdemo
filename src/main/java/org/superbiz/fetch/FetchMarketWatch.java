package org.superbiz.fetch;

import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.superbiz.HelloDL4J;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.fetch.model.MarketWatchData;
import org.superbiz.util.DateConverter;
import org.superbiz.util.GlobalInit;
import org.superbiz.util.LoggingConfig;
import org.yaml.snakeyaml.error.Mark;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.superbiz.fetch.model.MarketWatchData.MarketWatchDataBuilder.aMarketWatchData;
import static org.superbiz.model.jooq.Tables.MARKETWATCH;
import static org.superbiz.model.jooq.Tables.SECURITY;

public class FetchMarketWatch {

    public static final int PRICE_SCALE = 2;

    static { new GlobalInit(); }

    private static final Logger LOGGER = Logger.getLogger(FetchMarketWatch.class.getName());

    //private static final String URL_TEMPLATE = "https://www.marketwatch.com/investing/stock/amzn/analystestimates";
    private static final String URL_TEMPLATE = "https://www.marketwatch.com/investing/stock/%s/analystestimates";

    private static final FetchMarketWatch fetchMarketWatch = new FetchMarketWatch();

    public static void main(String[] args) throws IOException {
        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
                .setConnectTimeout(10000);
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);

        try (AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);
             ConnAndDSL dsl = ConnAndDSL.create()) {
            Result<Record1<String>> existingMarketData = dsl.getDsl().select(MARKETWATCH.SYMBOL)
                    .from(MARKETWATCH)
                    .where(MARKETWATCH.LAST_UPDATED.eq(DateConverter.from(currentDate)))
                    .fetch();
            List<String> existingMarketDataList = existingMarketData.stream()
                    .map(rec -> rec.value1())
                    .collect(Collectors.toList());
            LOGGER.info(String.format("MarketWatch data already read for %s: %s", currentDate, existingMarketDataList));

            Result<Record> securities = dsl.getDsl()
                    .select()
                    .from(SECURITY)
                    .where(SECURITY.SYMBOL.notIn(existingMarketDataList))
                    .orderBy(SECURITY.SYMBOL)
                    .fetch();
            securities.stream().forEach(security -> {
                //System.out.println(security);

                String symbol = security.getValue(SECURITY.SYMBOL);
                String url = String.format(URL_TEMPLATE, symbol.toLowerCase());
                Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                        .setUrl(url)
                        .build();

                ListenableFuture<Response> responseFuture = client.executeRequest(getRequest);
                try {
                    Response response = responseFuture.get();
                    LOGGER.info(String.format("%s --> %s", symbol, response.getStatusCode()));

                    try {
                        MarketWatchData marketWatchData = fetchMarketWatch.parseHtmlPage(symbol, response.getResponseBody(), url);
                        fetchMarketWatch.storeToDB(dsl, marketWatchData, currentDate);
                    } catch (ParsingException e) {
                        LOGGER.log(Level.SEVERE, String.format("Parsing problem: %s", e.getMessage()));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.log(Level.SEVERE, String.format("Cannot read MarketWatch data for %s", security), e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

    Map<String, MarketWatchData> readFromDB(ConnAndDSL dsl, List<String> symbols) {
        Result<Record> marketRecords = dsl.getDsl().select().from(MARKETWATCH).where(MARKETWATCH.SYMBOL.in(symbols)).fetch();
        final Map<String, MarketWatchData> marketWatchDataMap = marketRecords.stream().map(m -> {
            MarketWatchData marketWatchData = aMarketWatchData()
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
                    .withLastUpdated(DateConverter.fromEpochSeconds(m.getValue(MARKETWATCH.LAST_UPDATED).getTime()/1000))
                    .build();
            return marketWatchData;
        })
                .collect(Collectors.toMap(m -> m.getSymbol(), m -> m));
        return marketWatchDataMap;
    }

    void storeToDB(ConnAndDSL dsl, MarketWatchData marketWatchData, LocalDate currentDate) {
//  last_updated DATE NOT NULL,
//  history TEXT
        dsl.getDsl().insertInto(MARKETWATCH,
                MARKETWATCH.SYMBOL, MARKETWATCH.QUARTERS_ESTIMATE, MARKETWATCH.YEARS_ESTIMATE, MARKETWATCH.MEDIAN_PE_ON_CY,
                MARKETWATCH.NEXT_FISCAL_YEAR, MARKETWATCH.MEDIAN_PE_NEXT_FY, MARKETWATCH.LAST_QUARTER_EARNINGS,
                MARKETWATCH.YEAR_AGO_EARNINGS, MARKETWATCH.RECOMMENDATION, MARKETWATCH.NUMBER_OF_RATINGS,
                MARKETWATCH.BUY, MARKETWATCH.OVERWEIGHT, MARKETWATCH.HOLD, MARKETWATCH.UNDERWEIGHT, MARKETWATCH.SELL,
                MARKETWATCH.TARGET_PRICE, MARKETWATCH.LAST_UPDATED)
                .values(marketWatchData.getSymbol(), marketWatchData.getQuartersEstimate(), marketWatchData.getYearsEstimate(),
                        marketWatchData.getMedianPeOnCy(), marketWatchData.getNextFiscalYear(), marketWatchData.getMedianPeNextFy(),
                        marketWatchData.getLastQuarterEarnings(), marketWatchData.getYearAgoEarnings(), marketWatchData.getRecommendation(),
                        marketWatchData.getNumberOfRatings(), marketWatchData.getBuy(), marketWatchData.getOverweight(),
                        marketWatchData.getHold(), marketWatchData.getUnderweight(), marketWatchData.getSell(),
                        marketWatchData.getTargetPrice(), DateConverter.from(currentDate))
                .execute();
    }

    MarketWatchData parseHtmlPage(String symbol, String body, String url) throws ParsingException {
        try {
            Document doc = Jsoup.parse(body);
            Element estimates = doc.select("table.snapshot").first();
            if (estimates == null) {
                throw new ParsingException(String.format("Page %s doesn't contain table snapshot section", url));
            }
            Elements rows = estimates.select("tr");
            Map<String, String> snapshots = rows.stream()
                    .map(row -> {
                        Elements columns = row.select("td");
                        return Stream.of(SnapshotData.of(columns.get(0).text(), columns.get(1).text()),
                                SnapshotData.of(columns.get(2).text(), columns.get(3).text()));
                    })
                    .flatMap(stream -> stream)
                    .filter(snapshot -> snapshot.getDescription().length() > 0)
                    //.forEach(s -> System.out.println(String.format("%s=%s", s.getDescription(), s.getValue())));
                    .collect(Collectors.toMap(SnapshotData::getDescription, c -> c.getValue()));

            Element ratings = doc.select("table.ratings").first();
            final Map<String, String> ratingsMap = ratings.select("tr").stream()
                    .map(row -> {
                        final Stream<RecommendationRecord> result;
                        Elements columns = row.select("td");
                        if (columns.size() > 0) {
                            final String key = columns.get(0).text();
                            if (!"MEAN".equals(key)) {
                                result = Stream.of(RecommendationRecord.of(key, columns.get(1).text()));
                            } else {
                                result = Stream.empty();
                            }
                        } else {
                            result = Stream.empty();
                        }
                        return result;
                    })
                    .flatMap(Function.identity())
                    .collect(Collectors.toMap(RecommendationRecord::getKey, r -> r.getValue()));

            MarketWatchData marketWatchData = aMarketWatchData()
                    .withSymbol(symbol)
                    .withRecommendation(snapshots.get("Average Recommendation"))
                    .withTargetPrice(sanitizePrice(snapshots.get("Average Target Price")))
                    .withNumberOfRatings(sanitizeInteger(snapshots.get("Number of Ratings")))
                    .withQuartersEstimate(sanitizePrice(snapshots.get("Current Quarters Estimate")))
                    .withYearsEstimate(sanitizePrice(snapshots.get("Current Year's Estimate")))
                    .withLastQuarterEarnings(sanitizePrice(snapshots.get("Last Quarter's Earnings")))
                    .withMedianPeOnCy(sanitizePrice(snapshots.get("Median PE on CY Estimate")))
                    .withYearAgoEarnings(sanitizePrice(snapshots.get("Year Ago Earnings")))
                    .withNextFiscalYear(sanitizePrice(snapshots.get("Next Fiscal Year Estimate")))
                    .withMedianPeNextFy(sanitizePrice(snapshots.get("Median PE on Next FY Estimate")))
                    .withBuy(sanitizeInteger(ratingsMap.get("BUY")))
                    .withOverweight(sanitizeInteger(ratingsMap.get("OVERWEIGHT")))
                    .withHold(sanitizeInteger(ratingsMap.get("HOLD")))
                    .withUnderweight(sanitizeInteger(ratingsMap.get("UNDERWEIGHT")))
                    .withSell(sanitizeInteger(ratingsMap.get("SELL")))
                    .build();
            return marketWatchData;
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Problem parsing data for page: %s", url), e);
            throw new ParsingException(e);
        }
    }

    private Integer sanitizeInteger(String intValue) {
        if (intValue.length() == 0) {
            return null;
        } else {
            return Integer.parseInt(intValue);
        }
    }

    private BigDecimal sanitizePrice(String price) {
        final String priceWithoutCommas = price.replaceAll(",", "");
        try {
            return new BigDecimal(priceWithoutCommas).setScale(PRICE_SCALE, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            LOGGER.warning(String.format("Forced rounding for %s", price));
            return new BigDecimal(priceWithoutCommas).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
        }
    }

    private static class SnapshotData {
        private final String description;
        private final String value;

        public SnapshotData(String description, String value) {
            this.description = description;
            this.value = value;
        }

        public static SnapshotData of(String description, String value) {
            return new SnapshotData(stripUnwanted(description), value);
        }

        private static String stripUnwanted(String description) {
            return description.replaceFirst("([\\S\\s]*?)($|:)", "$1");
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("SnapshotData{");
            sb.append("description='").append(description).append('\'');
            sb.append(", value='").append(value).append('\'');
            sb.append('}');
            return sb.toString();
        }

        public String getDescription() {
            return description;
        }

        public String getValue() {
            return value;
        }
    }

    private static class RecommendationRecord {
        private final String key;
        private final String value;

        public RecommendationRecord(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("RecommendationRecord{");
            sb.append("key='").append(key).append('\'');
            sb.append(", value='").append(value).append('\'');
            sb.append('}');
            return sb.toString();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public static RecommendationRecord of(String key, String value) {
            return new RecommendationRecord(key, value);
        }
    }
}
