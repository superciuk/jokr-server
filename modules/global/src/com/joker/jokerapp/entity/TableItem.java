package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;

@NamePattern("%s - %s|area,number")
@Table(name = "JOKERAPP_TABLE_ITEM")
@Entity(name = "jokerapp$TableItem")
public class TableItem extends StandardEntity {
    private static final long serialVersionUID = -42909139055995657L;

    @NotNull
    @Column(name = "NUMBER_", nullable = false)
    protected Integer number;

    @OneToMany(mappedBy = "tableItem")
    protected List<TicketItem> ticketItems;

    @NotNull
    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AREA_ID")
    protected TableItemArea area;

    @MetaProperty(related = "ticketItems")
    public String getStatus() {
        return null;
    }



    public void setTicketItems(List<TicketItem> ticketItems) {
        this.ticketItems = ticketItems;
    }

    public List<TicketItem> getTicketItems() {
        return ticketItems;
    }


    public TableItemArea getArea() {
        return area;
    }

    public void setArea(TableItemArea area) {
        this.area = area;
    }



    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }


}