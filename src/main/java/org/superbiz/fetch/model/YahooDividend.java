package org.superbiz.fetch.model;

import java.math.BigDecimal;

public class YahooDividend {
    private BigDecimal amount;
    private Long date;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
