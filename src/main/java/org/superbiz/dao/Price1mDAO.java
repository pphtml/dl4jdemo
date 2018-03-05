package org.superbiz.dao;

import org.jooq.Record;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;
import org.superbiz.dto.PriceDTO;
import org.superbiz.util.DateConverter;
import org.superbiz.util.TickDataConverter;

import javax.inject.Inject;
import java.util.Optional;

import static org.superbiz.model.jooq.Tables.PRICE_1M;

public class Price1mDAO extends PriceAbstractDAO {
    @Inject
    ConnAndDSLProvider connAndDSLProvider;

    public void insertOrUpdate(PriceDTO price) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl().select().from(PRICE_1M).where(PRICE_1M.SYMBOL.eq(price.getSymbol())).fetchOne();
            if (record == null) {
                dsl.getDsl().insertInto(PRICE_1M,
                        PRICE_1M.SYMBOL,
                        PRICE_1M.DATA,
                        PRICE_1M.LAST_ERROR,
                        PRICE_1M.LAST_UPDATED,
                        PRICE_1M.LAST_UPDATED_ERROR)
                        .values(price.getSymbol(),
                                TickDataConverter.tickDataAsJson(price.getData()),
                                price.getLastError(),
                                DateConverter.fromLocalDateTime(price.getLastUpdated()),
                                DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .execute();

            } else {
                dsl.getDsl().update(PRICE_1M)
                        .set(PRICE_1M.LAST_UPDATED, DateConverter.fromLocalDateTime(price.getLastUpdated()))
                        .set(PRICE_1M.DATA, TickDataConverter.tickDataAsJson(price.getData()))
                        .set(PRICE_1M.LAST_ERROR, price.getLastError())
                        .set(PRICE_1M.LAST_UPDATED_ERROR, DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .where(PRICE_1M.SYMBOL.eq(price.getSymbol()))
                        .execute();
            }
        }
    }

    public void insertOrUpdateError(PriceDTO price) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl()
                    .select()
                    .from(PRICE_1M)
                    .where(PRICE_1M.SYMBOL.eq(price.getSymbol()))
                    .fetchOne();
            if (record == null) {
                dsl.getDsl().insertInto(PRICE_1M,
                        PRICE_1M.SYMBOL,
                        PRICE_1M.LAST_ERROR,
                        PRICE_1M.LAST_UPDATED_ERROR)
                        .values(price.getSymbol(),
                                price.getLastError(),
                                DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .execute();

            } else {
                dsl.getDsl().update(PRICE_1M)
                        .set(PRICE_1M.LAST_ERROR, price.getLastError())
                        .set(PRICE_1M.LAST_UPDATED_ERROR, DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .where(PRICE_1M.SYMBOL.eq(price.getSymbol()))
                        .execute();
            }
        }
    }

    public void dummy() {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {

        }
    }

    public Optional<PriceDTO> read(String symbol) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl()
                    .select()
                    .from(PRICE_1M)
                    .where(PRICE_1M.SYMBOL.eq(symbol))
                    .fetchOne();
            return Optional.ofNullable(convertRecordToPriceDTO(record));
        }
    }

    private PriceDTO convertRecordToPriceDTO(Record record) {
        if (record != null) {
            return PriceDTO.PriceDTOBuilder.createPriceDTO()
                    .withSymbol(record.get(PRICE_1M.SYMBOL))
                    .withLastUpdated(DateConverter.toLocalDateTime(record.get(PRICE_1M.LAST_UPDATED)))
                    .withData(TickDataConverter.jsonAsTickData(record.get(PRICE_1M.DATA)))
                    .withLastError(record.get(PRICE_1M.LAST_ERROR))
                    .withLastUpdatedError(DateConverter.toLocalDateTime(record.get(PRICE_1M.LAST_UPDATED_ERROR)))
                    .build();
        } else {
            return null;
        }
    }
}
