package org.superbiz.fetch;

import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.superbiz.fetch.model.finviz.AnalystEstimate;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.GlobalInit;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.*;
import static org.superbiz.util.Utils.readResourceToString;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(BasicModule.class)
public class FetchFinVizTest {
    static { GlobalInit.init(); }

    @Inject
    FetchFinViz fetchFinViz;

    @Test
    public void processData() throws ParsingException {
        String body = readResourceToString(getClass(), "A_finViz.html");
        FetchFinViz.FinVizVO finVizWebResult = fetchFinViz.processData("AMZN", body, null);
        assertNotNull(finVizWebResult);

        List<AnalystEstimate> analysts = finVizWebResult.getAnalysts();
        System.out.println(analysts);

//        assertEquals("AMZN", marketWatchData.getSymbol());
//        assertEquals("Buy", marketWatchData.getRecommendation());
//        assertEquals(new BigDecimal("1661.00"), marketWatchData.getTargetPrice());
//        assertEquals(new Integer(48), marketWatchData.getNumberOfRatings());
//        assertEquals(new BigDecimal("1.26"), marketWatchData.getQuartersEstimate());
//        assertEquals(new BigDecimal("8.40"), marketWatchData.getYearsEstimate());
//        assertEquals(new BigDecimal("2.16"), marketWatchData.getLastQuarterEarnings());
//        assertEquals(new BigDecimal("176.21"), marketWatchData.getMedianPeOnCy());
//        assertEquals(new BigDecimal("4.29"), marketWatchData.getYearAgoEarnings());
//        assertEquals(new BigDecimal("15.34"), marketWatchData.getNextFiscalYear());
//        assertEquals(new BigDecimal("95.15"), marketWatchData.getMedianPeNextFy());
//
//        assertEquals(new Integer(39), marketWatchData.getBuy());
//        assertEquals(new Integer(5), marketWatchData.getOverweight());
//        assertEquals(new Integer(4), marketWatchData.getHold());
//        assertEquals(new Integer(1), marketWatchData.getUnderweight());
//        assertEquals(new Integer(0), marketWatchData.getSell());

    }
}