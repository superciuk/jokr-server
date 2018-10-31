package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.OrderBy;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER")
@Entity(name = "jokerapp$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = 7728262321009676563L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TABLE_ITEM_ID")
    protected TableItem tableItem;

    @NotNull
    @Column(name = "ACTUAL_SEATS", nullable = false)
    protected Integer actualSeats;

    @OrderBy("createTs DESC")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "order")
    protected List<OrderLine> orderLines;

    @Column(name = "DISCOUNT", precision = 12, scale = 2)
    protected BigDecimal discount;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

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


    public void setStatus(OrderStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public OrderStatus getStatus() {
        return status == null ? null : OrderStatus.fromId(status);
    }


    @NotNull
    @MetaProperty(mandatory = true)
    public BigDecimal getCharge() {
        return null;
    }

    @NotNull
    @MetaProperty(mandatory = true)
    public BigDecimal getTaxes() {
        return null;
    }


    public void setTableItem(TableItem tableItem) {
        this.tableItem = tableItem;
    }

    public TableItem getTableItem() {
        return tableItem;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }















}