package org.superbiz.fetch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.jooq.Record;
import org.jooq.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.superbiz.dao.MarketWatchDAO;
import org.superbiz.dao.SecurityDAO;
import org.superbiz.fetch.model.MarketWatchData;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.DiffFinder;
import org.superbiz.util.GlobalInit;

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
import static org.superbiz.model.jooq.Tables.SECURITY;

public class FetchMarketWatch {

    private static final int PRICE_SCALE = 2;
    private static final TypeReference<List<MarketWatchData>> VALUE_TYPE_REF = new TypeReference<List<MarketWatchData>>() {
    };

    static { GlobalInit.init(); }

    @Inject
    Logger LOGGER;

    @Inject
    DefaultAsyncHttpClientConfig.Builder clientBuilder;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    MarketWatchDAO marketWatchDAO;

    @Inject
    SecurityDAO securityDAO;

    //private static final String URL_TEMPLATE = "https://www.marketwatch.com/investing/stock/amzn/analystestimates";
    private static final String URL_TEMPLATE = "https://www.marketwatch.com/investing/stock/%s/analystestimates";

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new BasicModule());
        FetchMarketWatch fetchMarketWatch = injector.getInstance(FetchMarketWatch.class);
        fetchMarketWatch.fetchAll();
    }

    private void fetchAll() throws IOException {
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);

        try (AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder)) {
            Map<String, MarketWatchData> marketWatchDataMap = marketWatchDAO.readMap();
            List<String> existingTodaySymbols = marketWatchDataMap.values().stream()
                    .filter(rec -> !rec.getLastUpdated().isBefore(currentDate))
                    .map(rec -> rec.getSymbol())
                    .collect(Collectors.toList());
            LOGGER.info(String.format("MarketWatch data already read for %s: %s", currentDate, existingTodaySymbols));

            Result<Record> securities = securityDAO.findAllSecuritiesExcept(existingTodaySymbols);
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
                        MarketWatchData marketWatchData = this.parseHtmlPage(symbol, response.getResponseBody(), url);
                        marketWatchData.setLastUpdated(currentDate);

                        MarketWatchData oldMarketWatchData = marketWatchDataMap.get(symbol);
                        if (oldMarketWatchData == null) {
                            marketWatchDAO.storeToDB(marketWatchData);
                        } else {
                            Optional<MarketWatchData> diffMarketWatchData = DiffFinder.computeDiff(oldMarketWatchData, marketWatchData);
                            if (diffMarketWatchData.isPresent()) {
                                String json = this.convertDiffToJson(diffMarketWatchData.get());
                                LOGGER.info(String.format("Difference for %s: %s", symbol, json));
                                //marketWatchData.setHistory(String.format("[%s]", json));
                                marketWatchData.setHistory(this.newHistory(oldMarketWatchData, diffMarketWatchData.get()));
                                marketWatchDAO.updateAll(marketWatchData);
                            } else {
                                marketWatchDAO.updateLastUpdate(marketWatchData);
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

//    Map<String, MarketWatchData> readFromDB(ConnAndDSL dsl, List<String> symbols) {
//        Result<Record> marketRecords = dsl.getDsl().select().from(MARKETWATCH).where(MARKETWATCH.SYMBOL.in(symbols)).fetch();
//        final Map<String, MarketWatchData> marketWatchDataMap = marketRecords.stream()
//                .map(m -> recordToMarketWatchData(m))
//                .collect(Collectors.toMap(m -> m.getSymbol(), m -> m));
//        return marketWatchDataMap;
//    }
//
//    Map<String, MarketWatchData> readFromDB(ConnAndDSL dsl) {
//        Result<Record> marketRecords = dsl.getDsl().select().from(MARKETWATCH).fetch();
//        final Map<String, MarketWatchData> marketWatchDataMap = marketRecords.stream()
//                .map(m -> recordToMarketWatchData(m))
//                .collect(Collectors.toMap(m -> m.getSymbol(), m -> m));
//        return marketWatchDataMap;
//    }

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
