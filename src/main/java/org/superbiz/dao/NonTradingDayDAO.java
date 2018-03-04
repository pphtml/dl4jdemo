package org.superbiz.dao;

import org.jooq.Record;
import org.superbiz.db.ConnAndDSL3;
import org.superbiz.db.ConnAndDSLProvider;

import javax.inject.Inject;
import java.time.LocalDate;

import static org.superbiz.model.jooq.Tables.NON_TRADING_DAYS;
import static org.superbiz.util.DateConverter.fromLocalDate;

public class NonTradingDayDAO {
    @Inject
    ConnAndDSLProvider connAndDSLProvider;

    public boolean isHoliday(LocalDate date) {
        try (ConnAndDSL3 dsl = connAndDSLProvider.create()) {
            Record holiday = dsl.getDsl()
                    .select()
                    .from(NON_TRADING_DAYS)
                    .where(NON_TRADING_DAYS.DATE.eq(fromLocalDate(date)))
                    .fetchOne();
            return holiday != null;
        }
    }

}
