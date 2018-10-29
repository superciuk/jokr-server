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

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER")
@Entity(name = "jokerapp$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = 7728262321009676563L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected TableItem orderId;

    @NotNull
    @Column(name = "ITEM_NAME", nullable = false)
    protected String itemName;

    @Column(name = "ITEM_PRICE", precision = 12, scale = 2)
    protected BigDecimal itemPrice;

    @Column(name = "TAX_AMOUNT", precision = 12, scale = 2)
    protected BigDecimal taxAmount;



    public void setOrderId(TableItem orderId) {
        this.orderId = orderId;
    }

    public TableItem getOrderId() {
        return orderId;
    }


    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }


    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }








    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }



}