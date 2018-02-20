package org.superbiz.fetch;

import org.junit.Test;
import org.superbiz.util.Utils;

import java.io.IOException;
import java.net.URISyntaxException;

public class NetFetcherYahooTest {

    @Test
    public void fetch() throws URISyntaxException, IOException {
        String data = Utils.readResourceToString(getClass(), "AMZN_5m.data.json");

        Object result = NetFetcherYahoo.parseJSON(data);
    }


}