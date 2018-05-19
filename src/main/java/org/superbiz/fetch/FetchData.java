package org.superbiz.fetch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.dao.*;
import org.superbiz.dto.PriceDTO;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.fetch.model.TickData;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.GlobalInit;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.superbiz.dto.PriceDTO.PriceDTOBuilder.createPriceDTO;
import static org.superbiz.model.jooq.Tables.SECURITY;
import static org.superbiz.util.TimezoneNewYork.fromTimestamp;

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
    Price1mDAO price1mDAO;
    @Inject
    Price5mDAO price5mDAO;
    @Inject
    Price1dDAO price1dDAO;

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new BasicModule());
        FetchData fetchData = injector.getInstance(FetchData.class);
        //fetchData.fetchAllIntervals();
        //fetchData.fetchAll(fetchData.price1mDAO, "1m", 7);
        //fetchData.fetchAll(fetchData.price5mDAO, "5m", 60);
        //fetchData.fetchAll(fetchData.price1dDAO, "1d", 3650);
        //PriceDTO price = fetchData.readFromDB("AMZN");

        Optional<PriceDTO> price = fetchData.price1dDAO.read("ORCL");
        price.get().getData().forEach(tick -> {
            LocalDateTime dateTime = fromTimestamp(tick.getTimestamp());
            System.out.println(String.format("%s %s", dateTime, tick));
        });

//        PriceDTO fixed = fetchData.price1dDAO.fixMultipleDayRecords(price.get());
//        fetchData.price1dDAO.insertOrUpdate(fixed);
    }

    public void fetchAllIntervals() throws IOException {
        fetchAll(price1mDAO, "1m", 7);
        fetchAll(price5mDAO, "5m", 60);
        fetchAll(price1dDAO, "1d", 3650);
    }

//    private Optional<PriceDTO> readFromDB(String symbol) {
//        return price5mDAO.read(symbol);
//    }

    private void fetchAll(PriceAbstractDAO priceDAO, String interval, Integer daysBack) throws IOException {
        LOGGER.info(String.format("Starting for interval %s", interval));
        try (AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder)) {

            List<String> knownSecurities = priceDAO.findFreshDataSymbols(LocalDateTime.now().minusHours(6));
            Result<Record> securities = securityDAO.findAllSecuritiesExcept(knownSecurities, true);
            securities.stream().forEach(security -> {
                String symbol = security.get(SECURITY.SYMBOL);
                String url = netFetcherYahoo.createUrl(symbol, interval, daysBack);
                Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                        .setUrl(url)
                        .build();

                ListenableFuture<Response> responseFuture = client.executeRequest(getRequest);
                try {
                    Response response = responseFuture.get();
                    LOGGER.info(String.format("%s -> %s (%s)", symbol, response.getStatusCode(), url));
                    ParsingResult parsedResult = netFetcherYahoo.processData(response.getResponseBody());
                    PriceDTO newPriceDTO = createPriceDTO()
                            .withSymbol(symbol)
                            .withLastUpdated(LocalDateTime.now())
                            .withData(parsedResult.getTickData())
                            .withLastUpdatedError(null)
                            .withLastError(null)
                            .build();
                    final Optional<PriceDTO> oldOptionalPriceDTO = priceDAO.read(symbol);
                    // Optional<PriceDTO> oldOptionalPriceDTO = priceDAO.read(symbol);
                    // oldOptionalPriceDTO = priceDAO.fixEmptyCAArrays(oldOptionalPriceDTO);

                    PriceDTO mergedPriceDTO = mergePriceDTOs(oldOptionalPriceDTO, newPriceDTO);
                    mergedPriceDTO = priceDAO.fixMultipleDayRecords(mergedPriceDTO);
                    priceDAO.insertOrUpdate(mergedPriceDTO);
                } catch (InterruptedException | ExecutionException | DataProcessingException | ParsingException e) {
                    LOGGER.log(Level.WARNING, String.format("Cannot read Trading Tick data for %s, reason: %s",
                            symbol, e.getMessage()), e);
                    String errorMessageForDB = String.format("%s: %s%s", e.getClass(), e.getMessage(), url);
                    PriceDTO priceDTO = createPriceDTO()
                            .withSymbol(symbol)
                            .withLastUpdatedError(LocalDateTime.now())
                            .withLastError(errorMessageForDB)
                            .build();
                    priceDAO.insertOrUpdateError(priceDTO);
                }
            });
        }
        LOGGER.info("Finished");
    }

    PriceDTO mergePriceDTOs(Optional<PriceDTO> oldOptionalPriceDTO, PriceDTO newPriceDTO) {
        if (oldOptionalPriceDTO.isPresent() && oldOptionalPriceDTO.get().getData() != null) {

            LinkedHashMap<Long, TickData> linkedMap = pricesLinkedMapFromList(oldOptionalPriceDTO.get().getData());

            LinkedHashMap<Long, TickData> newLinkedMap = pricesLinkedMapFromList(newPriceDTO.getData());

            linkedMap.putAll(newLinkedMap);

            List mergedList = new ArrayList(linkedMap.values());

            PriceDTO priceDTO = createPriceDTO()
                    .withSymbol(newPriceDTO.getSymbol())
                    .withLastUpdated(newPriceDTO.getLastUpdated())
                    .withData(mergedList)
                    .withLastUpdatedError(newPriceDTO.getLastUpdatedError())
                    .withLastError(newPriceDTO.getLastError())
                    .build();
            return priceDTO;
        } else {
            return newPriceDTO;
        }
    }

    public static LinkedHashMap<Long, TickData> pricesLinkedMapFromList(List<TickData> tickData) {
        return tickData.stream()
                .collect(Collectors.toMap(entry -> entry.getTimestamp(),
                        entry -> entry,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }
}
