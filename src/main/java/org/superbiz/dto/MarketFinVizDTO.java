package org.superbiz.dto;

import java.time.LocalDateTime;

public class MarketFinVizDTO {
    private String symbol;
    private byte[] parameters;
    private byte[] analysts;
    private byte[] insiders;
    private LocalDateTime lastUpdatedSuccess;
    private LocalDateTime lastUpdatedError;
    private String lastError;
    private String lastWarning;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public byte[] getParameters() {
        return parameters;
    }

    public void setParameters(byte[] parameters) {
        this.parameters = parameters;
    }

    public byte[] getAnalysts() {
        return analysts;
    }

    public void setAnalysts(byte[] analysts) {
        this.analysts = analysts;
    }

    public byte[] getInsiders() {
        return insiders;
    }

    public void setInsiders(byte[] insiders) {
        this.insiders = insiders;
    }

    public LocalDateTime getLastUpdatedSuccess() {
        return lastUpdatedSuccess;
    }

    public void setLastUpdatedSuccess(LocalDateTime lastUpdatedSuccess) {
        this.lastUpdatedSuccess = lastUpdatedSuccess;
    }

    public LocalDateTime getLastUpdatedError() {
        return lastUpdatedError;
    }

    public void setLastUpdatedError(LocalDateTime lastUpdatedError) {
        this.lastUpdatedError = lastUpdatedError;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getLastWarning() {
        return lastWarning;
    }

    public void setLastWarning(String lastWarning) {
        this.lastWarning = lastWarning;
    }


    public static final class MarketFinVizDTOBuilder {
        private String symbol;
        private byte[] parameters;
        private byte[] analysts;
        private byte[] insiders;
        private LocalDateTime lastUpdatedSuccess;
        private LocalDateTime lastUpdatedError;
        private String lastError;
        private String lastWarning;

        private MarketFinVizDTOBuilder() {
        }

        public static MarketFinVizDTOBuilder createMarketFinVizDTO() {
            return new MarketFinVizDTOBuilder();
        }

        public MarketFinVizDTOBuilder withSymbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public MarketFinVizDTOBuilder withParameters(byte[] parameters) {
            this.parameters = parameters;
            return this;
        }

        public MarketFinVizDTOBuilder withAnalysts(byte[] analysts) {
            this.analysts = analysts;
            return this;
        }

        public MarketFinVizDTOBuilder withInsiders(byte[] insiders) {
            this.insiders = insiders;
            return this;
        }

        public MarketFinVizDTOBuilder withLastUpdatedSuccess(LocalDateTime lastUpdatedSuccess) {
            this.lastUpdatedSuccess = lastUpdatedSuccess;
            return this;
        }

        public MarketFinVizDTOBuilder withLastUpdatedError(LocalDateTime lastUpdatedError) {
            this.lastUpdatedError = lastUpdatedError;
            return this;
        }

        public MarketFinVizDTOBuilder withLastError(String lastError) {
            this.lastError = lastError;
            return this;
        }

        public MarketFinVizDTOBuilder withLastWarning(String lastWarning) {
            this.lastWarning = lastWarning;
            return this;
        }

        public MarketFinVizDTO build() {
            MarketFinVizDTO marketFinVizDTO = new MarketFinVizDTO();
            marketFinVizDTO.setSymbol(symbol);
            marketFinVizDTO.setParameters(parameters);
            marketFinVizDTO.setAnalysts(analysts);
            marketFinVizDTO.setInsiders(insiders);
            marketFinVizDTO.setLastUpdatedSuccess(lastUpdatedSuccess);
            marketFinVizDTO.setLastUpdatedError(lastUpdatedError);
            marketFinVizDTO.setLastError(lastError);
            marketFinVizDTO.setLastWarning(lastWarning);
            return marketFinVizDTO;
        }
    }

//    public static class Parameters {
//        public byte[] getBytes() {
//            return null;
//        }
//
//        public static Parameters from(byte[] bytes) {
//            if (true) throw new UnsupportedOperationException("implement me!");
//            return new Parameters();
//        }
//    }
//
//    public static class Insiders {
//        public byte[] getBytes() {
//            return null;
//        }
//
//        public static Insiders from(byte[] bytes) {
//            if (true) throw new UnsupportedOperationException("implement me!");
//            return new Insiders();
//        }
//    }
//
//    public static class Analysts {
//        public byte[] getBytes() {
//            return null;
//        }
//
//        public static Analysts from(byte[] bytes) {
//            if (true) throw new UnsupportedOperationException("implement me!");
//            return new Analysts();
//        }
//    }
}
