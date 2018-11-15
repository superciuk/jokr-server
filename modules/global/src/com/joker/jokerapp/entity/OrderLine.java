package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.math.BigDecimal;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.annotation.OnDelete;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER_LINE")
@Entity(name = "jokerapp$OrderLine")
public class OrderLine extends StandardEntity {
    private static final long serialVersionUID = 2123398643400124806L;

    @NotNull
    @Column(name = "ITEM_NAME", nullable = false)
    protected String itemName;

    @NotNull
    @Column(name = "PRICE", nullable = false, precision = 12, scale = 2)
    protected BigDecimal price;

    @NotNull
    @Column(name = "TAXES", nullable = false, precision = 12, scale = 2)
    protected BigDecimal taxes;


    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }


    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }


    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }


    public BigDecimal getTaxes() {
        return taxes;
    }


    public BigDecimal getPrice() {
        return price;
    }



}