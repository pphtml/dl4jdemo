package org.superbiz.dao;

import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;

import javax.inject.Inject;

import java.util.List;

import static org.superbiz.model.jooq.Tables.SECURITY;

public class SecurityDAO {
    @Inject
    ConnAndDSLProvider connAndDSLProvider;

    public void findAll() {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Result<Record> securities = dsl.getDsl().select().from(SECURITY).orderBy(SECURITY.SYMBOL).fetch();
            System.out.println(securities);
        }
    }

    public Result<Record> fetchAll() {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            return dsl.getDsl().select().from(SECURITY).orderBy(SECURITY.SYMBOL).fetch();
        }
    }

    public Result<Record> findAllSecuritiesExcept(List<String> symbols) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            return dsl.getDsl()
                    .select()
                    .from(SECURITY)
                    .where(SECURITY.SYMBOL.notIn(symbols))
                      .and(SECURITY.INDEX.eq(false))
                    .orderBy(SECURITY.SYMBOL)
                    .fetch();
        }
    }

}
