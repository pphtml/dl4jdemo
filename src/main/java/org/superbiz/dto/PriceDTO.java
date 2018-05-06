package org.superbiz.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.superbiz.fetch.model.TickData;

import java.time.LocalDateTime;
import java.util.List;

public class PriceDTO {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String symbol;
    private String lastError;
    private LocalDateTime lastUpdated;
    private List<TickData> data;
    private LocalDateTime lastUpdatedError;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<TickData> getData() {
        return data;
    }

    public void setData(List<TickData> data) {
        this.data = data;
    }

    public LocalDateTime getLastUpdatedError() {
        return lastUpdatedError;
    }

    public void setLastUpdatedError(LocalDateTime lastUpdatedError) {
        this.lastUpdatedError = lastUpdatedError;
    }


    public static final class PriceDTOBuilder {
        private String symbol;
        private String lastError;
        private LocalDateTime lastUpdated;
        private List<TickData> data;
        private LocalDateTime lastUpdatedError;

        private PriceDTOBuilder() {
        }

        public static PriceDTOBuilder createPriceDTO() {
            return new PriceDTOBuilder();
        }

        public PriceDTOBuilder withSymbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public PriceDTOBuilder withLastError(String lastError) {
            this.lastError = lastError;
            return this;
        }

        public PriceDTOBuilder withLastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public PriceDTOBuilder withData(List<TickData> data) {
            this.data = data;
            return this;
        }

        public PriceDTOBuilder withLastUpdatedError(LocalDateTime lastUpdatedError) {
            this.lastUpdatedError = lastUpdatedError;
            return this;
        }

        public PriceDTO build() {
            PriceDTO priceDTO = new PriceDTO();
            priceDTO.setSymbol(symbol);
            priceDTO.setLastError(lastError);
            priceDTO.setLastUpdated(lastUpdated);
            priceDTO.setData(data);
            priceDTO.setLastUpdatedError(lastUpdatedError);
            return priceDTO;
        }
    }

    public String asJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
