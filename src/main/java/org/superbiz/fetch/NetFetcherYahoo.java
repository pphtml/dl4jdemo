package org.superbiz.fetch;

import org.nd4j.shade.jackson.databind.ObjectMapper;
import org.slf4j.helpers.MessageFormatter;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.fetch.model.TickData;
import org.superbiz.fetch.model.YahooChart;
import org.superbiz.fetch.model.YahooData;
import org.superbiz.fetch.model.YahooIndicators;
import org.superbiz.fetch.model.YahooQuote;
import org.superbiz.fetch.model.YahooResult;
import org.superbiz.util.DateConverter;
import org.superbiz.util.GlobalInit;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetFetcherYahoo {
    private static final GlobalInit globalInit = new GlobalInit();

    public String createUrl(String symbol) {
        LocalDate dateEnd = LocalDate.now().plusDays(1);
        LocalDate dateStart = dateEnd.minusDays(60);


//        String url = "https://query2.finance.yahoo.com/v7/finance/chart/{{symbol}}?' \
//        'period2={period_to}&' \
//        'period1={period_from}&' \
//        'interval={interval}&' \
//        'indicators={indicators}&' \
//        'includeTimestamps=true&' \
//        'includePrePost=false&' \
//        'events=div%7Csplit%7Cearn&' \
//        'corsDomain=finance.yahoo.com'.\
//        format(period_from=period_from,
//                period_to=period_to,
//                interval=interval,
//                indicators=indicators.replace('|', '%7C'))

        String urlTemplate = "https://query1.finance.yahoo.com/v8/finance/chart/%s?" +
                "symbol=%s&" +
                "period1=%d&" +
                "period2=%d&" +
                "interval=5m&" +
                "indicators=quote&" +
                "includeTimestamps=true&" +
                "includePrePost=false&" +
                "events=div%%7Csplit%%7Cearn&" +
                "corsDomain=finance.yahoo.com";
        String url = String.format(urlTemplate,
                symbol,
                symbol,
                DateConverter.toEpochSeconds(dateStart),
                DateConverter.toEpochSeconds(dateEnd));
        return url;
    }

    public ParsingResult processData(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            YahooResult result = objectMapper.readValue(json, YahooResult.class);
            List<TickData> translatedResult = translateToTickData(result);
            return ParsingResult.of(translatedResult);
        } catch (IOException | ParsingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<TickData> translateToTickData(YahooResult result) throws ParsingException {
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
        if (timestamps.size() != opens.size()) {
            throw new ParsingException(String.format("Number of timestamps and opening values differs for %s (%s != %s)",
                    symbol, timestamps.size(), opens.size()));
        }

        List<TickData> listOfTickData = IntStream.range(0, timestamps.size())
                .mapToObj(index -> TickData.from(timestamps.get(index),
                        opens.get(index),
                        highs.get(index),
                        lows.get(index),
                        closes.get(index),
                        volumes.get(index)))
                .collect(Collectors.toList());
        return listOfTickData;
    }

    public static void main(String[] args) {
        // new NetFetcherYahoo().fetch("AMZN");
    }
}
