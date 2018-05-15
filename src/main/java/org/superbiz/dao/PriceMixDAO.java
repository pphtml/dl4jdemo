package org.superbiz.dao;

import org.superbiz.dto.PriceDTO;

import javax.inject.Inject;
import java.util.Optional;

public class PriceMixDAO {
    @Inject
    Price1mDAO price1mDAO;

    @Inject
    Price5mDAO price5mDAO;

    @Inject
    Price1dDAO price1dDAO;

    private PriceAbstractDAO findPriceDAO(Interval interval) {
        if (interval == Interval.M1) {
            return price1mDAO;
        } else if (interval == Interval.M5) {
            return price5mDAO;
        } else if (interval == Interval.D1) {
            return price1dDAO;
        } else {
            throw new IllegalStateException(String.format("Interval %s not supported.", interval));
        }
    }

    public Optional<PriceDTO> read(String symbol, Interval interval) {
        return findPriceDAO(interval).read(symbol);
    }

    public enum Interval {
        D1("1d"), M5("5m"), M1("1m");

        private final String identifier;

        Interval(String identifier) {
            this.identifier = identifier;
        }

        public static Optional<Interval> of(String identifier) {
            for (Interval interval : Interval.values()) {
                if (interval.identifier.equals(identifier)) {
                    return Optional.of(interval);
                }
            }

            return Optional.empty();
        }
    }
}
