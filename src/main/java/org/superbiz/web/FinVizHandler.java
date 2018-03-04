package org.superbiz.web;

import org.superbiz.dao.MarketFinVizDAO;
import org.superbiz.dto.MarketFinVizDTO;
import org.superbiz.fetch.FetchFinViz;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FinVizHandler implements Handler {
    @Inject
    Logger logger;

//    @Inject
//    FetchFinViz fetchFinViz;

    @Inject
    MarketFinVizDAO marketFinVizDAO;

    @Override
    public void handle(Context ctx) throws Exception {
        //if (ctx.getRequest().getPath().startsWith("foo/")))
        //fetchFinViz.fetchAll();
        List<MarketFinVizDTO> statuses = marketFinVizDAO.readStatuses();
        String text = statuses.stream()
                .map(finViz -> String.format("%s: successUpdated: %s, errorUpdated: %s, errorMsg: %s, warning: %s",
                        finViz.getSymbol(), finViz.getLastUpdatedSuccess(), finViz.getLastUpdatedError(),
                        finViz.getLastError(), finViz.getLastWarning()))
                .collect(Collectors.joining("\n"));
        ctx.getResponse().send(text);
    }
}
