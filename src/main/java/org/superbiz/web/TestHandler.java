package org.superbiz.web;

import ratpack.handling.Context;
import ratpack.handling.Handler;

public class TestHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.render("hello ${context}");
    }
}
