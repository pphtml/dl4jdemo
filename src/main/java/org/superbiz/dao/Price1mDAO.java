package org.superbiz.dao;

import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.superbiz.dto.PriceDTO;

import java.sql.Timestamp;

import static org.superbiz.model.jooq.Tables.PRICE_1M;

public class Price1mDAO extends PriceAbstractDAO {

    @Override
    public TableImpl getTableName() {
        return PRICE_1M;
    }

    @Override
    public TableField getTableFieldSymbol() {
        return PRICE_1M.SYMBOL;
    }

    @Override
    public TableField<?, Timestamp> getTableFieldLastUpdated() {
        return PRICE_1M.LAST_UPDATED;
    }

    @Override
    public TableField<?, byte[]> getTableFieldData() {
        return PRICE_1M.DATA;
    }

    @Override
    public TableField<?, String> getTableFieldLastError() {
        return PRICE_1M.LAST_ERROR;
    }

    @Override
    public TableField<?, Timestamp> getTableFieldLastUpdatedError() {
        return PRICE_1M.LAST_UPDATED_ERROR;
    }

    @Override
    public PriceDTO fixMultipleDayRecords(PriceDTO price) {
        return price; // ignored form 1m
    }
}
