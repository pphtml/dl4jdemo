package org.superbiz.dao;

import org.jooq.*;
import org.jooq.impl.TableImpl;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;
import org.superbiz.dto.PriceDTO;
import org.superbiz.fetch.model.TickData;
import org.superbiz.util.DateConverter;
import org.superbiz.util.TickDataConverter;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.superbiz.util.DateConverter.fromLocalDateTime;

public abstract class PriceAbstractDAO {
    @Inject
    ConnAndDSLProvider connAndDSLProvider;

    public abstract TableImpl getTableName();
    //             final TableField<Price_5mRecord, String> symbol = PRICE_5M.SYMBOL;
    public abstract TableField<?, String> getTableFieldSymbol();
    //final TableField<Price_1mRecord, Timestamp> last_updated = PRICE_1M.LAST_UPDATED;
    public abstract TableField<?, Timestamp> getTableFieldLastUpdated();
    //public static final TableField<Price_1mRecord, byte[]> PRICE_1_M_RECORD_TABLE_FIELD = PRICE_1M.DATA;
    public abstract TableField<?, byte[]> getTableFieldData();
    //public static final TableField<Price_1mRecord, String> PRICE_1_M_RECORD_STRING_TABLE_FIELD = PRICE_1M.LAST_ERROR;
    public abstract TableField<?, String> getTableFieldLastError();
    //public static final TableField<Price_1mRecord, Timestamp> PRICE_1_M_RECORD_TIMESTAMP_TABLE_FIELD = PRICE_1M.LAST_UPDATED_ERROR;
    public abstract TableField<?, Timestamp> getTableFieldLastUpdatedError();

    public Optional<PriceDTO> read(String symbol) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl()
                    .select()
                    .from(getTableName())
                    .where(getTableFieldSymbol().eq(symbol))
                    .fetchOne();
            return Optional.ofNullable(convertRecordToPriceDTO(record));
        }
    }

    public void insertOrUpdate(PriceDTO price) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl().select().from(getTableName()).where(getTableFieldSymbol().eq(price.getSymbol())).fetchOne();
            if (record == null) {
                dsl.getDsl().insertInto(getTableName(),
                        getTableFieldSymbol(),
                        getTableFieldData(),
                        getTableFieldLastError(),
                        getTableFieldLastUpdated(),
                        getTableFieldLastUpdatedError())
                        .values(price.getSymbol(),
                                TickDataConverter.tickDataAsBytea(price.getData()),
                                price.getLastError(),
                                DateConverter.fromLocalDateTime(price.getLastUpdated()),
                                DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .execute();

            } else {
                dsl.getDsl().update(getTableName())
                        .set(getTableFieldLastUpdated(), DateConverter.fromLocalDateTime(price.getLastUpdated()))
                        .set(getTableFieldData(), TickDataConverter.tickDataAsBytea(price.getData()))
                        .set(getTableFieldLastError(), price.getLastError())
                        .set(getTableFieldLastUpdatedError(), DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .where(getTableFieldSymbol().eq(price.getSymbol()))
                        .execute();
            }
        }
    }

    public void insertOrUpdateError(PriceDTO price) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record record = dsl.getDsl()
                    .select()
                    .from(getTableName())
                    .where(getTableFieldSymbol().eq(price.getSymbol()))
                    .fetchOne();
            if (record == null) {
                dsl.getDsl().insertInto(getTableName(),
                        getTableFieldSymbol(),
                        getTableFieldLastError(),
                        getTableFieldLastUpdatedError())
                        .values(price.getSymbol(),
                                price.getLastError(),
                                DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .execute();

            } else {
                dsl.getDsl().update(getTableName())
                        .set(getTableFieldLastError(), price.getLastError())
                        .set(getTableFieldLastUpdatedError(), DateConverter.fromLocalDateTime(price.getLastUpdatedError()))
                        .where(getTableFieldSymbol().eq(price.getSymbol()))
                        .execute();
            }
        }
    }


    private PriceDTO convertRecordToPriceDTO(Record record) {
        if (record != null) {
            return PriceDTO.PriceDTOBuilder.createPriceDTO()
                    .withSymbol(record.get(getTableFieldSymbol()))
                    .withLastUpdated(DateConverter.toLocalDateTime(record.get(getTableFieldLastUpdated())))
                    .withData(TickDataConverter.byteaAsTickData(record.get(getTableFieldData())))
                    .withLastError(record.get(getTableFieldLastError()))
                    .withLastUpdatedError(DateConverter.toLocalDateTime(record.get(getTableFieldLastUpdatedError())))
                    .build();
        } else {
            return null;
        }
    }

    public List<String> findFreshDataSymbols(LocalDateTime dateTime) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Result<Record1<String>> symbols = dsl.getDsl()
                    .select(getTableFieldSymbol())
                    .from(getTableName())
                    .where(getTableFieldLastUpdated().gt(fromLocalDateTime(dateTime)))
                    .fetch();
            return symbols.stream().map(r -> r.value1()).collect(Collectors.toList());
        }
    }

    public List<PriceDTO> readStatuses() {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Result<Record4<String, Timestamp, String, Timestamp>> records = dsl.getDsl()
                    .select(getTableFieldSymbol(), getTableFieldLastUpdated(),
                            getTableFieldLastError(), getTableFieldLastUpdatedError())
                    .from(getTableName())
                    .orderBy(getTableFieldSymbol())
                    .fetch();
            return records.stream()
                    .map(record -> PriceDTO.PriceDTOBuilder.createPriceDTO()
                    .withSymbol(record.get(getTableFieldSymbol()))
                    .withLastUpdated(DateConverter.toLocalDateTime(record.get(getTableFieldLastUpdated())))
                    .withLastError(record.get(getTableFieldLastError()))
                    .withLastUpdatedError(DateConverter.toLocalDateTime(record.get(getTableFieldLastUpdatedError())))
                    .build())
                    .collect(Collectors.toList());
        }
    }

    public Optional<PriceDTO> fixEmptyCAArrays(Optional<PriceDTO> optionalPriceDTO) {
        if (optionalPriceDTO.isPresent()) {
            List<TickData> tickDataList = optionalPriceDTO.get().getData();
            for (TickData tickData : tickDataList) {
                if (tickData.getEvents() != null && tickData.getEvents().size() == 0) {
                    tickData.setEvents(null);
                }
            }
        }

        return optionalPriceDTO;
    }

    public abstract PriceDTO fixMultipleDayRecords(PriceDTO price);
}
