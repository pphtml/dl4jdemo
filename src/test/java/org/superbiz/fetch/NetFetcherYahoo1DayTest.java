package org.superbiz.fetch;

import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.fetch.model.TickData;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.Utils;

import javax.inject.Inject;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.superbiz.fetch.FetchData.pricesLinkedMapFromList;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(BasicModule.class)
public class NetFetcherYahoo1DayTest {
    @Inject
    private NetFetcherYahoo netFetcherYahoo;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fetch() throws ParsingException {
        String data = Utils.readResourceToString(getClass(), "GSPC_1d.data.json");

        ParsingResult result = netFetcherYahoo.processData(data);
        assertNotNull(result);
        assertNotNull(result.getTickData());
        assertEquals(2516, result.getTickData().size());

        LinkedHashMap<Long, TickData> map = pricesLinkedMapFromList(result.getTickData());

        final TickData tickData = map.get(1514557800L);
        assertNotNull(tickData);
    }
}