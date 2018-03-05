package org.superbiz.dao;

import org.superbiz.dto.PriceDTO;

import java.util.Optional;

public abstract class PriceAbstractDAO {
    public abstract Optional<PriceDTO> read(String symbol);

    public abstract void insertOrUpdate(PriceDTO mergedPriceDTO);

    public abstract void insertOrUpdateError(PriceDTO priceDTO);
}
