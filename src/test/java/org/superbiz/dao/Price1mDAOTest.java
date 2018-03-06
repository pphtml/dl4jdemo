package org.superbiz.dao;

import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.superbiz.dto.PriceDTO;
import org.superbiz.fetch.FetchFinViz;
import org.superbiz.guice.BasicModule;
import org.superbiz.util.GlobalInit;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(BasicModule.class)
public class Price1mDAOTest {
    static { GlobalInit.init(); }

    @Inject
    Price1mDAO price1mDAO;

    @Test
    public void read() {
        final Optional<PriceDTO> priceDTO = price1mDAO.read("AMZN");
        assertFalse(priceDTO.isPresent());
    }
}