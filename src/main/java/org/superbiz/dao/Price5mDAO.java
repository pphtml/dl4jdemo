package org.superbiz.dao;

import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;
import org.superbiz.dto.PriceDTO;
import org.superbiz.util.DateConverter;
import org.superbiz.util.TickDataConverter;

import javax.inject.Inject;

import java.sql.Timestamp;
import java.util.Optional;

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
}
