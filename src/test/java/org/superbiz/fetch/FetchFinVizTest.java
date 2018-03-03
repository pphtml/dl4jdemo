package org.superbiz.fetch;

import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.superbiz.dto.MarketFinVizDTO;
import org.superbiz.fetch.model.finviz.AnalystEstimate;
import org.superbiz.fetch.model.finviz.InsiderTrade;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.GlobalInit;
import org.superbiz.util.InsiderTradeParser;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.superbiz.model.jooq.Tables.MARKET_FIN_VIZ;
import static org.superbiz.util.DateConverter.toLocalDateTime;
import static org.superbiz.util.Utils.readResourceToString;
import static org.superbiz.fetch.FetchFinViz.FinVizVO;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(BasicModule.class)
public class FetchFinVizTest {
    static { GlobalInit.init(); }

    @Inject
    FetchFinViz fetchFinViz;

    @Inject
    InsiderTradeParser insiderTradeParser;

    @Test
    public void processData() throws ParsingException {
        String body = readResourceToString(getClass(), "A_finViz.html");
        FinVizVO finVizWebResult = fetchFinViz.processData("AMZN", body, null);
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

    @Test
    public void mergeFinVizData() {
        MarketFinVizDTO marketFinVizDTO = createSampleMarketFinVizDTO();

        FinVizVO finVizVO = createSampleFinVizVO();

        FinVizVO mergedFinVizVO = fetchFinViz.mergeFinVizData(Optional.of(marketFinVizDTO), finVizVO);

        System.out.println(mergedFinVizVO);
    }

    private MarketFinVizDTO createSampleMarketFinVizDTO() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "a123");
        parameters.put("b", "b234");
        parameters.put("c", "c345");

        List<AnalystEstimate> analystEstimates = Arrays.asList(
                AnalystEstimate.of("Feb-26-18", "act2", "Goldman", "buy", "100$"),
                AnalystEstimate.of("Feb-23-18", "act1", "JPM", "sell", "123$"),
                AnalystEstimate.of("Feb-22-18", "act0", "Lehman", "hold", "1000$"),
                AnalystEstimate.of("Feb-22-18", "actm1", "Lehman", "sell", "150$")
        );

        List<InsiderTrade> insiderTradings = Arrays.asList(
                insiderTradeParser.parse("Director", "Feb 05", "Option Exercise", "14.00",
                        "15,482", "215,045", "75,937", "Feb 06 04:35 PM"),
                insiderTradeParser.parse("Director", "Feb 05", "Option Exercise", "13.89",
                        "15,482", "215,045", "75,937", "Feb 06 04:30 PM")
        );

        FetchFinViz.FinVizVO oldFinVizVO = FetchFinViz.FinVizVO.ofSingleParameters(LocalDate.parse("2018-02-26"), parameters,
                analystEstimates, insiderTradings);

        return MarketFinVizDTO.MarketFinVizDTOBuilder.createMarketFinVizDTO()
                .withSymbol("AMZN")
                .withParameters(oldFinVizVO.getParametersAsBytes())
                .withAnalysts(oldFinVizVO.getAnalystsAsBytes())
                .withInsiders(oldFinVizVO.getInsidersAsBytes())
                .build();
    }

    private FinVizVO createSampleFinVizVO() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "a124");
        parameters.put("b", "b234");
        parameters.put("c", "c345");

        List<AnalystEstimate> analystEstimates = Arrays.asList(
                AnalystEstimate.of("Feb-27-18", "act4", "Kokos", "SELL!", "220$"),
                AnalystEstimate.of("Feb-27-18", "act3", "Barclays", "SELL!", "200$"),
                AnalystEstimate.of("Feb-26-18", "act2", "Goldman", "buy", "100$"),
                AnalystEstimate.of("Feb-23-18", "act1", "JPM", "sell", "123$"),
                AnalystEstimate.of("Feb-22-18", "act0", "Lehman", "hold", "1000$")
        );

        List<InsiderTrade> insiderTradings = Arrays.asList(
                insiderTradeParser.parse("Director", "Feb 27", "Option Exercise", "15.00",
                        "15,482", "215,045", "75,937", "Feb 27 02:35 PM"),
                insiderTradeParser.parse("Director", "Feb 05", "Option Exercise", "14.00",
                        "15,482", "215,045", "75,937", "Feb 06 04:35 PM")
        );

        return FinVizVO.ofSingleParameters(LocalDate.parse("2018-02-27"), parameters,
                analystEstimates, insiderTradings);
    }
}