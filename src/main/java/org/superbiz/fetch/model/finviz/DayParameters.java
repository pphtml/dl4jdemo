package org.superbiz.fetch.model.finviz;

//import com.fasterxml.jackson.annotation.JsonIgnore;
//import org.nd4j.shade.jackson.databind.annotation.JsonDeserialize;
//import org.nd4j.shade.jackson.databind.annotation.JsonSerialize;
import org.superbiz.util.LocalDateDeserializer;
import org.superbiz.util.LocalDateSerializer;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DayParameters extends HashMap<String, String> {
    public DayParameters(Map<String, String> singleParameters) {
        super(singleParameters);
    }

    public DayParameters() {
    }

    public static DayParameters of(Map<String, String> singleParameters) {
        return new DayParameters(singleParameters);
    }
//    private LocalDate date;
//    private Map<String, String> parameters;
//
//    public DayParameters(LocalDate date, Map<String, String> parameters) {
//        this.date = date;
//        this.parameters = parameters;
//    }
//
////    @JsonDeserialize(using = LocalDateDeserializer.class)
////    @JsonSerialize(using = LocalDateSerializer.class)
//    public LocalDate getDate() {
//        return date;
//    }
//
//    public void setDate(LocalDate date) {
//        this.date = date;
//    }
//
//    public Map<String, String> getParameters() {
//        return parameters;
//    }
//
//    public void setParameters(Map<String, String> parameters) {
//        this.parameters = parameters;
//    }
//
//    public static DayParameters of(LocalDate date, Map<String, String> singleParameters) {
//        return new DayParameters(date, singleParameters);
//    }
}
