package org.superbiz.fetch;

import org.junit.Test;
import org.superbiz.fetch.model.ParsingResult;
import org.superbiz.util.Utils;

import java.io.IOException;
import java.net.URISyntaxException;

public class NetFetcherYahooTest {
    private NetFetcherYahoo netFetcherYahoo = new NetFetcherYahoo();

    @Test
    public void fetch() {
        String data = Utils.readResourceToString(getClass(), "A_5m.data.json");
        //String data = Utils.readResourceToString(getClass(), "AMZN_5m.data.json");

        ParsingResult result = netFetcherYahoo.processData(data);
        final String jsonString = result.asJson();
//        System.out.println(jsonString);
//        System.out.println(jsonString.length());
    }
}