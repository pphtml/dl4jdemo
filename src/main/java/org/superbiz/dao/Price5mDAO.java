package org.superbiz.dao;

import org.jooq.Record;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;
import org.superbiz.dto.PriceDTO;
import org.superbiz.util.DateConverter;
import org.superbiz.util.TickDataConverter;

import javax.inject.Inject;

import static org.superbiz.model.jooq.Tables.PRICE_5M;

public class Price5mDAO {
    @Inject
    ConnAndDSLProvider connAndDSLProvider;

//    public void insertOrUpdate(ConnAndDSL dsl, PriceDTO price) {
//        Record record = dsl.getDsl().select().from(PRICE_5M).where(PRICE_5M.SYMBOL.eq(price.getSymbol())).fetchOne();
//        if (record == null) {
//            dsl.getDsl().insertInto(PRICE_5M,
//                    PRICE_5M.SYMBOL,
//                    PRICE_5M.DATA,
//                    PRICE_5M.LAST_ERROR,
//                    PRICE_5M.LAST_UPDATED,
//                    PRICE_5M.LAST_UPDATED_ERROR)
//                    .values(price.getSymbol(),
//                            TickDataConverter.tickDataAsJson(price.getData()),
//                            price.getLastError(),
//                            DateConverter.from(price.getLastUpdated()),
//                            DateConverter.from(price.getLastUpdatedError()))
//                    .execute();
//
//        } else {
//            dsl.getDsl().update(PRICE_5M)
//                    .set(PRICE_5M.LAST_UPDATED, DateConverter.from(price.getLastUpdated()))
//                    .set(PRICE_5M.DATA, TickDataConverter.tickDataAsJson(price.getData()))
//                    .set(PRICE_5M.LAST_ERROR, price.getLastError())
//                    .set(PRICE_5M.LAST_UPDATED_ERROR, DateConverter.from(price.getLastUpdatedError()))
//                    .where(MARKETWATCH.SYMBOL.eq(price.getSymbol()))
//                    .execute();
//        }
//    }
//
//    public void insertOrUpdateError(ConnAndDSL dsl, PriceDTO price) {
//        Record record = dsl.getDsl().select().from(PRICE_5M).where(PRICE_5M.SYMBOL.eq(price.getSymbol())).fetchOne();
//        if (record == null) {
//            dsl.getDsl().insertInto(PRICE_5M,
//                    PRICE_5M.SYMBOL,
//                    PRICE_5M.LAST_ERROR,
//                    PRICE_5M.LAST_UPDATED_ERROR)
//                    .values(price.getSymbol(),
//                            price.getLastError(),
//                            DateConverter.from(price.getLastUpdatedError()))
//                    .execute();
//
//        } else {
//            dsl.getDsl().update(PRICE_5M)
//                    .set(PRICE_5M.LAST_ERROR, price.getLastError())
//                    .set(PRICE_5M.LAST_UPDATED_ERROR, DateConverter.from(price.getLastUpdatedError()))
//                    .where(MARKETWATCH.SYMBOL.eq(price.getSymbol()))
//                    .execute();
//        }
//    }

    public void insertOrUpdate(PriceDTO price) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl().select().from(PRICE_5M).where(PRICE_5M.SYMBOL.eq(price.getSymbol())).fetchOne();
            if (record == null) {
                dsl.getDsl().insertInto(PRICE_5M,
                        PRICE_5M.SYMBOL,
                        PRICE_5M.DATA,
                        PRICE_5M.LAST_ERROR,
                        PRICE_5M.LAST_UPDATED,
                        PRICE_5M.LAST_UPDATED_ERROR)
                        .values(price.getSymbol(),
                                TickDataConverter.tickDataAsJson(price.getData()),
                                price.getLastError(),
                                DateConverter.fromLocalDateTime(price.getLastUpdated()),
                                DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .execute();

            } else {
                dsl.getDsl().update(PRICE_5M)
                        .set(PRICE_5M.LAST_UPDATED, DateConverter.fromLocalDateTime(price.getLastUpdated()))
                        .set(PRICE_5M.DATA, TickDataConverter.tickDataAsJson(price.getData()))
                        .set(PRICE_5M.LAST_ERROR, price.getLastError())
                        .set(PRICE_5M.LAST_UPDATED_ERROR, DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .where(PRICE_5M.SYMBOL.eq(price.getSymbol()))
                        .execute();
            }
        }
    }

    public void insertOrUpdateError(PriceDTO price) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl()
                    .select()
                    .from(PRICE_5M)
                    .where(PRICE_5M.SYMBOL.eq(price.getSymbol()))
                    .fetchOne();
            if (record == null) {
                dsl.getDsl().insertInto(PRICE_5M,
                        PRICE_5M.SYMBOL,
                        PRICE_5M.LAST_ERROR,
                        PRICE_5M.LAST_UPDATED_ERROR)
                        .values(price.getSymbol(),
                                price.getLastError(),
                                DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .execute();

            } else {
                dsl.getDsl().update(PRICE_5M)
                        .set(PRICE_5M.LAST_ERROR, price.getLastError())
                        .set(PRICE_5M.LAST_UPDATED_ERROR, DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .where(PRICE_5M.SYMBOL.eq(price.getSymbol()))
                        .execute();
            }
        }
    }

    public void dummy() {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {

        }
    }

    public PriceDTO read(String symbol) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl()
                    .select()
                    .from(PRICE_5M)
                    .where(PRICE_5M.SYMBOL.eq(symbol))
                    .fetchOne();
            return convertRecordToPriceDTO(record);
        }
    }

    private PriceDTO convertRecordToPriceDTO(Record record) {
        if (record != null) {
            return PriceDTO.PriceDTOBuilder.createPriceDTO()
                    .withSymbol(record.get(PRICE_5M.SYMBOL))
                    .withLastUpdated(DateConverter.toLocalDateTime(record.get(PRICE_5M.LAST_UPDATED)))
                    .withData(TickDataConverter.jsonAsTickData(record.get(PRICE_5M.DATA)))
                    .withLastError(record.get(PRICE_5M.LAST_ERROR))
                    .withLastUpdatedError(DateConverter.toLocalDateTime(record.get(PRICE_5M.LAST_UPDATED_ERROR)))
                    .build();
        } else {
            return null;
        }
    }
}
