package org.superbiz.fetch.model.finviz;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InsiderTrade {
    private String relationship;
    private LocalDate date;
    private String transaction;
    private BigDecimal price;
    private Long shares;
    private BigDecimal value;
    private Long sharesTotal;
    private LocalDateTime secForm;

    public InsiderTrade() {
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setShares(Long shares) {
        this.shares = shares;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setSharesTotal(Long sharesTotal) {
        this.sharesTotal = sharesTotal;
    }

    public void setSecForm(LocalDateTime secForm) {
        this.secForm = secForm;
    }

    public InsiderTrade(String relationship, LocalDate date, String transaction, BigDecimal price, Long shares, BigDecimal value, Long sharesTotal, LocalDateTime secForm) {
        this.relationship = relationship;
        this.date = date;
        this.transaction = transaction;
        this.price = price;
        this.shares = shares;
        this.value = value;
        this.sharesTotal = sharesTotal;
        this.secForm = secForm;
    }

    public String getRelationship() {
        return relationship;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTransaction() {
        return transaction;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getShares() {
        return shares;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Long getSharesTotal() {
        return sharesTotal;
    }

    public LocalDateTime getSecForm() {
        return secForm;
    }
}
