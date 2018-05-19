package org.superbiz.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimezoneNewYork {
    private static final ZoneId ZONE_NEW_YORK = ZoneId.of("America/New_York");

    public static LocalDateTime fromTimestamp(long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZONE_NEW_YORK).toLocalDateTime();
    }
}
