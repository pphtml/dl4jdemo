package org.superbiz.web;

import org.superbiz.dao.Price1mDAO;
import org.superbiz.dto.PriceDTO;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PricesHandler implements Handler {
    @Inject
    Logger logger;

    @Inject
    Price1mDAO price1mDAO;

    @Override
    public void handle(Context ctx) throws Exception {
        List<PriceDTO> statuses = price1mDAO.readStatuses();
        String text = statuses.stream()
                .map(price -> String.format("%s: successUpdated: %s, errorUpdated: %s, errorMsg: %s",
                        price.getSymbol(), price.getLastUpdated(), price.getLastUpdatedError(),
                        price.getLastError()))
                .collect(Collectors.joining("\n"));
        ctx.getResponse().send(text);
    }
}