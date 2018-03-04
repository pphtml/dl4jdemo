package org.superbiz.web;

import org.superbiz.fetch.FetchFinViz;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;

public class DemoHandler implements Handler {
    @Inject
    FetchFinViz fetchFinViz;

    @Override
    public void handle(Context ctx) throws Exception {
        fetchFinViz.fetchAll();
        ctx.getResponse().send("bar");
    }
}
