package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.ManyToOne;

@NamePattern("%s |quantity,productItem")
@Table(name = "JOKERAPP_TICKET_ITEM_LINE")
@Entity(name = "jokerapp$TicketItemLine")
public class TicketItemLine extends StandardEntity {
    private static final long serialVersionUID = -3274160509068241315L;

    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    protected Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "PRODUCT_ITEM_ID")
    protected ProductItem productItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TICKET_ITEM_ID")
    protected TicketItem ticketItem;

    public void setTicketItem(TicketItem ticketItem) {
        this.ticketItem = ticketItem;
    }

    public TicketItem getTicketItem() {
        return ticketItem;
    }


    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public ProductItem getProductItem() {
        return productItem;
    }


}