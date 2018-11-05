package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.OneToMany;
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
    protected List<Order> order;

    @NotNull
    @Column(name = "TABLE_STATUS", nullable = false)
    protected String tableStatus;

    public void setTableStatus(TableItemStatus tableStatus) {
        this.tableStatus = tableStatus == null ? null : tableStatus.getId();
    }


    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public List<Order> getOrder() {
        return order;
    }


    public TableItemStatus getTableStatus() {
        return tableStatus == null ? null : TableItemStatus.fromId(tableStatus);
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

        if (order.size() > 0) {
            Order lastOrder = order.get(0);
            if (lastOrder != null && lastOrder.getStatus() == OrderStatus.open) {
                return lastOrder;
            }
        }

        return null;
    }


}