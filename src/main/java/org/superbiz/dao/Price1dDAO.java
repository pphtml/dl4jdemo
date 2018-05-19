package org.superbiz.dao;

import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.superbiz.dto.PriceDTO;
import org.superbiz.fetch.model.TickData;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.superbiz.model.jooq.Tables.PRICE_1D;
import static org.superbiz.util.TimezoneNewYork.fromTimestamp;

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

    @Override
    public PriceDTO fixMultipleDayRecords(PriceDTO price) {
        Map<LocalDate, List<TickData>> grouped = price.getData().stream()
                .collect(Collectors.groupingBy(tick -> {
                    LocalDateTime dateTime = fromTimestamp(tick.getTimestamp());
                    return dateTime.toLocalDate();
                }));
        List<TickData> result = grouped.entrySet().stream()
                .map(entry -> entry.getValue().stream()
                        .sorted(Comparator.comparing(TickData::getTimestamp))
                        .findFirst().get())
                .sorted(Comparator.comparing(TickData::getTimestamp))
                .collect(Collectors.toList());

        price.setData(result);

        return price;
    }
}
