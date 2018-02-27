package org.superbiz.fetch.model;

import java.util.List;

public class ParsingResult {
    private List<TickData> tickData;

    public static ParsingResult of(List<TickData> tickData) {
        ParsingResult result = new ParsingResult();
        result.tickData = tickData;
        return result;
    }

    public List<TickData> getTickData() {
        return tickData;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParsingResult{");
        sb.append("tickData=").append(tickData);
        sb.append('}');
        return sb.toString();
    }
}
