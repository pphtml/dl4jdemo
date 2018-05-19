package org.superbiz.dao;

import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.superbiz.dto.PriceDTO;

import java.sql.Timestamp;

import static org.superbiz.model.jooq.Tables.PRICE_5M;

public class Price5mDAO extends PriceAbstractDAO {
    @Override
    public TableImpl getTableName() {
        return PRICE_5M;
    }

    @Override
    public TableField getTableFieldSymbol() {
        return PRICE_5M.SYMBOL;
    }

    @Override
    public TableField<?, Timestamp> getTableFieldLastUpdated() {
        return PRICE_5M.LAST_UPDATED;
    }

    @Override
    public TableField<?, byte[]> getTableFieldData() {
        return PRICE_5M.DATA;
    }

    @Override
    public TableField<?, String> getTableFieldLastError() {
        return PRICE_5M.LAST_ERROR;
    }

    @Override
    public TableField<?, Timestamp> getTableFieldLastUpdatedError() {
        return PRICE_5M.LAST_UPDATED_ERROR;
    }

    @Override
    public PriceDTO fixMultipleDayRecords(PriceDTO price) {
        return price; // ignored form 5m
    }
}
