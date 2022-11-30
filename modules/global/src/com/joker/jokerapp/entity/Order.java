package com.joker.jokerapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import java.math.BigDecimal;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER")
@Entity(name = "jokerapp$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = 7728262321009676563L;

    @NotNull
    @Column(name = "ACTUAL_SEATS", nullable = false)
    protected Integer actualSeats;

    @NotNull
    @Column(name = "CURRENT_STATUS", nullable = false)
    protected String currentStatus;

    @Column(name = "PREVIOUS_STATUS")
    protected String previousStatus;

    @Column(name = "ORDER_IN_PROGRESS")
    protected Boolean orderInProgress;

    @Column(name = "TABLE_ITEM_CAPTION")
    protected String tableItemCaption;

    @NotNull
    @Column(name = "WITH_SERVICE", nullable = false)
    protected Boolean withService = false;

    @Column(name = "DISCOUNT", precision = 12, scale = 2)
    protected BigDecimal discount;

    @Column(name = "CHARGE")
    protected BigDecimal charge;

    @Column(name = "TAXES")
    protected BigDecimal taxes;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "order")
    protected List<Ticket> tickets;

    @NotNull
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    protected User user;

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTableItemCaption(String tableItemCaption) {
        this.tableItemCaption = tableItemCaption;
    }

    public String getTableItemCaption() {
        return tableItemCaption;
    }

    public void setWithService(Boolean withService) {
        this.withService = withService;
    }

    public Boolean getWithService() {
        return withService;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setActualSeats(Integer actualSeats) {
        this.actualSeats = actualSeats;
    }

    public Integer getActualSeats() {
        return actualSeats;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setCurrentStatus(OrderStatus currentStatus) {
        this.currentStatus = currentStatus == null ? null : currentStatus.getId();
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus == null ? null : OrderStatus.fromId(currentStatus);
    }

    public void setPreviousStatus(OrderStatus previousStatus) {
        this.previousStatus = previousStatus == null ? null : previousStatus.getId();
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus == null ? null : OrderStatus.fromId(previousStatus);
    }

    public Boolean getOrderInProgress() {
        return orderInProgress;
    }

    public void setOrderInProgress(Boolean orderInProgress) {
        this.orderInProgress = orderInProgress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}