package org.superbiz.fetch.model.finviz;

import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.InsiderTradeParser;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(BasicModule.class)
public class InsiderTradeTest {
    @Inject
    InsiderTradeParser insiderTradeParser;

    @Test
    public void of() {
        InsiderTrade insiderTrade = insiderTradeParser.parse("Director", "Feb 05", "Option Exercise", "13.89",
                "15,482", "215,045", "75,937", "Feb 06 04:30 PM");
        assertNotNull(insiderTrade);;
        assertEquals("Director", insiderTrade.getRelationship());
        assertEquals(LocalDate.parse("2018-02-05"), insiderTrade.getDate());
        assertEquals("Option Exercise", insiderTrade.getTransaction());
        assertEquals(new BigDecimal("13.89"), insiderTrade.getPrice());
        assertEquals(new Long(15482), insiderTrade.getShares());
        assertEquals(new BigDecimal("215045"), insiderTrade.getValue());
        assertEquals(new Long(75937), insiderTrade.getSharesTotal());
        assertEquals(LocalDateTime.parse("2018-02-06T16:30"), insiderTrade.getSecForm());
    }
}