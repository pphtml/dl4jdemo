package org.superbiz.fetch;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class NetFetcherYahooTest {

    @Test
    public void fetch() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass()
                .getResource("AMZN_5m.data.json").toURI());

        StringBuilder data = new StringBuilder();
        Stream<String> lines = Files.lines(path);
        lines.forEach(line -> data.append(line).append("\n"));
        lines.close();

        Object result = NetFetcherYahoo.parseJSON(data.toString());
    }


}