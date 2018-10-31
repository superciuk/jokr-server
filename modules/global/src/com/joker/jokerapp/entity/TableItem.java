package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;
import javax.persistence.OrderBy;

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

    @OrderBy("createTs DESC")
    @OneToMany(mappedBy = "tableItem")
    protected List<Order> orders;

    @MetaProperty(related = "orders")
    public TableItemStatus getStatus() {
        //return status == null ? null : TableItemStatus.fromId(status);
        return getCurrentOrder() == null ? TableItemStatus.closed : TableItemStatus.open;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setSeatsCapacity(Integer seatsCapacity) {
        this.seatsCapacity = seatsCapacity;
    }

    public Integer getSeatsCapacity() {
        return seatsCapacity;
    }

    public Order getCurrentOrder() {

        if (orders.size() > 0) {
            Order lastOrder = orders.get(0);
            if (lastOrder != null && lastOrder.getStatus() == OrderStatus.open) {
                return lastOrder;
            }
        }

        return null;
    }


}