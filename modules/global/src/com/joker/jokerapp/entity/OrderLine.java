package com.joker.jokerapp.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER_LINE")
@Entity(name = "jokerapp$OrderLine")

public class OrderLine extends StandardEntity {
    private static final long serialVersionUID = 2123398643400124806L;

    @Composition
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

    @Column(name = "IS_BEVERAGE")
    protected Boolean isBeverage = false;

    @Column(name = "CHECKED")
    protected Boolean checked;

    @Column(name = "IS_REVERSED")
    protected Boolean isReversed;

    @Column(name = "PRINTER_GROUP")
    protected String printerGroup;

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {
        return checked;
    }


    public PrinterGroup getPrinterGroup() {
        return printerGroup == null ? null : PrinterGroup.fromId(printerGroup);
    }

    public void setPrinterGroup(PrinterGroup printerGroup) {
        this.printerGroup = printerGroup == null ? null : printerGroup.getId();
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

    public void setIsBeverage(Boolean isBeverage) { this.isBeverage = isBeverage; }

    public Boolean getIsBeverage() { return isBeverage; }

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