package org.superbiz.fetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.superbiz.fetch.model.*;
import org.superbiz.util.DateConverter;
import org.superbiz.util.GlobalInit;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.superbiz.fetch.model.Event.EventBuilder.createEvent;

public class NetFetcherYahoo {
    private static final GlobalInit globalInit = new GlobalInit();

    @Inject
    Logger logger;

    public String createUrl(String symbol, String interval, Integer daysBack) {
        LocalDate dateEnd = LocalDate.now().plusDays(1);
        LocalDate dateStart = dateEnd.minusDays(daysBack);

        String localSymbolUrlEncoded = localSymbolUrlEncoded(symbol);
        String urlTemplate = "https://query1.finance.yahoo.com/v8/finance/chart/%s?" +
                "symbol=%s&" +
                "period1=%d&" +
                "period2=%d&" +
                "interval=%s&" +
                "indicators=quote&" +
                "includeTimestamps=true&" +
                "includePrePost=false&" +
                "events=div%%7Csplit%%7Cearn&" +
                "corsDomain=finance.yahoo.com";
        String url = String.format(urlTemplate,
                localSymbolUrlEncoded,
                localSymbolUrlEncoded,
                DateConverter.toEpochSeconds(dateStart),
                DateConverter.toEpochSeconds(dateEnd),
                interval);
        return url;
    }

    private String localSymbolUrlEncoded(String symbol) {
        String replaced = symbol.replaceAll("\\.", "-");
        try {
            return URLEncoder.encode(replaced, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ParsingResult processData(String json) throws ParsingException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            YahooResult result = objectMapper.readValue(json, YahooResult.class);
            if (result.getChart().getError() != null && result.getChart().getError().getCode() != null) {
                YahooError error = result.getChart().getError();
                throw new DataProcessingException(String.format("Code: %s, Description: %s",
                        error.getCode(), error.getDescription()));
            } else {
                List<TickData> translatedResult = translateToTickData(result);
                return ParsingResult.of(translatedResult);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new ParsingException(String.format("processData exception: %s",
                    ExceptionUtils.getStackTrace(e)));
        }
    }

    private List<TickData> translateToTickData(YahooResult result) throws ParsingException {
        try {
            YahooChart chart = result.getChart();
            final List<YahooData> chartResult = chart.getResult();
            final YahooData yahooData = chartResult.get(0);
            final String symbol = yahooData.getMeta().getSymbol();
            final List<Long> timestamps = yahooData.getTimestamp();
            final YahooIndicators indicators = yahooData.getIndicators();
            final List<YahooQuote> quote = indicators.getQuote();
            final YahooQuote quoteLists = quote.get(0);
            final List<BigDecimal> opens = quoteLists.getOpen();
            final List<BigDecimal> highs = quoteLists.getHigh();
            final List<BigDecimal> lows = quoteLists.getLow();
            final List<BigDecimal> closes = quoteLists.getClose();
            final List<Long> volumes = quoteLists.getVolume();
            final YahooEvents events = yahooData.getEvents();
            if (timestamps.size() != opens.size()) {
                throw new ParsingException(String.format("Number of timestamps and opening values differs for %s (%s != %s)",
                        symbol, timestamps.size(), opens.size()));
            }

            List<TickData> listOfTickData = IntStream.range(0, timestamps.size())
                    .mapToObj(index -> {
                        final Long timestamp = timestamps.get(index);
                        TickData tickData = TickData.from(timestamp,
                                opens.get(index),
                                highs.get(index),
                                lows.get(index),
                                closes.get(index),
                                volumes.get(index));
                        if (events != null) {
                            List<Event> eventList = new ArrayList<>();
                            if (events.getDividends() != null) {
                                YahooDividend dividend = events.getDividends().get(timestamp);
                                if (dividend != null) {
                                    Event event = createEvent()
                                            .withEventType(EventType.DIVIDEND)
                                            .withAmount(dividend.getAmount())
                                            .build();
                                    eventList.add(event);
                                }
                            }
                            if (events.getSplits() != null) {
                                YahooSplit split = events.getSplits().get(timestamp);
                                if (split != null) {
                                    Event event = createEvent()
                                            .withEventType(EventType.SPLIT)
                                            .withNumerator(split.getNumerator())
                                            .withDenominator(split.getDenominator())
                                            .build();
                                    eventList.add(event);
                                }
                            }
                            tickData.setEvents(eventList);
                        }
                        return tickData;
                    })
                    .collect(Collectors.toList());
            return listOfTickData;
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(String.format("Generic exception caught: %s",
                    ExceptionUtils.getStackTrace(e)));
        }
    }

    public static void main(String[] args) {
        System.out.println(new NetFetcherYahoo().createUrl("^GSPC", "1d", 3600));
        //System.out.println(new NetFetcherYahoo().createUrl("AMZN", "1d", 3600));
    }
}
