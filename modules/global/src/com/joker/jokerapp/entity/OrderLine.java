package com.joker.jokerapp.entity;

// import javax.inject.Inject;
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

// import com.haulmont.cuba.core.global.DataManager;

import java.util.UUID;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER_LINE")
@Entity(name = "jokerapp$OrderLine")

public class OrderLine extends StandardEntity {
    private static final long serialVersionUID = 2123398643400124806L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TICKET_ID")
    protected Ticket ticket;

    @Column(name = "QUANTITY")
    protected Integer quantity;

    @NotNull
    @Column(name = "ITEM_NAME", nullable = false)
    protected String itemName;

    @Column(name = "ITEM_ID")
    protected UUID itemId;

    @Column(name = "UNIT_PRICE")
    protected BigDecimal unitPrice;

    @NotNull
    @Column(name = "PRICE", nullable = false, precision = 12, scale = 2)
    protected BigDecimal price;

    @NotNull
    @Column(name = "TAXES", nullable = false, precision = 12, scale = 2)
    protected BigDecimal taxes;


    @Column(name = "POSITION_")
    protected Integer position;

    @Column(name = "NEXT_MODIFIER_POSITION")
    protected Integer nextModifierPosition;

    @NotNull
    @Column(name = "HAS_MODIFIER", nullable = false)
    protected Boolean hasModifier = false;

    @NotNull
    @Column(name = "IS_MODIFIER", nullable = false)
    protected Boolean isModifier = false;

    @Column(name = "ITEM_TO_MODIFY_ID")
    protected UUID itemToModifyId;



    @Column(name = "ISDONE")
    protected Boolean isdone;

    @Column(name = "IS_REVERSED")
    protected Boolean isReversed;

    @Column(name = "PRINTER_GROUP")
    protected String printerGroup;

    public void setIsdone(Boolean isdone) {
        this.isdone = isdone;
    }

    public Boolean getIsdone() {
        return isdone;
    }


    public void setIsReversed(Boolean isReversed) {
        this.isReversed = isReversed;
    }

    public Boolean getIsReversed() {
        return isReversed;
    }


    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }


    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public UUID getItemId() {
        return itemId;
    }




    public void setPrinterGroup(String printerGroup) {
        this.printerGroup = printerGroup;
    }

    public String getPrinterGroup() {
        return printerGroup;
    }


    public void setNextModifierPosition(Integer nextModifierPosition) {
        this.nextModifierPosition = nextModifierPosition;
    }

    public Integer getNextModifierPosition() {
        return nextModifierPosition;
    }


    public void setItemToModifyId(UUID itemToModifyId) {
        this.itemToModifyId = itemToModifyId;
    }

    public UUID getItemToModifyId() {
        return itemToModifyId;
    }


    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
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