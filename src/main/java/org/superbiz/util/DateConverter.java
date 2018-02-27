package org.superbiz.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class DateConverter {
    // https://stackoverflow.com/questions/8992282/convert-localdate-to-localdatetime-or-java-sql-timestamp

    public static Date from(Optional<LocalDate> optionalDate) {
        if (optionalDate.isPresent()) {
            return from(optionalDate.get());
        } else {
            return null;
        }
    }

    public static Date from(LocalDate date) {
        long epoch = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        return new Date(epoch);
    }

    public static long toEpochMillis(LocalDate date) {
        return date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    public static LocalDate fromEpochSeconds(long epochSeconds) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC);
        System.out.println(dateTime);
        LocalDate date = dateTime.toLocalDate();
        return date;
    }

    public static long toEpochSeconds(LocalDate date) {
        return date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
    }

    public static Timestamp fromLocalDateTime(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
