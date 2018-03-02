package org.superbiz.dao;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;
import org.superbiz.dto.MarketFinVizDTO;
import org.superbiz.fetch.FetchFinViz;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.superbiz.model.jooq.Tables.MARKET_FIN_VIZ;
import static org.superbiz.util.DateConverter.fromLocalDateTime;
import static org.superbiz.util.DateConverter.toLocalDateTime;

public class MarketFinVizDAO {
    @Inject
    ConnAndDSLProvider connAndDSLProvider;

    public void insertOrUpdate(MarketFinVizDTO finViz) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl().select().from(MARKET_FIN_VIZ).where(MARKET_FIN_VIZ.SYMBOL.eq(finViz.getSymbol())).fetchOne();
            if (record == null) {
                dsl.getDsl().insertInto(MARKET_FIN_VIZ,
                        MARKET_FIN_VIZ.SYMBOL,
                        MARKET_FIN_VIZ.PARAMETERS,
                        MARKET_FIN_VIZ.ANALYSTS,
                        MARKET_FIN_VIZ.INSIDERS,
                        MARKET_FIN_VIZ.LAST_UPDATED_SUCCESS,
                        MARKET_FIN_VIZ.LAST_UPDATED_ERROR,
                        MARKET_FIN_VIZ.LAST_ERROR,
                        MARKET_FIN_VIZ.LAST_WARNING)
                        .values(finViz.getSymbol(),
                                finViz.getParameters(),
                                finViz.getAnalysts(),
                                finViz.getInsiders(),
                                fromLocalDateTime(finViz.getLastUpdatedSuccess()),
                                fromLocalDateTime(finViz.getLastUpdatedError()),
                                finViz.getLastError(),
                                finViz.getLastWarning())
                        .execute();

            } else {
                dsl.getDsl().update(MARKET_FIN_VIZ)
                        .set(MARKET_FIN_VIZ.PARAMETERS, finViz.getParameters())
                        .set(MARKET_FIN_VIZ.ANALYSTS, finViz.getAnalysts())
                        .set(MARKET_FIN_VIZ.INSIDERS, finViz.getInsiders())
                        .set(MARKET_FIN_VIZ.LAST_UPDATED_SUCCESS, fromLocalDateTime(finViz.getLastUpdatedSuccess()))
                        .set(MARKET_FIN_VIZ.LAST_UPDATED_ERROR, fromLocalDateTime(finViz.getLastUpdatedError()))
                        .set(MARKET_FIN_VIZ.LAST_ERROR, finViz.getLastError())
                        .set(MARKET_FIN_VIZ.LAST_WARNING, finViz.getLastWarning())
                        .where(MARKET_FIN_VIZ.SYMBOL.eq(finViz.getSymbol()))
                        .execute();
            }
        }
    }

    public void insertOrUpdateError(MarketFinVizDTO finViz) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl().select().from(MARKET_FIN_VIZ).where(MARKET_FIN_VIZ.SYMBOL.eq(finViz.getSymbol())).fetchOne();
            if (record == null) {
                dsl.getDsl().insertInto(MARKET_FIN_VIZ,
                        MARKET_FIN_VIZ.SYMBOL,
                        MARKET_FIN_VIZ.LAST_UPDATED_ERROR,
                        MARKET_FIN_VIZ.LAST_ERROR,
                        MARKET_FIN_VIZ.LAST_WARNING)
                        .values(finViz.getSymbol(),
                                fromLocalDateTime(finViz.getLastUpdatedError()),
                                finViz.getLastError(),
                                finViz.getLastWarning())
                        .execute();

            } else {
                dsl.getDsl().update(MARKET_FIN_VIZ)
                        .set(MARKET_FIN_VIZ.LAST_UPDATED_ERROR, fromLocalDateTime(finViz.getLastUpdatedError()))
                        .set(MARKET_FIN_VIZ.LAST_ERROR, finViz.getLastError())
                        .set(MARKET_FIN_VIZ.LAST_WARNING, finViz.getLastWarning())
                        .where(MARKET_FIN_VIZ.SYMBOL.eq(finViz.getSymbol()))
                        .execute();
            }
        }
    }

    private MarketFinVizDTO convertRecordToMarketFinVizDTO(Record record) {
//        if (record != null) {
            return MarketFinVizDTO.MarketFinVizDTOBuilder.createMarketFinVizDTO()
                    .withSymbol(record.get(MARKET_FIN_VIZ.SYMBOL))
                    .withParameters(record.get(MARKET_FIN_VIZ.PARAMETERS))
                    .withAnalysts(record.get(MARKET_FIN_VIZ.ANALYSTS))
                    .withInsiders(record.get(MARKET_FIN_VIZ.INSIDERS))
                    .withLastUpdatedSuccess(toLocalDateTime(record.get(MARKET_FIN_VIZ.LAST_UPDATED_SUCCESS)))
                    .withLastUpdatedError(toLocalDateTime(record.get(MARKET_FIN_VIZ.LAST_UPDATED_ERROR)))
                    .withLastError(record.get(MARKET_FIN_VIZ.LAST_ERROR))
                    .withLastWarning(record.get(MARKET_FIN_VIZ.LAST_WARNING))
                    .build();
//        } else {
//            return null;
//        }
    }

    public List<String> findFreshDataSymbols(LocalDateTime dateTime) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Result<Record1<String>> symbols = dsl.getDsl()
                    .select(MARKET_FIN_VIZ.SYMBOL)
                    .from(MARKET_FIN_VIZ)
                    .where(MARKET_FIN_VIZ.LAST_UPDATED_SUCCESS.gt(fromLocalDateTime(dateTime)))
                    .fetch();
            return symbols.stream().map(r -> r.value1()).collect(Collectors.toList());
        }
    }

    public Optional<MarketFinVizDTO> findBySymbol(String symbol) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl()
                    .select()
                    .from(MARKET_FIN_VIZ)
                    .where(MARKET_FIN_VIZ.SYMBOL.eq(symbol))
                    .fetchOne();
            return record != null ?
                    Optional.of(convertRecordToMarketFinVizDTO(record)) :
                    Optional.empty();
        }
    }
}
