package org.superbiz.util;

import org.junit.Test;
import org.superbiz.fetch.model.finviz.DayParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class MapMergerTest {

    @Test
    public void findUpdates() {
        DayParameters newParameters = createSampleNewParameters();
        DayParameters oldParameters = createSampleOldParameters();

        assertFalse(MapMerger.findUpdates(newParameters, newParameters).isPresent());

        Optional<DayParameters> diffParameters = MapMerger.findUpdates(newParameters, oldParameters);
        assertTrue(diffParameters.isPresent());
        assertTrue(diffParameters.get().containsKey("a"));
        assertTrue(diffParameters.get().get("a").equals("a123"));
    }

    private DayParameters createSampleOldParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "a123");
        parameters.put("b", "b234");
        parameters.put("c", "c345");
        return DayParameters.of(parameters);
    }

    private DayParameters createSampleNewParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "a124");
        parameters.put("b", "b234");
        parameters.put("c", "c345");
        return DayParameters.of(parameters);
    }
}