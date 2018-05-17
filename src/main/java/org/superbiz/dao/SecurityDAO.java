package org.superbiz.dao;

import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;
import org.superbiz.dto.SecurityDTO;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static org.superbiz.model.jooq.Tables.SECURITY;

public class SecurityDAO {
    @Inject
    ConnAndDSLProvider connAndDSLProvider;

    public List<SecurityDTO> findAll() {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Result<Record> securities = dsl.getDsl().select().from(SECURITY).orderBy(SECURITY.SYMBOL).fetch();

            List<SecurityDTO> result = securities.stream()
                    .map(security ->
                            SecurityDTO.of(security.get(SECURITY.SYMBOL), security.get(SECURITY.NAME)))
                    .collect(Collectors.toList());

            return result;
        }
    }

    public Result<Record> fetchAll() {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            return dsl.getDsl().select().from(SECURITY).orderBy(SECURITY.SYMBOL).fetch();
        }
    }

    public Result<Record> findAllSecuritiesExcept(List<String> symbols, boolean includeIndex) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            if (includeIndex) {
                return dsl.getDsl()
                        .select()
                        .from(SECURITY)
                        .where(SECURITY.SYMBOL.notIn(symbols))
                        .orderBy(SECURITY.SYMBOL)
                        .fetch();
            } else {
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
}
