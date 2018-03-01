package org.superbiz.fetch.model.finviz;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnalystEstimate {
    private static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("MMM-dd-yy");

    private LocalDate date;
    private String action;
    private String house;
    private String status;
    private String value;

    public AnalystEstimate() {
    }

    public static DateTimeFormatter getPATTERN() {
        return PATTERN;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static AnalystEstimate of(String date, String action, String house, String status, String value) {
        AnalystEstimate result = new AnalystEstimate();
        result.date = parseDate(date);
        result.action = action;
        result.house = house;
        result.status = status;
        result.value = value;
        return result;
    }

    static LocalDate parseDate(String date) {
        return LocalDate.parse(date, PATTERN);
    }
}
