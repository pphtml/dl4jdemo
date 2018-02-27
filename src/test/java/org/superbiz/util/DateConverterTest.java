package org.superbiz.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateConverterTest extends TestCase {
//    private final DateConverter dateConverter = new DateConverter();

    @Test
    public void testToEpochSeconds() {
        long seconds = DateConverter.toEpochSeconds(LocalDate.parse("2007-01-01"));
        assertEquals(1167609600L, seconds);
    }

    @Test
    public void testFromEpochSeconds() {
        LocalDate date = DateConverter.fromEpochSeconds(1167609600);
        assertEquals(LocalDate.parse("2007-01-01"), date);
    }

    public void testFrom() {
        LocalDateTime dateTime = LocalDateTime.parse("2018-02-27T11:12:13");
        Timestamp sql = DateConverter.fromLocalDateTime(dateTime);
        assertEquals("2018-02-27 11:12:13.0", sql.toString());
    }
}