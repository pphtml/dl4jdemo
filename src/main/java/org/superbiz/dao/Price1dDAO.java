package org.superbiz.dao;

import org.jooq.TableField;
import org.jooq.impl.TableImpl;

import java.sql.Timestamp;

import static org.superbiz.model.jooq.Tables.PRICE_1D;

public class Price1dDAO extends PriceAbstractDAO {

    @Override
    public TableImpl getTableName() {
        return PRICE_1D;
    }

    @Override
    public TableField getTableFieldSymbol() {
        return PRICE_1D.SYMBOL;
    }

    @Override
    public TableField<?, Timestamp> getTableFieldLastUpdated() {
        return PRICE_1D.LAST_UPDATED;
    }

    @Override
    public TableField<?, byte[]> getTableFieldData() {
        return PRICE_1D.DATA;
    }

    @Override
    public TableField<?, String> getTableFieldLastError() {
        return PRICE_1D.LAST_ERROR;
    }

    @Override
    public TableField<?, Timestamp> getTableFieldLastUpdatedError() {
        return PRICE_1D.LAST_UPDATED_ERROR;
    }
}
