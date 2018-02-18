package org.superbiz.fetch;

import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.util.GlobalInit;

import static org.superbiz.model.jooq.Tables.SECURITY;

public class FetchData {
    private static final GlobalInit globalInit = new GlobalInit();

    public static void main(String[] args) {
        try (ConnAndDSL dsl = ConnAndDSL.create()) {
            Result<Record> securities = dsl.getDsl().select().from(SECURITY).orderBy(SECURITY.SYMBOL).fetch();
            securities.stream().limit(10).forEach(security -> {
                System.out.println(security);
            });
        }
    }
}
