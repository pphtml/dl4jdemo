package org.superbiz.fetch;

import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.jooq.Record;
import org.jooq.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.nd4j.shade.jackson.core.type.TypeReference;
import org.nd4j.shade.jackson.databind.ObjectMapper;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.fetch.model.MarketWatchData;
import org.superbiz.util.DateConverter;
import org.superbiz.util.DiffFinder;
import org.superbiz.util.GlobalInit;
import org.superbiz.util.HttpUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
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
    public static final TypeReference<List<MarketWatchData>> VALUE_TYPE_REF = new TypeReference<List<MarketWatchData>>() {
    };

    private ObjectMapper objectMapper = new ObjectMapper();


    static { new GlobalInit(); }

    private static final Logger LOGGER = Logger.getLogger(FetchMarketWatch.class.getName());

    //private static final String URL_TEMPLATE = "https://www.marketwatch.com/investing/stock/amzn/analystestimates";
    private static final String URL_TEMPLATE = "https://www.marketwatch.com/investing/stock/%s/analystestimates";

    private static final FetchMarketWatch fetchMarketWatch = new FetchMarketWatch();

    public static void main(String[] args) throws IOException {
        DefaultAsyncHttpClientConfig.Builder clientBuilder = HttpUtils.getHttpAgentBuilder();
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);

        try (AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);
             ConnAndDSL dsl = ConnAndDSL.create()) {
            Map<String, MarketWatchData> marketWatchDataMap = fetchMarketWatch.readFromDB(dsl);
            List<String> existingTodaySymbols = marketWatchDataMap.values().stream()
                    .filter(rec -> !rec.getLastUpdated().isBefore(currentDate))
                    .map(rec -> rec.getSymbol())
                    .collect(Collectors.toList());
//            Result<Record> existingMarketData = dsl.getDsl().select()
//                    .from(MARKETWATCH)
//                    //.where(MARKETWATCH.LAST_UPDATED.eq(DateConverter.from(currentDate)))
//                    .fetch();
//            List<String> existingMarketDataList = existingMarketData.stream()
//                    .filter(rec -> {
//                        return !rec.getValue(MARKETWATCH.LAST_UPDATED).toLocalDate().isBefore(currentDate);
//                    })
//                    .map(rec -> rec.getValue(MARKETWATCH.SYMBOL))
//                    .collect(Collectors.toList());
            LOGGER.info(String.format("MarketWatch data already read for %s: %s", currentDate, existingTodaySymbols));

            Result<Record> securities = dsl.getDsl()
                    .select()
                    .from(SECURITY)
                    .where(SECURITY.SYMBOL.notIn(existingTodaySymbols))
                    .orderBy(SECURITY.SYMBOL)
                    .fetch();
            securities.stream().forEach(security -> {
                String symbol = security.getValue(SECURITY.SYMBOL);
                String url = String.format(URL_TEMPLATE, symbol.toLowerCase());
                Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                        .setUrl(url)
                        .build();

                ListenableFuture<Response> responseFuture = client.executeRequest(getRequest);
                try {
                    Response response = responseFuture.get();
                    LOGGER.info(String.format("%s -> %s", symbol, response.getStatusCode()));

                    try {
                        MarketWatchData marketWatchData = fetchMarketWatch.parseHtmlPage(symbol, response.getResponseBody(), url);
                        marketWatchData.setLastUpdated(currentDate);

                        MarketWatchData oldMarketWatchData = marketWatchDataMap.get(symbol);
                        if (oldMarketWatchData == null) {
                            fetchMarketWatch.storeToDB(dsl, marketWatchData);
                        } else {
                            Optional<MarketWatchData> diffMarketWatchData = DiffFinder.computeDiff(oldMarketWatchData, marketWatchData);
                            if (diffMarketWatchData.isPresent()) {
                                String json = fetchMarketWatch.convertDiffToJson(diffMarketWatchData.get());
                                LOGGER.info(String.format("Difference for %s: %s", symbol, json));
                                //marketWatchData.setHistory(String.format("[%s]", json));
                                marketWatchData.setHistory(fetchMarketWatch.newHistory(oldMarketWatchData, diffMarketWatchData.get()));
                                fetchMarketWatch.updateAll(dsl, marketWatchData);
                            } else {
                                fetchMarketWatch.updateLastUpdate(dsl, marketWatchData);
                            }
                        }
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

    String newHistory(MarketWatchData marketWatchData, MarketWatchData diffMarketWatchData) {
        List<MarketWatchData> result = new ArrayList<>();
        List<MarketWatchData> diffs = unmarshallHistoryRecords(marketWatchData);
        result.add(diffMarketWatchData);
        result.addAll(diffs);
        String json = marshallHistoryRecords(result);
        return json;
    }

    String marshallHistoryRecords(List<MarketWatchData> diffs) {
        try {
            return objectMapper.writeValueAsString(diffs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    List<MarketWatchData> unmarshallHistoryRecords(MarketWatchData marketWatchData) {
        if (marketWatchData.getHistory() == null) {
            return Collections.emptyList();
        } else {
            try {
                return objectMapper.readValue(marketWatchData.getHistory(), VALUE_TYPE_REF);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateAll(ConnAndDSL dsl, MarketWatchData marketWatchData) {
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

    private void updateLastUpdate(ConnAndDSL dsl, MarketWatchData marketWatchData) {
        dsl.getDsl().update(MARKETWATCH)
                .set(MARKETWATCH.LAST_UPDATED, DateConverter.from(marketWatchData.getLastUpdated()))
                .where(MARKETWATCH.SYMBOL.eq(marketWatchData.getSymbol()))
                .execute();
    }

    Map<String, MarketWatchData> readFromDB(ConnAndDSL dsl, List<String> symbols) {
        Result<Record> marketRecords = dsl.getDsl().select().from(MARKETWATCH).where(MARKETWATCH.SYMBOL.in(symbols)).fetch();
        final Map<String, MarketWatchData> marketWatchDataMap = marketRecords.stream()
                .map(m -> recordToMarketWatchData(m))
                .collect(Collectors.toMap(m -> m.getSymbol(), m -> m));
        return marketWatchDataMap;
    }

    Map<String, MarketWatchData> readFromDB(ConnAndDSL dsl) {
        Result<Record> marketRecords = dsl.getDsl().select().from(MARKETWATCH).fetch();
        final Map<String, MarketWatchData> marketWatchDataMap = marketRecords.stream()
                .map(m -> recordToMarketWatchData(m))
                .collect(Collectors.toMap(m -> m.getSymbol(), m -> m));
        return marketWatchDataMap;
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

    void storeToDB(ConnAndDSL dsl, MarketWatchData marketWatchData) {
//  last_updated DATE NOT NULL,
//  history TEXT
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
        if (price.equals("N/A")) {
            return null;
        }

        final String priceWithoutCommas = price.replaceAll(",", "");
        try {
            return new BigDecimal(priceWithoutCommas).setScale(PRICE_SCALE, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            LOGGER.warning(String.format("Forced rounding for %s", price));
            return new BigDecimal(priceWithoutCommas).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
        }
    }

    public Optional<MarketWatchData> computeDiff(MarketWatchData oldMarketWatchData, MarketWatchData newMarketWatchData) {
        return DiffFinder.computeDiff(oldMarketWatchData, newMarketWatchData);
    }

    public String convertDiffToJson(MarketWatchData diffMarketWatchData) {
        try {
            return objectMapper.writeValueAsString(diffMarketWatchData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
