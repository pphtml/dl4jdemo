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

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.superbiz.fetch.FetchData.pricesLinkedMapFromList;

public class NetFetcherYahoo1MinTest {
    private NetFetcherYahoo netFetcherYahoo = new NetFetcherYahoo();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fetch() throws ParsingException {
        String data = Utils.readResourceToString(getClass(), "A_1m.data.json");

        ParsingResult result = netFetcherYahoo.processData(data);
        assertNotNull(result);
        assertNotNull(result.getTickData());
        assertEquals(1914, result.getTickData().size());

        LinkedHashMap<Long, TickData> map = pricesLinkedMapFromList(result.getTickData());

        final TickData tickData = map.get(1519741800L);
        assertNotNull(tickData);
    }
}