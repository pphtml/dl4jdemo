package org.superbiz.dao;

import org.jooq.Record;
import org.jooq.Result;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.dto.PriceDTO;
import org.superbiz.util.DateConverter;

import static org.superbiz.model.jooq.Tables.MARKETWATCH;
import static org.superbiz.model.jooq.Tables.PRICE;

public class PriceDAO {
    public void insertOrUpdate(ConnAndDSL dsl, PriceDTO price) {
        Record record = dsl.getDsl().select().from(PRICE).where(PRICE.SYMBOL.eq(price.getSymbol())).fetchOne();
        if (record == null) {
            dsl.getDsl().insertInto(PRICE,
                    PRICE.SYMBOL,
                    PRICE.DATA,
                    PRICE.LAST_ERROR,
                    PRICE.LAST_UPDATED)
                    .values(price.getSymbol(),
                            price.getData(),
                            price.getLastError(),
                            DateConverter.from(price.getLastUpdated()))
                    .execute();

        } else {
            throw new UnsupportedOperationException();

        }
    }
}
