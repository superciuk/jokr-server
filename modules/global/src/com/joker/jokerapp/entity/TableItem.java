package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.math.BigDecimal;
import java.util.UUID;

@NamePattern("%s|tableNumber")
@Table(name = "JOKERAPP_TABLE_ITEM")
@Entity(name = "jokerapp$TableItem")
public class TableItem extends StandardEntity {
    private static final long serialVersionUID = -4769521418786074068L;

    @NotNull
    @Column(name = "TABLE_NUMBER", nullable = false, unique = true)
    protected Integer tableNumber;

    @Column(name = "SEATS_CAPACITY")
    protected Integer seatsCapacity;

    @Column(name = "ACTUAL_SEATS")
    protected Integer actualSeats;

    @Column(name = "ORDER_ID")
    protected UUID orderId;

    @Column(name = "CHARGE", precision = 12, scale = 2)
    protected BigDecimal charge;

    @Column(name = "TAX", precision = 12, scale = 2)
    protected BigDecimal tax;

    @Column(name = "DISCOUNT", precision = 12, scale = 2)
    protected BigDecimal discount;

    @Column(name = "STATUS")
    protected Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }


    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }




    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }


    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }


    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }





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






}