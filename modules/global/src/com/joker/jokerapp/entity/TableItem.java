package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|number")
@Table(name = "JOKERAPP_TABLE_ITEM")
@Entity(name = "jokerapp$TableItem")
public class TableItem extends StandardEntity {
    private static final long serialVersionUID = -4769521418786074068L;

    @NotNull
    @Column(name = "NUMBER_", nullable = false)
    protected Integer number;

    @Column(name = "SEATS_CAPACITY")
    protected Integer seatsCapacity;

    @Column(name = "ACTUAL_SEATS")
    protected Integer actualSeats;

    @Column(name = "ORDER_ID")
    protected Integer orderId;

    @Column(name = "PRICE")
    protected Long price;

    @Column(name = "TAX")
    protected Double tax;

    @Column(name = "DISCOUNT")
    protected Double discount;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;



    public void setActualSeats(Integer actualSeats) {
        this.actualSeats = actualSeats;
    }

    public Integer getActualSeats() {
        return actualSeats;
    }


    public void setSeatsCapacity(Integer seatsCapacity) {
        this.seatsCapacity = seatsCapacity;
    }

    public Integer getSeatsCapacity() {
        return seatsCapacity;
    }


    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getPrice() {
        return price;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getTax() {
        return tax;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getDiscount() {
        return discount;
    }


    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }


    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}