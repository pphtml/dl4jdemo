package org.superbiz.fetch.model.finviz;

import org.nd4j.shade.jackson.databind.annotation.JsonDeserialize;
import org.nd4j.shade.jackson.databind.annotation.JsonSerialize;
import org.superbiz.util.LocalDateDeserializer;
import org.superbiz.util.LocalDateSerializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AnalystEstimate {
    private static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("MMM-dd-yy");

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalystEstimate that = (AnalystEstimate) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(action, that.action) &&
                Objects.equals(house, that.house) &&
                Objects.equals(status, that.status) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, action, house, status, value);
    }
}
