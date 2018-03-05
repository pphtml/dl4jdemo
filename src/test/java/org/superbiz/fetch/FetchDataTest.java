package org.superbiz.fetch;

import org.junit.Test;
import org.superbiz.dto.PriceDTO;
import org.superbiz.fetch.model.TickData;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.superbiz.dto.PriceDTO.PriceDTOBuilder.createPriceDTO;

public class FetchDataTest {
    private FetchData fetchData = new FetchData();

    @Test
    public void mergePriceDTOs() {
        String symbol = "AMZN";

        List<TickData> oldTickData = Arrays.asList(
                TickData.from(1000000L, new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L),
                TickData.from(1000001L, new BigDecimal("1.01"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L),
                TickData.from(1000002L, new BigDecimal("1.02"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L),
                TickData.from(1000003L, new BigDecimal("1.03"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L),
                TickData.from(1000004L, new BigDecimal("1.04"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L)
        );

        PriceDTO oldPriceDTO = createPriceDTO()
                .withSymbol(symbol)
                .withLastUpdated(LocalDateTime.now())
                .withData(oldTickData)
                .withLastUpdatedError(null)
                .withLastError(null)
                .build();

        List<TickData> newTickData = Arrays.asList(
                TickData.from(1000002L, new BigDecimal("1.02"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L),
                TickData.from(1000003L, new BigDecimal("1.03"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L),
                TickData.from(1000004L, new BigDecimal("1.14"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L),
                TickData.from(1000005L, new BigDecimal("1.05"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L),
                TickData.from(1000006L, new BigDecimal("1.06"), new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00"), 1000L)
                );

        PriceDTO newPriceDTO = createPriceDTO()
                .withSymbol(symbol)
                .withLastUpdated(LocalDateTime.now())
                .withData(newTickData)
                .withLastUpdatedError(null)
                .withLastError(null)
                .build();

        final PriceDTO mergedPriceDTO = fetchData.mergePriceDTOs(Optional.of(oldPriceDTO), newPriceDTO);

        assertNotNull(mergedPriceDTO);
        assertNotNull(mergedPriceDTO.getData());
        assertEquals(7, mergedPriceDTO.getData().size());
    }
}