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

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER")
@Entity(name = "jokerapp$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = 7728262321009676563L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDER_ID_ID")
    @NotNull
    protected TableItem orderId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TABLE_NUMBER_ID")
    protected TableItem tableNumber;

    @NotNull
    @Column(name = "ITEM_NAME", nullable = false)
    protected String itemName;

    @Column(name = "ITEM_PRICE")
    protected Double itemPrice;

    @Column(name = "TAX_AMOUNT")
    protected Double taxAmount;


    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;



    public TableItem getOrderId() {
        return orderId;
    }

    public void setOrderId(TableItem orderId) {
        this.orderId = orderId;
    }


    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


    public void setTableNumber(TableItem tableNumber) {
        this.tableNumber = tableNumber;
    }

    public TableItem getTableNumber() {
        return tableNumber;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }


}