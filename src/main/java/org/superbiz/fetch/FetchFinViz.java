package org.superbiz.fetch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.jooq.Record;
import org.jooq.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.superbiz.dao.MarketFinVizDAO;
import org.superbiz.dao.Price5mDAO;
import org.superbiz.dao.SecurityDAO;
import org.superbiz.dto.MarketFinVizDTO;
import org.superbiz.dto.PriceDTO;
import org.superbiz.fetch.model.MarketWatchData;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.GlobalInit;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.superbiz.dto.MarketFinVizDTO.MarketFinVizDTOBuilder.createMarketFinVizDTO;
import static org.superbiz.dto.PriceDTO.PriceDTOBuilder.createPriceDTO;
import static org.superbiz.fetch.model.MarketWatchData.MarketWatchDataBuilder.aMarketWatchData;
import static org.superbiz.model.jooq.Tables.MARKET_FIN_VIZ;
import static org.superbiz.model.jooq.Tables.SECURITY;
import static org.superbiz.util.DateConverter.toLocalDateTime;

public class FetchFinViz {
    static { GlobalInit.init(); }

    @Inject
    Logger LOGGER;

    @Inject
    DefaultAsyncHttpClientConfig.Builder clientBuilder;

    @Inject
    SecurityDAO securityDAO;

