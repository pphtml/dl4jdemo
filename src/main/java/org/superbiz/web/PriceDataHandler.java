package org.superbiz.web;

import org.superbiz.dao.Price1mDAO;
import org.superbiz.dao.PriceMixDAO;
import org.superbiz.dto.PriceDTO;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import java.util.Optional;
import java.util.logging.Logger;

public class PriceDataHandler implements Handler {
    @Inject
    Logger logger;

    @Inject
    //Price1mDAO price1mDAO;
    PriceMixDAO priceMixDAO;

    @Override
    public void handle(Context ctx) throws Exception {
        final String symbol = ctx.getPathTokens().get("symbol").toUpperCase();
        final String intervalString = ctx.getPathTokens().get("interval");
        final Optional<PriceMixDAO.Interval> interval = PriceMixDAO.Interval.of(intervalString);

        if (!interval.isPresent()) {
            ctx.getResponse().status(404);
            ctx.getResponse().send(String.format("Interval %s not found", intervalString));
        } else {
            Optional<PriceDTO> data = priceMixDAO.read(symbol, interval.get());
            if (data.isPresent()) {
                ctx.getResponse().send(data.get().asJson());
            } else {
                ctx.getResponse().status(404);
                ctx.getResponse().send(String.format("Data for symbol %s for interval %s not found",
                        symbol, intervalString));
            }
        }
    }
}
