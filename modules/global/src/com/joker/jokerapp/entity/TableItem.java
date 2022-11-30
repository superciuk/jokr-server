package com.joker.jokerapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;

@NamePattern("%s|tableNumber")
@Table(name = "JOKERAPP_TABLE_ITEM")
@Entity(name = "jokerapp$TableItem")
public class TableItem extends StandardEntity {
    private static final long serialVersionUID = -4769521418786074068L;

    @NotNull
    @Column(name = "TABLE_NUMBER", nullable = false, unique = true)
    protected Integer tableNumber;

    @Column(name = "TABLE_CAPTION")
    protected String tableCaption;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TABLE_AREA_ID")
    protected TableItemArea tableItemArea;

    @Column(name = "SEATS_CAPACITY")
    protected Integer seatsCapacity;

    @NotNull
    @Column(name = "TABLE_STATUS", nullable = false)
    protected String tableStatus;

    @Column(name = "TABLE_RESERVATION_NAME")
    protected String tableReservationName;

    @Column(name = "TABLE_RESERVATION_SEATS")
    protected String tableReservationSeats;

    @Column(name = "TABLE_RESERVATION_TIME")
    protected String tableReservationTime;

    @Column(name = "TABLE_RESERVATION_PHONE_NUMBER")
    protected String tableReservationPhoneNumber;

    @Composition
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.DENY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_ORDER_ID", unique = true)
    protected Order currentOrder;

    @Composition
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.DENY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_ORDER_ID", unique = true)
    protected Order lastOrder;

    @NotNull
    @Column(name = "WITH_SERVICE_BY_DEFAULT", nullable = false)
    protected Boolean withServiceByDefault = false;

    @NotNull
    @Column(name = "CHECKED", nullable = false)
    protected Boolean checked = false;


    public void setTableItemArea(TableItemArea tableItemArea) {
        this.tableItemArea = tableItemArea;
    }

    public TableItemArea getTableItemArea() {
        return tableItemArea;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setTableCaption(String tableCaption) {
        this.tableCaption = tableCaption;
    }

    public String getTableCaption() {
        return tableCaption;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setWithServiceByDefault(Boolean withServiceByDefault) { this.withServiceByDefault = withServiceByDefault; }

    public Boolean getWithServiceByDefault() {
        return withServiceByDefault;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setLastOrder(Order lastOrder) {
        this.lastOrder = lastOrder;
    }

    public Order getLastOrder() {
        return lastOrder;
    }

    public void setTableStatus(TableItemStatus tableStatus) { this.tableStatus = tableStatus == null ? null : tableStatus.getId(); }

    public TableItemStatus getTableStatus() {
        return tableStatus == null ? null : TableItemStatus.fromId(tableStatus);
    }

    public void setTableStatus(String tableStatus) {
        this.tableStatus = tableStatus;
    }

    public String getTableReservationName() {
        return tableReservationName;
    }

    public void setTableReservationName(String tableReservationName) {
        this.tableReservationName = tableReservationName;
    }

    public String getTableReservationSeats() {
        return tableReservationSeats;
    }

    public void setTableReservationSeats(String tableReservationSeats) {
        this.tableReservationSeats = tableReservationSeats;
    }

    public String getTableReservationTime() {
        return tableReservationTime;
    }

    public void setTableReservationTime(String tableReservationTime) {
        this.tableReservationTime = tableReservationTime;
    }

    public String getTableReservationPhoneNumber() {
        return tableReservationPhoneNumber;
    }

    public void setTableReservationPhoneNumber(String tableReservationPhoneNumber) {
        this.tableReservationPhoneNumber = tableReservationPhoneNumber;
    }

    public void setSeatsCapacity(Integer seatsCapacity) {
        this.seatsCapacity = seatsCapacity;
    }

    public Integer getSeatsCapacity() {
        return seatsCapacity;
    }

}