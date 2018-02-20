package org.superbiz.fetch;

import org.junit.Ignore;
import org.junit.Test;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.fetch.model.MarketWatchData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.superbiz.util.Utils.readResourceToString;

public class FetchMarketWatchTest {
    FetchMarketWatch fetchMarketWatch = new FetchMarketWatch();

    @Test
    public void parseHtmlPage() throws ParsingException {
        String body = readResourceToString(getClass(), "AMZN_MarketWatch.html");
        final MarketWatchData marketWatchData = fetchMarketWatch.parseHtmlPage("AMZN", body, null);
        assertNotNull(marketWatchData);

        assertEquals("AMZN", marketWatchData.getSymbol());
        assertEquals("Buy", marketWatchData.getRecommendation());
        assertEquals(new BigDecimal("1661.00"), marketWatchData.getTargetPrice());
        assertEquals(new Integer(48), marketWatchData.getNumberOfRatings());
        assertEquals(new BigDecimal("1.26"), marketWatchData.getQuartersEstimate());
        assertEquals(new BigDecimal("8.40"), marketWatchData.getYearsEstimate());
        assertEquals(new BigDecimal("2.16"), marketWatchData.getLastQuarterEarnings());
        assertEquals(new BigDecimal("176.21"), marketWatchData.getMedianPeOnCy());
        assertEquals(new BigDecimal("4.29"), marketWatchData.getYearAgoEarnings());
        assertEquals(new BigDecimal("15.34"), marketWatchData.getNextFiscalYear());
        assertEquals(new BigDecimal("95.15"), marketWatchData.getMedianPeNextFy());

        assertEquals(new Integer(39), marketWatchData.getBuy());
        assertEquals(new Integer(5), marketWatchData.getOverweight());
        assertEquals(new Integer(4), marketWatchData.getHold());
        assertEquals(new Integer(1), marketWatchData.getUnderweight());
        assertEquals(new Integer(0), marketWatchData.getSell());
    }

    @Test
    @Ignore
    public void testStoreToDB() throws ParsingException {
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);

        String body = readResourceToString(getClass(), "AMZN_MarketWatch.html");
        final MarketWatchData marketWatchData = fetchMarketWatch.parseHtmlPage("AMZN", body, null);

        try (ConnAndDSL dsl = ConnAndDSL.create()) {
            fetchMarketWatch.storeToDB(dsl, marketWatchData, currentDate);
        }
    }

    @Test
    @Ignore
    public void testReadFromDB() {
        final List<String> symbols = Arrays.asList("AMZN");
        try (ConnAndDSL dsl = ConnAndDSL.create()) {
            Map<String, MarketWatchData> map = fetchMarketWatch.readFromDB(dsl, symbols);

            assertNotNull(map);
            assertTrue(map.containsKey("AMZN"));
            MarketWatchData marketWatchData = map.get("AMZN");

            assertNotNull(marketWatchData);

            assertEquals("AMZN", marketWatchData.getSymbol());
            assertEquals("Buy", marketWatchData.getRecommendation());
            assertEquals(new BigDecimal("1661.00"), marketWatchData.getTargetPrice());
            assertEquals(new Integer(48), marketWatchData.getNumberOfRatings());
            assertEquals(new BigDecimal("1.26"), marketWatchData.getQuartersEstimate());
            assertEquals(new BigDecimal("8.40"), marketWatchData.getYearsEstimate());
            assertEquals(new BigDecimal("2.16"), marketWatchData.getLastQuarterEarnings());
            assertEquals(new BigDecimal("176.21"), marketWatchData.getMedianPeOnCy());
            assertEquals(new BigDecimal("4.29"), marketWatchData.getYearAgoEarnings());
            assertEquals(new BigDecimal("15.34"), marketWatchData.getNextFiscalYear());
            assertEquals(new BigDecimal("95.15"), marketWatchData.getMedianPeNextFy());

            assertEquals(new Integer(39), marketWatchData.getBuy());
            assertEquals(new Integer(5), marketWatchData.getOverweight());
            assertEquals(new Integer(4), marketWatchData.getHold());
            assertEquals(new Integer(1), marketWatchData.getUnderweight());
            assertEquals(new Integer(0), marketWatchData.getSell());
        }
    }
}