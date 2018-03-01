package org.superbiz.util;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class InsiderTradeParserTest {

    @Test
    public void parseDate() {
        InsiderTradeParser insiderTradeParser = new InsiderTradeParser(LocalDate.parse("2018-01-15"));

        assertEquals(LocalDate.parse("2017-05-30"), insiderTradeParser.parseDate("May 30"));
        assertEquals(LocalDate.parse("2018-01-05"), insiderTradeParser.parseDate("Jan 05"));
        assertEquals(LocalDate.parse("2017-01-20"), insiderTradeParser.parseDate("Jan 20"));
        assertEquals(LocalDate.parse("2018-01-14"), insiderTradeParser.parseDate("Jan 14"));
        assertEquals(LocalDate.parse("2018-01-15"), insiderTradeParser.parseDate("Jan 15"));
        assertEquals(LocalDate.parse("2017-01-16"), insiderTradeParser.parseDate("Jan 16"));
    }


    @Test
    public void parseDateTime() {
        InsiderTradeParser insiderTradeParser = new InsiderTradeParser(LocalDate.parse("2018-01-15"));

        assertEquals(LocalDateTime.parse("2017-02-06T04:31"), insiderTradeParser.parseDateTime("Feb 06 04:31 AM"));
        assertEquals(LocalDateTime.parse("2017-02-06T16:31"), insiderTradeParser.parseDateTime("Feb 06 04:31 PM"));
        assertEquals(LocalDateTime.parse("2018-01-14T16:31"), insiderTradeParser.parseDateTime("Jan 14 04:31 PM"));
        assertEquals(LocalDateTime.parse("2018-01-15T00:00"), insiderTradeParser.parseDateTime("Jan 15 00:00 AM"));
        assertEquals(LocalDateTime.parse("2018-01-15T16:31"), insiderTradeParser.parseDateTime("Jan 15 04:31 PM"));
        assertEquals(LocalDateTime.parse("2017-01-16T00:00"), insiderTradeParser.parseDateTime("Jan 16 00:00 AM"));
        assertEquals(LocalDateTime.parse("2017-01-16T16:31"), insiderTradeParser.parseDateTime("Jan 16 04:31 PM"));
//        assertEquals(LocalDate.parse("2018-01-05"), insiderTradeParser.parseDate("Jan 05"));
//        assertEquals(LocalDate.parse("2017-01-20"), insiderTradeParser.parseDate("Jan 20"));
//        assertEquals(LocalDate.parse("2018-01-15"), insiderTradeParser.parseDate("Jan 15"));
    }

    @Test
    public void initializationWorksForAllDays() {
        final LocalDate initialDate = LocalDate.parse("2017-01-01");
        for (int index = 0; index < 366 * 4; index++) {
            new InsiderTradeParser(initialDate.plusDays(index));
        }
    }
}