    @Inject
    MarketFinVizDAO marketFinVizDAO;

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new BasicModule());
        FetchFinViz fetchData = injector.getInstance(FetchFinViz.class);
        fetchData.fetchAll();
    }

    private void fetchAll() throws IOException {
        LOGGER.info("Starting");
        try (AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder)) {
            Result<Record> securities = securityDAO.fetchAll();
            securities.stream().forEach(security -> {
                final String symbol = security.get(SECURITY.SYMBOL);
                final String url = createUrl(symbol);
                final Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                        .setUrl(url)
                        .build();
                final ListenableFuture<Response> responseFuture = client.executeRequest(getRequest);

                try {
                    Response response = responseFuture.get();
                    LOGGER.info(String.format("%s -> %s (%s)", symbol, response.getStatusCode(), url));
                    FinVizWebResultVO finVizWebResult = processData(symbol, response.getResponseBody(), url);
                    MarketFinVizDTO marketFinVizDTO = createMarketFinVizDTO()
                            .withSymbol(symbol)
                            .withParameters(finVizWebResult.getParameters())
                            .withAnalysts(finVizWebResult.getAnalysts())
                            .withInsiders(finVizWebResult.getInsiders())
                            .withLastUpdatedSuccess(LocalDateTime.now())
                            .build();
                    marketFinVizDAO.insertOrUpdate(marketFinVizDTO);
                } catch (InterruptedException | ExecutionException | DataProcessingException | ParsingException e) {
                    LOGGER.log(Level.WARNING, String.format("Cannot read FinViz data for %s, reason: %s",
                            symbol, e.getMessage()), e);
                    String errorMessageForDB = String.format("%s: %s", e.getClass(), e.getMessage());
                    MarketFinVizDTO marketFinVizDTO = createMarketFinVizDTO()
                            .withSymbol(symbol)
                            .withLastUpdatedError(LocalDateTime.now())
                            .withLastError(errorMessageForDB)
                            .build();
                    marketFinVizDAO.insertOrUpdateError(marketFinVizDTO);
                }
            });
        }
        LOGGER.info("Finished");
    }

    FinVizWebResultVO processData(String symbol, String body, String url) throws ParsingException {
        try {
            Document doc = Jsoup.parse(body);
            Element estimates = doc.select("table.snapshot-table2").first();

            if (estimates == null) {
                throw new ParsingException(String.format("Page %s doesn't contain table snapshot-table2 section", url));
            }
            Elements rows = estimates.select("tr");
            Map<String, String> parameters = rows.stream()
            //List<ParameterRecord> parameters = rows.stream()
                    .map(row -> {
                        Elements columns = row.select("td");
                        return IntStream.range(0, columns.size() / 2)
                                .mapToObj(index -> {
                                    ParameterRecord parameterRecord = ParameterRecord.of(columns.get(index * 2).text(), columns.get(index * 2 + 1).text());
                                    LOGGER.info(String.format("%s", parameterRecord));
                                    return parameterRecord;
                                });
//                        return Stream.of(ParameterRecord.of(columns.get(0).text(), columns.get(1).text()),
//                                ParameterRecord.of(columns.get(2).text(), columns.get(3).text()));
                    })
                    .flatMap(stream -> stream)
                    //.filter(snapshot -> snapshot.getLabel().length() > 0)
                    //.forEach(s -> System.out.println(String.format("%s=%s", s.getDescription(), s.getValue())));
                    //.collect(Collectors.toList());
                    .collect(Collectors.toMap(ParameterRecord::getLabel,
                            c -> c.getValue(),
                            (key1, key2) -> {
                                LOGGER.warning(String.format("Duplicate key found: %s", key1));
                                return key1;
                            }));

            System.out.println("tady");
//            Element ratings = doc.select("table.ratings").first();
//            final Map<String, String> ratingsMap = ratings.select("tr").stream()
//                    .map(row -> {
//                        final Stream<FetchMarketWatch.RecommendationRecord> result;
//                        Elements columns = row.select("td");
//                        if (columns.size() > 0) {
//                            final String key = columns.get(0).text();
//                            if (!"MEAN".equals(key)) {
//                                result = Stream.of(FetchMarketWatch.RecommendationRecord.of(key, columns.get(1).text()));
//                            } else {
//                                result = Stream.empty();
//                            }
//                        } else {
//                            result = Stream.empty();
//                        }
//                        return result;
//                    })
//                    .flatMap(Function.identity())
//                    .collect(Collectors.toMap(FetchMarketWatch.RecommendationRecord::getKey, r -> r.getValue()));
//
//            MarketWatchData marketWatchData = aMarketWatchData()
//                    .withSymbol(symbol)
//                    .withRecommendation(snapshots.get("Average Recommendation"))
//                    .withTargetPrice(sanitizePrice(snapshots.get("Average Target Price")))
//                    .withNumberOfRatings(sanitizeInteger(snapshots.get("Number of Ratings")))
//                    .withQuartersEstimate(sanitizePrice(snapshots.get("Current Quarters Estimate")))
//                    .withYearsEstimate(sanitizePrice(snapshots.get("Current Year's Estimate")))
//                    .withLastQuarterEarnings(sanitizePrice(snapshots.get("Last Quarter's Earnings")))
//                    .withMedianPeOnCy(sanitizePrice(snapshots.get("Median PE on CY Estimate")))
//                    .withYearAgoEarnings(sanitizePrice(snapshots.get("Year Ago Earnings")))
//                    .withNextFiscalYear(sanitizePrice(snapshots.get("Next Fiscal Year Estimate")))
//                    .withMedianPeNextFy(sanitizePrice(snapshots.get("Median PE on Next FY Estimate")))
//                    .withBuy(sanitizeInteger(ratingsMap.get("BUY")))
//                    .withOverweight(sanitizeInteger(ratingsMap.get("OVERWEIGHT")))
//                    .withHold(sanitizeInteger(ratingsMap.get("HOLD")))
//                    .withUnderweight(sanitizeInteger(ratingsMap.get("UNDERWEIGHT")))
//                    .withSell(sanitizeInteger(ratingsMap.get("SELL")))
//                    .build();
//            return marketWatchData;
            return null;
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Problem parsing data for page: %s", url), e);
            throw new ParsingException(e);
        }
    }

    public static class FinVizWebResultVO {
        private MarketFinVizDTO.Parameters parameters;
        private MarketFinVizDTO.Analysts analysts;
        private MarketFinVizDTO.Insiders insiders;
        private List<String> warningMessages;

        public MarketFinVizDTO.Parameters getParameters() {
            return parameters;
        }

        public void setParameters(MarketFinVizDTO.Parameters parameters) {
            this.parameters = parameters;
        }

        public MarketFinVizDTO.Analysts getAnalysts() {
            return analysts;
        }

        public void setAnalysts(MarketFinVizDTO.Analysts analysts) {
            this.analysts = analysts;
        }

        public MarketFinVizDTO.Insiders getInsiders() {
            return insiders;
        }

        public void setInsiders(MarketFinVizDTO.Insiders insiders) {
            this.insiders = insiders;
        }

        public List<String> getWarningMessages() {
            return warningMessages;
        }

        public void setWarningMessages(List<String> warningMessages) {
            this.warningMessages = warningMessages;
        }
    }

    private String createUrl(String symbol) {
        return String.format("https://finviz.com/quote.ashx?t=%s", symbol);
    }

    private static class ParameterRecord {
        private final String label;
        private final String value;

        public ParameterRecord(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public static ParameterRecord of(String label, String value) {
            return new ParameterRecord(label, value);
        }

        @Override
        public String toString() {
            return "ParameterRecord{" +
                    "label='" + label + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
