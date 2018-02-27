package org.superbiz.util;

import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.nd4j.shade.jackson.core.type.TypeReference;
import org.nd4j.shade.jackson.databind.ObjectMapper;
import org.superbiz.fetch.model.MarketWatchData;
import org.superbiz.fetch.model.TickData;

import java.io.IOException;
import java.util.List;

public class TickDataConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<List<TickData>> VALUE_TYPE_REF = new TypeReference<List<TickData>>() {
    };

    public static String tickDataAsJson(List<TickData> tickDataList) {
        try {
            String result = objectMapper.writeValueAsString(tickDataList);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<TickData> jsonAsTickData(String json) {
        try {
            System.out.println(json);
            List<TickData> result = objectMapper.readValue(json, VALUE_TYPE_REF);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}