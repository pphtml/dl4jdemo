package org.superbiz.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.superbiz.dao.NonTradingDayDAO;
import org.superbiz.fetch.FetchData;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.GlobalInit;

import javax.inject.Inject;
import java.io.IOException;
import java.time.*;
import java.util.logging.Logger;

public class TradingDayService {
    static { GlobalInit.init(); }

    @Inject
    Logger logger;

    @Inject
    NonTradingDayDAO nonTradingDayDAO;

    public boolean isBeforeTradingStart() {
        LocalDateTime timeNY = LocalDateTime.now(ZoneId.of("America/New_York"));

        LocalDate date = timeNY.toLocalDate();
        LocalTime time = timeNY.toLocalTime();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean weekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        boolean holiday = nonTradingDayDAO.isHoliday(date);
        boolean periodBeforeTrading = time.getHour() >= 9 && time.getHour() < 10;

        boolean result = !weekend && !holiday && periodBeforeTrading;

        logger.info(String.format("Checking isBeforeTradingStart, NY: %s, weekend: %s, holiday: %s, beforeTrading: %s, result: %s",
                timeNY, weekend, holiday, periodBeforeTrading, result));

        return result;
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BasicModule());
        TradingDayService tradingDayService = injector.getInstance(TradingDayService.class);
        tradingDayService.isBeforeTradingStart();
    }
}
