package org.superbiz.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.superbiz.dao.NonTradingDayDAO;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.GlobalInit;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Logger;


public class TradingDayChecker {
    static { GlobalInit.init(); }

    @Inject
    Logger LOGGER;

    @Inject
    NonTradingDayDAO nonTradingDayDAO;

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new BasicModule());
        TradingDayChecker tradingDayChecker = injector.getInstance(TradingDayChecker.class);
        boolean result = tradingDayChecker.checkDate(LocalDate.parse("2018-03-30"));
        System.out.println(result);
    }

    private boolean checkDate(LocalDate date) {
        return nonTradingDayDAO.isHoliday(date);
    }
}
