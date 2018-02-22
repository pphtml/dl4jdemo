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
import java.util.Optional;

import static org.junit.Assert.*;
import static org.superbiz.fetch.model.MarketWatchData.MarketWatchDataBuilder.aMarketWatchData;
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
        marketWatchData.setLastUpdated(currentDate);

        try (ConnAndDSL dsl = ConnAndDSL.create()) {
            fetchMarketWatch.storeToDB(dsl, marketWatchData);
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

    @Test
    @Ignore
    public void testComputeDiff() throws ParsingException {
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);
        final String symbol = "AMZN";
        final List<String> symbols = Arrays.asList(symbol);
        try (ConnAndDSL dsl = ConnAndDSL.create()) {
            Map<String, MarketWatchData> map = fetchMarketWatch.readFromDB(dsl, symbols);

            assertNotNull(map);
            assertTrue(map.containsKey(symbol));
            MarketWatchData oldMarketWatchData = map.get(symbol);

            String body = readResourceToString(getClass(), "AMZN_MarketWatch_2.html");
            final MarketWatchData newMarketWatchData = fetchMarketWatch.parseHtmlPage(symbol, body, null);
            newMarketWatchData.setLastUpdated(currentDate);
            assertNotNull(newMarketWatchData);

            Optional<MarketWatchData> diffMarketWatchData = fetchMarketWatch.computeDiff(oldMarketWatchData, newMarketWatchData);
            System.out.println(diffMarketWatchData);

//            assertNotNull(marketWatchData);
//
//            assertEquals("AMZN", marketWatchData.getSymbol());
//            assertEquals("Buy", marketWatchData.getRecommendation());
//            assertEquals(new BigDecimal("1661.00"), marketWatchData.getTargetPrice());
//            assertEquals(new Integer(48), marketWatchData.getNumberOfRatings());
//            assertEquals(new BigDecimal("1.26"), marketWatchData.getQuartersEstimate());
//            assertEquals(new BigDecimal("8.40"), marketWatchData.getYearsEstimate());
//            assertEquals(new BigDecimal("2.16"), marketWatchData.getLastQuarterEarnings());
//            assertEquals(new BigDecimal("176.21"), marketWatchData.getMedianPeOnCy());
//            assertEquals(new BigDecimal("4.29"), marketWatchData.getYearAgoEarnings());
//            assertEquals(new BigDecimal("15.34"), marketWatchData.getNextFiscalYear());
//            assertEquals(new BigDecimal("95.15"), marketWatchData.getMedianPeNextFy());
//
//            assertEquals(new Integer(39), marketWatchData.getBuy());
//            assertEquals(new Integer(5), marketWatchData.getOverweight());
//            assertEquals(new Integer(4), marketWatchData.getHold());
//            assertEquals(new Integer(1), marketWatchData.getUnderweight());
//            assertEquals(new Integer(0), marketWatchData.getSell());
        }
    }

    @Test
    public void testConvertDiffToJson() {
        MarketWatchData diffMarketWatchData = aMarketWatchData()
                .withSymbol("AMZN")
                .withTargetPrice(new BigDecimal("1234.50"))
                .withLastUpdated(LocalDate.parse("2017-01-01"))
                .build();

        String json = fetchMarketWatch.convertDiffToJson(diffMarketWatchData);
        assertNotNull(json);
        System.out.println(json);
    }

    @Test
    public void testMarshallHistoryRecords() {
        MarketWatchData diffMarketWatchData = aMarketWatchData()
                .withSymbol("AMZN")
                .withTargetPrice(new BigDecimal("1234.50"))
                .withLastUpdated(LocalDate.parse("2017-01-01"))
                .build();

        List<MarketWatchData> marketWatchDataList = Arrays.asList(diffMarketWatchData);
        String json = fetchMarketWatch.marshallHistoryRecords(marketWatchDataList);
        assertNotNull(json);
        assertEquals("[{\"symbol\":\"AMZN\",\"targetPrice\":1234.50,\"lastUpdated\":\"2017-01-01\"}]", json);
    }

    @Test
    public void unmarshallHistoryRecords() {
        MarketWatchData marketWatchData = aMarketWatchData()
                .build();
        List<MarketWatchData> marketWatchDataList = fetchMarketWatch.unmarshallHistoryRecords(marketWatchData);
        assertNotNull(marketWatchDataList);
        assertEquals(0, marketWatchDataList.size());

        marketWatchData = aMarketWatchData()
                .withHistory("[{\"symbol\":\"AMZN\",\"targetPrice\":1234.50,\"lastUpdated\":\"2017-01-01\"}]")
                .build();
        marketWatchDataList = fetchMarketWatch.unmarshallHistoryRecords(marketWatchData);
        assertNotNull(marketWatchDataList);
        assertEquals(1, marketWatchDataList.size());
        assertEquals("AMZN", marketWatchDataList.get(0).getSymbol());
    }

    @Test
    public void testNewHistory() {
        MarketWatchData emptyHistory = aMarketWatchData()
                .build();
        MarketWatchData diffMarketWatchData = aMarketWatchData()
                .withTargetPrice(new BigDecimal("1234.50"))
                .withLastUpdated(LocalDate.parse("2017-01-01"))
                .build();

        String newHistory = fetchMarketWatch.newHistory(emptyHistory, diffMarketWatchData);

        assertEquals("[{\"targetPrice\":1234.50,\"lastUpdated\":\"2017-01-01\"}]", newHistory);

        MarketWatchData someHistory = aMarketWatchData()
                .withHistory("[{\"targetPrice\":1234.40,\"lastUpdated\":\"2016-12-31\"}]")
                .build();

        newHistory = fetchMarketWatch.newHistory(someHistory, diffMarketWatchData);

        assertEquals("[{\"targetPrice\":1234.50,\"lastUpdated\":\"2017-01-01\"},{\"targetPrice\":1234.40,\"lastUpdated\":\"2016-12-31\"}]", newHistory);
    }
}