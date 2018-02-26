package org.superbiz.fetch;

import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.dao.PriceDAO;
import org.superbiz.dao.SecurityDAO;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.dto.PriceDTO;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.util.GlobalInit;
import org.superbiz.util.HttpUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.superbiz.dto.PriceDTO.PriceDTOBuilder.createPriceDTO;
import static org.superbiz.model.jooq.Tables.SECURITY;

public class FetchData {
    private static final GlobalInit globalInit = new GlobalInit();
    private static final Logger LOGGER = Logger.getLogger(FetchData.class.getName());

    private NetFetcherYahoo netFetcherYahoo = new NetFetcherYahoo();
    //private SecurityDAO securityDAO = new SecurityDAO();
    private PriceDAO priceDAO = new PriceDAO();

    public static void main(String[] args) throws IOException {
        new FetchData().fetchAll();
    }

    private void fetchAll() throws IOException {
        DefaultAsyncHttpClientConfig.Builder clientBuilder = HttpUtils.getHttpAgentBuilder();

        try (ConnAndDSL dsl = ConnAndDSL.create();
            AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder)) {
            Result<Record> securities = dsl.getDsl().select().from(SECURITY).orderBy(SECURITY.SYMBOL).fetch();
            securities.stream().forEach(security -> {
                String symbol = security.get(SECURITY.SYMBOL);
                String url = netFetcherYahoo.createUrl(symbol);
                Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                        .setUrl(url)
                        .build();

                ListenableFuture<Response> responseFuture = client.executeRequest(getRequest);
                try {
                    Response response = responseFuture.get();
                    LOGGER.info(String.format("%s -> %s (%s)", symbol, response.getStatusCode(), url));
                    ParsingResult parsedResult = netFetcherYahoo.processData(response.getResponseBody());
                    // LOGGER.info(String.format("%s", parsedResult.asJson()));
                    PriceDTO priceDTO = createPriceDTO()
                            .withSymbol(symbol)
                            .withLastUpdated(LocalDateTime.now())
                            .withData(parsedResult.asJson())
                            .build();
                    priceDAO.insertOrUpdate(dsl, priceDTO);
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.log(Level.SEVERE, String.format("Cannot read Trading Tick data for %s", security), e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

}
