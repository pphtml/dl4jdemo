package org.superbiz.dto;

import java.time.LocalDateTime;

public class PriceDTO {
    private String symbol;
    private String lastError;
    private LocalDateTime lastUpdated;
    private String data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
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
        private String data;
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

        public PriceDTOBuilder withData(String data) {
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
}
