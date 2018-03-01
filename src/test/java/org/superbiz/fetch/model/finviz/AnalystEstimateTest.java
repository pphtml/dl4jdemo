package org.superbiz.fetch.model.finviz;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class AnalystEstimateTest {

    @Test
    public void parseDate() {
        final LocalDate date = AnalystEstimate.parseDate("Nov-21-17");
        assertEquals(LocalDate.parse("2017-11-21"), date);
    }
}