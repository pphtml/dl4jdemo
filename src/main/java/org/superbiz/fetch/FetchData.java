package org.superbiz.fetch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.dao.Price5mDAO;
import org.superbiz.dao.SecurityDAO;
import org.superbiz.dto.PriceDTO;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.GlobalInit;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.superbiz.dto.PriceDTO.PriceDTOBuilder.createPriceDTO;
import static org.superbiz.model.jooq.Tables.SECURITY;

public class FetchData {
    static { GlobalInit.init(); }

    @Inject
    Logger LOGGER;

    @Inject
    NetFetcherYahoo netFetcherYahoo = new NetFetcherYahoo();

    @Inject
    DefaultAsyncHttpClientConfig.Builder clientBuilder;

    @Inject
    SecurityDAO securityDAO;

    @Inject
    Price5mDAO price5mDAO;

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new BasicModule());
        FetchData fetchData = injector.getInstance(FetchData.class);
        fetchData.fetchAll();
        //PriceDTO price = fetchData.readFromDB("AMZN");
    }

    private PriceDTO readFromDB(String symbol) {
        return price5mDAO.read(symbol);
    }

    private void fetchAll() throws IOException {
        LOGGER.info("Starting");
        try (AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder)) {
            Result<Record> securities = securityDAO.fetchAll();
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
                    PriceDTO priceDTO = createPriceDTO()
                            .withSymbol(symbol)
                            .withLastUpdated(LocalDateTime.now())
                            .withData(parsedResult.getTickData())
                            .withLastUpdatedError(null)
                            .withLastError(null)
                            .build();
                    price5mDAO.insertOrUpdate(priceDTO);
                } catch (InterruptedException | ExecutionException | DataProcessingException e) {
                    LOGGER.log(Level.WARNING, String.format("Cannot read Trading Tick data for %s, reason: %s",
                            symbol, e.getMessage()), e);
                    String errorMessageForDB = String.format("%s: %s", e.getClass(), e.getMessage());
                    PriceDTO priceDTO = createPriceDTO()
                            .withSymbol(symbol)
                            .withLastUpdatedError(LocalDateTime.now())
                            .withLastError(errorMessageForDB)
                            .build();
                    price5mDAO.insertOrUpdateError(priceDTO);
                }
            });
        }
        LOGGER.info("Finished");
    }
}
