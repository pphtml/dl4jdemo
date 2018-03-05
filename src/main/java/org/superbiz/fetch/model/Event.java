package org.superbiz.fetch.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
    private EventType eventType;
    private BigDecimal amount;
    private Integer numerator;
    private Integer denominator;

    //           "dividends": {
    //            "1514557800": {
    //              "amount": 0.149,
    //              "date": 1514557800
    //            }
    //          }


    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getNumerator() {
        return numerator;
    }

    public void setNumerator(Integer numerator) {
        this.numerator = numerator;
    }

    public Integer getDenominator() {
        return denominator;
    }

    public void setDenominator(Integer denominator) {
        this.denominator = denominator;
    }


    public static final class EventBuilder {
        private EventType eventType;
        private BigDecimal amount;
        private Integer numerator;
        private Integer denominator;

        private EventBuilder() {
        }

        public static EventBuilder createEvent() {
            return new EventBuilder();
        }

        public EventBuilder withEventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public EventBuilder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public EventBuilder withNumerator(Integer numerator) {
            this.numerator = numerator;
            return this;
        }

        public EventBuilder withDenominator(Integer denominator) {
            this.denominator = denominator;
            return this;
        }

        public Event build() {
            Event event = new Event();
            event.setEventType(eventType);
            event.setAmount(amount);
            event.setNumerator(numerator);
            event.setDenominator(denominator);
            return event;
        }
    }
}
