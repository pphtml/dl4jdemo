package org.superbiz.fetch;

import org.nd4j.shade.jackson.databind.ObjectMapper;
import org.slf4j.helpers.MessageFormatter;
import org.superbiz.fetch.model.YahooResult;
import org.superbiz.util.DateConverter;
import org.superbiz.util.GlobalInit;

import java.io.IOException;
import java.time.LocalDate;

public class NetFetcherYahoo {
    private static final GlobalInit globalInit = new GlobalInit();

    public void fetch(String symbol) {
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
        System.out.println(url);
    }

    public static Object parseJSON(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            YahooResult result = objectMapper.readValue(json, YahooResult.class);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new NetFetcherYahoo().fetch("AMZN");
    }
}
