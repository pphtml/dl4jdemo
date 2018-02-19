package org.superbiz.fetch;

import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.util.GlobalInit;
import org.superbiz.util.LoggingConfig;

import java.util.concurrent.ExecutionException;

import static org.superbiz.model.jooq.Tables.SECURITY;

public class FetchMarketWatch {
    static { new GlobalInit(); }

    //private static final String URL_TEMPLATE = "https://www.marketwatch.com/investing/stock/amzn/analystestimates";
    private static final String URL_TEMPLATE = "https://www.marketwatch.com/investing/stock/%s/analystestimates";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
                .setConnectTimeout(10000);
        //.setProxyServer(new ProxyServer(...));
        AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);

        try (ConnAndDSL dsl = ConnAndDSL.create()) {
            Result<Record> securities = dsl.getDsl().select().from(SECURITY).orderBy(SECURITY.SYMBOL).fetch();
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
                    System.out.println(String.format("%s -> %s", symbol, response.getStatusCode()));
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(security);
                    throw new RuntimeException(e);
                }
            });
        }
    }


}
