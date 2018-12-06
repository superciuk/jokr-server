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
import java.util.UUID;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER_LINE")
@Entity(name = "jokerapp$OrderLine")
public class OrderLine extends StandardEntity {
    private static final long serialVersionUID = 2123398643400124806L;

    @Column(name = "QUANTITY")
    protected Integer quantity;

    @NotNull
    @Column(name = "ITEM_NAME", nullable = false)
    protected String itemName;

    @Column(name = "UNIT_PRICE")
    protected BigDecimal unitPrice;

    @NotNull
    @Column(name = "PRICE", nullable = false, precision = 12, scale = 2)
    protected BigDecimal price;

    @NotNull
    @Column(name = "TAXES", nullable = false, precision = 12, scale = 2)
    protected BigDecimal taxes;


    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @Column(name = "POSITION_")
    protected Integer position;

    @NotNull
    @Column(name = "HAS_MODIFIER", nullable = false)
    protected Boolean hasModifier = false;

    @NotNull
    @Column(name = "IS_MODIFIER", nullable = false)
    protected Boolean isModifier = false;

    @Column(name = "ITEM_TO_MDIFY_ID")
    protected UUID itemToMdifyId;

    @NotNull
    @Column(name = "IS_SENDED", nullable = false)
    protected Boolean isSended = false;

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }


    public void setIsSended(Boolean isSended) {
        this.isSended = isSended;
    }

    public Boolean getIsSended() {
        return isSended;
    }


    public void setHasModifier(Boolean hasModifier) {
        this.hasModifier = hasModifier;
    }

    public Boolean getHasModifier() {
        return hasModifier;
    }



    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }


    public void setItemToMdifyId(UUID itemToMdifyId) {
        this.itemToMdifyId = itemToMdifyId;
    }

    public UUID getItemToMdifyId() {
        return itemToMdifyId;
    }


    public void setIsModifier(Boolean isModifier) {
        this.isModifier = isModifier;
    }

    public Boolean getIsModifier() {
        return isModifier;
    }


    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getPosition() {
        return position;
    }


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