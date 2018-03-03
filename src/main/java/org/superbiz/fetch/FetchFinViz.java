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
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.nd4j.shade.jackson.core.type.TypeReference;
import org.nd4j.shade.jackson.databind.ObjectMapper;
import org.nd4j.shade.jackson.databind.SerializationFeature;
import org.nd4j.shade.jackson.databind.module.SimpleModule;
//import org.nd4j.shade.jackson.datatype.joda.deser.key.LocalDateKeyDeserializer;
import org.superbiz.dao.MarketFinVizDAO;
import org.superbiz.dao.SecurityDAO;
import org.superbiz.dto.MarketFinVizDTO;
import org.superbiz.fetch.model.MarketWatchData;
import org.superbiz.fetch.model.finviz.AnalystEstimate;
import org.superbiz.fetch.model.finviz.DayParameters;
import org.superbiz.fetch.model.finviz.InsiderTrade;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.*;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.superbiz.dto.MarketFinVizDTO.MarketFinVizDTOBuilder.createMarketFinVizDTO;
import static org.superbiz.model.jooq.Tables.SECURITY;

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

    @Inject
    InsiderTradeParser insiderTradeParser;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(LocalDate.class, new LocalDateDeserializer());
        OBJECT_MAPPER.registerModule(simpleModule);

        //OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new BasicModule());
        FetchFinViz fetchData = injector.getInstance(FetchFinViz.class);
        //fetchData.fetchAll();
        fetchData.findBySymbol("AMZN");
    }

    private void findBySymbol(String symbol) {
        Optional<MarketFinVizDTO> oldFinVizDTO = marketFinVizDAO.findBySymbol(symbol);
        if (oldFinVizDTO.isPresent()) {
            FinVizVO finViz = FinVizVO.of(oldFinVizDTO.get().getParameters(), oldFinVizDTO.get().getAnalysts(), oldFinVizDTO.get().getInsiders());
            System.out.println(finViz);
        }
    }

    private void fetchAll() throws IOException {
        LOGGER.info("Starting");
        try (AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder)) {

            List<String> knownSecurities = marketFinVizDAO.findFreshDataSymbols(LocalDate.now().atStartOfDay());
            Result<Record> securities = securityDAO.findAllSecuritiesExcept(knownSecurities);

            securities.stream().forEach(security -> {
                final String symbol = security.get(SECURITY.SYMBOL);
                final String url = createUrl(localSymbol(symbol));
                final Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                        .setUrl(url)
                        .build();
                final ListenableFuture<Response> responseFuture = client.executeRequest(getRequest);

                try {
                    Response response = responseFuture.get();
                    LOGGER.info(String.format("%s -> %s (%s)", symbol, response.getStatusCode(), url));
                    FinVizVO finVizWebResult = processData(symbol, response.getResponseBody(), url);

                    Optional<MarketFinVizDTO> oldFinVizDTO = marketFinVizDAO.findBySymbol(symbol);

                    FinVizVO finVizMerged = mergeFinVizData(oldFinVizDTO, finVizWebResult);

                    MarketFinVizDTO marketFinVizDTO = createMarketFinVizDTO()
                            .withSymbol(symbol)
                            .withParameters(finVizWebResult.getParametersAsBytes())
                            .withAnalysts(finVizWebResult.getAnalystsAsBytes())
                            .withInsiders(finVizWebResult.getInsidersAsBytes())
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

    FinVizVO mergeFinVizData(Optional<MarketFinVizDTO> oldFinVizDTO, FinVizVO newFinVizVO) {
        if (oldFinVizDTO.isPresent()) {
            final FinVizVO oldFinVizVO = FinVizVO.of(oldFinVizDTO.get().getParameters(),
                    oldFinVizDTO.get().getAnalysts(),
                    oldFinVizDTO.get().getInsiders());
            Map<LocalDate, DayParameters> oldParameters = oldFinVizVO.getParameters();
            if (newFinVizVO.getParameters().size() != 1) {
                throw new IllegalArgumentException(String.format("Size of new parameters is supposed to be of size 1, not %d",
                        newFinVizVO.getParameters().size()));
            }
            Map<LocalDate, DayParameters> parametersMerged = mergeParameters(oldParameters, newFinVizVO.getParameters());
            List<AnalystEstimate> analystMerged = ListMerger.mergeLists(oldFinVizVO.getAnalysts(), newFinVizVO.getAnalysts());
            List<InsiderTrade> insiderMerged = ListMerger.mergeLists(oldFinVizVO.getInsiders(), newFinVizVO.getInsiders());
            return FinVizVO.of(parametersMerged, analystMerged, insiderMerged);
        } else {
            return newFinVizVO;
        }
    }

    Map<LocalDate,DayParameters> mergeParameters(Map<LocalDate, DayParameters> oldParameters, Map<LocalDate, DayParameters> newParameters) {
        Map<LocalDate, DayParameters> mergedParameters = oldParameters.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        LocalDate lastDate = newParameters.keySet().iterator().next();
        DayParameters lastParameters = newParameters.values().iterator().next();
        if (oldParameters.containsKey(lastDate)) {
            LOGGER.warning(String.format("Old parameters already had data for date %s, updating.", lastDate));
            mergedParameters.put(lastDate, lastParameters);
        } else {
            LocalDate lastOldDate = oldParameters.keySet().stream()
                    .max(Comparator.comparing(date -> date))
                    .orElseThrow(() -> new IllegalArgumentException("Empty old parameters dictionary"));

            Optional<DayParameters> diffParameters = MapMerger.findUpdates(lastParameters,
                    oldParameters.get(lastOldDate));
        }
        return mergedParameters;
    }

    private String localSymbol(String symbol) {
        return symbol.replaceAll("\\.", "-");
    }

    FinVizVO processData(String symbol, String body, String url) throws ParsingException {
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);

        try {
            Document doc = Jsoup.parse(body);
            Element estimates = doc.select("table.snapshot-table2").first();

            if (estimates == null) {
                throw new ParsingException(String.format("Page %s doesn't contain table snapshot-table2 section", url));
            }
            Elements rows = estimates.select("tr");
            List<ParameterRecord> parametersList = rows.stream()
                    .map(row -> {
                        Elements columns = row.select("td");
                        return IntStream.range(0, columns.size() / 2)
                                .mapToObj(index -> {
                                    ParameterRecord parameterRecord = ParameterRecord.of(columns.get(index * 2).text(), columns.get(index * 2 + 1).text());
                                    //LOGGER.info(String.format("%s", parameterRecord));
                                    return parameterRecord;
                                });
//                        return Stream.of(ParameterRecord.of(columns.get(0).text(), columns.get(1).text()),
//                                ParameterRecord.of(columns.get(2).text(), columns.get(3).text()));
                    })
                    .flatMap(stream -> stream)
                    .filter(record -> !record.label.matches("ATR|Index|SMA.*|RSI.*|52W.*|.*Volume|Prev Close|Price|Change"))
                    .collect(Collectors.toList());

            Optional<ParameterRecord> firstEps = parametersList.stream()
                    .filter(record -> record.label.equals("EPS next Y")).findFirst();
            Map<String, String> parameters = Stream.concat(parametersList.stream()
                            .filter(record -> !record.label.equals("EPS next Y"))
                    ,
                    firstEps.isPresent() ? Stream.of(firstEps.get()) : Stream.empty())
                    .collect(Collectors.toMap(c -> c.getLabel(),
                            c -> c.getValue()));
//            parameters.put()

//            String list = parameters.keySet().stream()
//                    .sorted()
//                    .collect(Collectors.joining("\n"));
//            LOGGER.info(String.format("%d\n%s", parameters.size(), list));



            Element ratingsOuter = doc.select("table.fullview-ratings-outer").first();
            final List<AnalystEstimate> analystEstimates = ratingsOuter == null ? Collections.emptyList() :
                    ratingsOuter.select("table table tr").stream()
                    .map(rec -> {
                        List<String> tds = rec.select("td").stream()
                                .map(e -> e.text())
                                .collect(Collectors.toList());
                        return AnalystEstimate.of(tds.get(0), tds.get(1), tds.get(2), tds.get(3), tds.get(4));
                    })
                    .collect(Collectors.toList());



            Element insiderTrading = doc.select("table.body-table").first();
            List<InsiderTrade> insiderTradings = insiderTrading == null ? Collections.emptyList() :
                    insiderTrading.select("tr.insider-option-row").stream()
                    .map(rec -> {
                        List<String> tds = rec.select("td").stream()
                                .map(e -> e.text())
                                .collect(Collectors.toList());
                        return insiderTradeParser.parse(tds.get(1), tds.get(2), tds.get(3), tds.get(4),
                                tds.get(5), tds.get(6), tds.get(7), tds.get(8));
                    })
                    .collect(Collectors.toList());

            return FinVizVO.ofSingleParameters(currentDate, parameters, analystEstimates, insiderTradings);
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Problem parsing data for page: %s", url), e);
            throw new ParsingException(e);
        }
    }

    public static class FinVizVO {
        private final Map<LocalDate, DayParameters> parameters;
        private final List<AnalystEstimate> analysts;
        private final List<InsiderTrade> insiders;

        public FinVizVO(Map<LocalDate, DayParameters> parameters, List<AnalystEstimate> analysts, List<InsiderTrade> insiders) {
            this.parameters = parameters;
            this.analysts = analysts;
            this.insiders = insiders;
        }

//        public FinVizVO(Map<String, String> parameters, List<AnalystEstimate> analysts, List<InsiderTrade> insiders) {
//            this.parameters = parameters;
//            this.analysts = analysts;
//            this.insiders = insiders;
//        }
//
        public Map<LocalDate, DayParameters> getParameters() {
            return parameters;
        }

        public List<AnalystEstimate> getAnalysts() {
            return analysts;
        }

        public List<InsiderTrade> getInsiders() {
            return insiders;
        }

        //        private MarketFinVizDTO.Parameters parameters;
//        private MarketFinVizDTO.Analysts analysts;
//        private MarketFinVizDTO.Insiders insiders;
//        private List<String> warningMessages;


        public static FinVizVO of(Map<LocalDate, DayParameters> parameters, List<AnalystEstimate> analystEstimates, List<InsiderTrade> insiderTradings) {
            FinVizVO result = new FinVizVO(parameters, analystEstimates, insiderTradings);
            return result;
        }

        public static FinVizVO ofSingleParameters(LocalDate date, Map<String, String> singleParameters, List<AnalystEstimate> analystEstimates, List<InsiderTrade> insiderTradings) {
            Map<LocalDate, DayParameters> parameters = new HashMap<>(1);
            parameters.put(date, DayParameters.of(singleParameters));
            return of(parameters, analystEstimates, insiderTradings);
        }


        private static final TypeReference<Map<LocalDate, DayParameters>> TYPE_REF_PARAMETERS = new TypeReference<Map<LocalDate, DayParameters>>() {
        };

        private static final TypeReference<List<AnalystEstimate>> TYPE_REF_ANALYSTS = new TypeReference<List<AnalystEstimate>>() {
        };

        private static final TypeReference<List<InsiderTrade>> TYPE_REF_INSIDERS = new TypeReference<List<InsiderTrade>>() {
        };

        public static FinVizVO of(byte[] parameters, byte[] analystEstimates, byte[] insiderTradings) {
            try {
                String jsonParameters = GzipUtil.unzip(parameters);
                Map<LocalDate, DayParameters> parametersObject = OBJECT_MAPPER.readValue(jsonParameters, TYPE_REF_PARAMETERS);

                String jsonAnalysts = GzipUtil.unzip(analystEstimates);
                List<AnalystEstimate> analystEstimatesObject = OBJECT_MAPPER.readValue(jsonAnalysts, TYPE_REF_ANALYSTS);

                String jsonInsiders = GzipUtil.unzip(insiderTradings);
                List<InsiderTrade> insiderTradingsObject = OBJECT_MAPPER.readValue(jsonInsiders, TYPE_REF_INSIDERS);


                FinVizVO result = new FinVizVO(parametersObject, analystEstimatesObject, insiderTradingsObject);
                return result;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] getParametersAsBytes() {
            try {
                String json = OBJECT_MAPPER.writeValueAsString(parameters);
                return GzipUtil.zip(json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] getAnalystsAsBytes() {
            try {
                String json = OBJECT_MAPPER.writeValueAsString(analysts);
                return GzipUtil.zip(json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] getInsidersAsBytes() {
            try {
                String json = OBJECT_MAPPER.writeValueAsString(insiders);
                return GzipUtil.zip(json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
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
