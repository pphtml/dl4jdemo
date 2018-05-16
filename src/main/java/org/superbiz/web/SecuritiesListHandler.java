package org.superbiz.web;

import org.superbiz.dao.MarketFinVizDAO;
import org.superbiz.dao.SecurityDAO;
import org.superbiz.dto.MarketFinVizDTO;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SecuritiesListHandler implements Handler {
    @Inject
    Logger logger;

    @Inject
    SecurityDAO securityDAO;

    @Override
    public void handle(Context ctx) throws Exception {
        securityDAO.fetchAll();

//        String text = statuses.stream()
//                .map(finViz -> String.format("%s: successUpdated: %s, errorUpdated: %s, errorMsg: %s, warning: %s",
//                        finViz.getSymbol(), finViz.getLastUpdatedSuccess(), finViz.getLastUpdatedError(),
//                        finViz.getLastError(), finViz.getLastWarning()))
//                .collect(Collectors.joining("\n"));
//        ctx.getResponse().send(text);
    }
}
