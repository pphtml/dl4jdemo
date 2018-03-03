package org.superbiz.fetch.model.finviz;

//import org.nd4j.shade.jackson.databind.annotation.JsonDeserialize;
//import org.nd4j.shade.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.superbiz.util.LocalDateDeserializer;
import org.superbiz.util.LocalDateSerializer;
import org.superbiz.util.LocalDateTimeDeserializer;
import org.superbiz.util.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class InsiderTrade {
    private String relationship;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private String transaction;
    private BigDecimal price;
    private Long shares;
    private BigDecimal value;
    private Long sharesTotal;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsiderTrade that = (InsiderTrade) o;
        return Objects.equals(relationship, that.relationship) &&
                Objects.equals(date, that.date) &&
                Objects.equals(transaction, that.transaction) &&
                Objects.equals(price, that.price) &&
                Objects.equals(shares, that.shares) &&
                Objects.equals(value, that.value) &&
                Objects.equals(sharesTotal, that.sharesTotal) &&
                Objects.equals(secForm, that.secForm);
    }

    @Override
    public int hashCode() {

        return Objects.hash(relationship, date, transaction, price, shares, value, sharesTotal, secForm);
    }
}
