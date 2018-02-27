package org.superbiz.fetch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.util.Utils;

import java.io.IOException;
import java.net.URISyntaxException;

public class NetFetcherYahooTest {
    private NetFetcherYahoo netFetcherYahoo = new NetFetcherYahoo();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fetch() {
        String data = Utils.readResourceToString(getClass(), "A_5m.data.json");
        //String data = Utils.readResourceToString(getClass(), "AMZN_5m.data.json");

        ParsingResult result = netFetcherYahoo.processData(data);
        //final String jsonString = result.asJson();
    }

    @Test
    public void fetchDelisted() {
        expectedException.expect(DataProcessingException.class);
        expectedException.expectMessage("Code: Not Found, Description: No data found, symbol may be delisted");

        String data = Utils.readResourceToString(getClass(), "BF.B_5m.data.json");

        ParsingResult result = netFetcherYahoo.processData(data);
    }
}