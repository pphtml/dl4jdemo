package org.superbiz.util;

import org.superbiz.fetch.model.finviz.InsiderTrade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InsiderTradeParser {
    private static final DateTimeFormatter PATTERN_DATE = DateTimeFormatter.ofPattern("yyy MMM dd");
    private static final DateTimeFormatter PATTERN_DATETIME = DateTimeFormatter.ofPattern("yyy MMM dd hh:mm a");
    private final LocalDate initialDate;

//    private Map<String, Integer> monthYearMapping;
//    private Map<String, Integer> monthMonthMapping;

    public InsiderTradeParser(LocalDate initialDate) {
        this.initialDate = initialDate;
//        this.monthYearMapping = IntStream.range(0, 12)
//                .mapToObj(monthIndex -> initialDate.minusMonths(monthIndex))
////                .forEach(d ->
////                        System.out.println(d.format(PATTERN)));
//                .collect(Collectors.toMap(d -> d.format(PATTERN), d -> d.getYear()));
//        this.monthMonthMapping = IntStream.range(0, 12)
//                .mapToObj(monthIndex -> initialDate.minusMonths(monthIndex))
//                .collect(Collectors.toMap(d -> d.format(PATTERN), d -> d.getMonth().getValue()));
    }

    public InsiderTradeParser() {
        this(LocalDate.now());
    }

    public InsiderTrade parse(String relationship, String date, String transaction, String price, String shares,
                              String value, String sharesTotal, String secForm) {
        InsiderTrade result = new InsiderTrade(
                relationship,
                parseDate(date),
                transaction,
                parseMoney(price),
                parseCount(shares),
                parseMoney(value),
                parseCount(sharesTotal),
                parseDateTime(secForm));
        return result;
    }

    LocalDateTime parseDateTime(String dateTime) {
        String withYear = enhanceWithYear(dateTime);
        LocalDateTime result = LocalDateTime.parse(withYear, PATTERN_DATETIME); // 2018 Feb 06 04:31 PM
        if (result.toLocalDate().isAfter(this.initialDate)) {
            return result.minusYears(1);
        } else {
            return result;
        }
    }

    private String enhanceWithYear(String dateTime) {
        String withYear = String.format("%d %s", this.initialDate.getYear(), dateTime);
        return withYear;
    }

    Long parseCount(String count) {
        String withoutCommas = count.replaceAll(",", "");
        return Long.parseLong(withoutCommas);
    }

    BigDecimal parseMoney(String price) {
        String withoutCommas = price.replaceAll(",", "");
        return new BigDecimal(withoutCommas);
    }

    LocalDate parseDate(String date) {
        String withYear = enhanceWithYear(date);
        LocalDate result = LocalDate.parse(withYear, PATTERN_DATE);
        if (result.isAfter(this.initialDate)) {
            return result.minusYears(1);
        } else {
            return result;
        }

//        String monthKey = date.replaceAll("(\\d|\\s)", "");
//        String dayKey = date.replaceAll("(\\D|\\s)", "");
//        LocalDate result = LocalDate.of(this.monthYearMapping.get(monthKey),
//                this.monthMonthMapping.get(monthKey),
//                Integer.parseInt(dayKey)
//        );
//        if (result.isAfter(this.initialDate)) {
//            return result.minusYears(1);
//        } else {
//            return result;
//        }
    }
}
