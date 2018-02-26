package org.superbiz.fetch.model;

import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import java.util.List;

public class ParsingResult {
    private List<TickData> tickData;
    private ObjectMapper objectMapper = new ObjectMapper();

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

    public String asJson() {
        try {
            String result = objectMapper.writeValueAsString(tickData);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
