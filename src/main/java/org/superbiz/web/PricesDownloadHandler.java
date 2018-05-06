package org.superbiz.web;

import org.superbiz.dao.Price1mDAO;
import org.superbiz.dto.PriceDTO;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import java.util.Optional;
import java.util.logging.Logger;

public class PricesDownloadHandler implements Handler {
    @Inject
    Logger logger;

    @Inject
    Price1mDAO price1mDAO;

    @Override
    public void handle(Context ctx) throws Exception {
        String symbol = ctx.getRequest().getQueryParams().get("s");
        Optional<PriceDTO> data = price1mDAO.read(symbol);
        if (data.isPresent()) {
            ctx.getResponse().send(data.get().asJson());
        } else {
            // TODO 404
        }
    }
}
