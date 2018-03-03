package org.superbiz.util;

import org.superbiz.fetch.model.finviz.DayParameters;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MapMerger {
    public static Optional<DayParameters> findUpdates(DayParameters lastParameters, DayParameters oldParameters) {
        Map<String, String> diffMap = oldParameters.entrySet().stream()
                .filter(entry -> !entry.getValue().equals(lastParameters.get(entry.getKey())))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
        if (diffMap.isEmpty()) {
            return Optional.empty();
        } else {
            DayParameters dayParameters = new DayParameters();
            dayParameters.putAll(diffMap);
            return Optional.of(dayParameters);
        }
    }
}
