package org.superbiz.fetch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.superbiz.fetch.model.Event;
import org.superbiz.fetch.model.EventType;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.fetch.model.TickData;
import org.superbiz.util.TickDataConverter;
import org.superbiz.util.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.superbiz.fetch.FetchData.pricesLinkedMapFromList;

public class NetFetcherYahooTest {
    private NetFetcherYahoo netFetcherYahoo = new NetFetcherYahoo();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fetch() {
        String data = Utils.readResourceToString(getClass(), "A_5m.data.json");
        //String data = Utils.readResourceToString(getClass(), "AMZN_5m.data.json");

        ParsingResult result = netFetcherYahoo.processData(data);
        assertNotNull(result);
        assertNotNull(result.getTickData());
        assertEquals(3039, result.getTickData().size());

        LinkedHashMap<Long, TickData> map = pricesLinkedMapFromList(result.getTickData());

        final TickData tickData = map.get(1514557800L);
        assertNotNull(tickData);

        final List<Event> events = tickData.getEvents();
        assertNotNull(events);

        assertEquals(1L, events.size());

        final Event event = events.get(0);
        assertNotNull(event);
        assertEquals(EventType.DIVIDEND, event.getEventType());
        assertEquals(new BigDecimal("0.149"), event.getAmount());

        System.out.println(TickDataConverter.tickDataAsJson(result.getTickData()));
    }

    @Test
    public void fetchDelisted() {
        expectedException.expect(DataProcessingException.class);
        expectedException.expectMessage("Code: Not Found, Description: No data found, symbol may be delisted");

        String data = Utils.readResourceToString(getClass(), "BF.B_5m.data.json");

        ParsingResult result = netFetcherYahoo.processData(data);
    }
}