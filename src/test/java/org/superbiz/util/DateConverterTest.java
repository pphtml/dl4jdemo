package org.superbiz.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.time.LocalDate;

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
}