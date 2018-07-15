package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Table(name = "JOKERAPP_TICKET_ITEM")
@Entity(name = "jokerapp$TicketItem")
public class TicketItem extends StandardEntity {
    private static final long serialVersionUID = -8270403757555396296L;

    @NotNull
    @Column(name = "PAID", nullable = false)
    protected Boolean paid = false;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TABLE_ITEM_ID")
    protected TableItem tableItem;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "ticketItem")
    protected List<TicketItemLine> lines;

    public void setTableItem(TableItem tableItem) {
        this.tableItem = tableItem;
    }

    public TableItem getTableItem() {
        return tableItem;
    }


    public void setLines(List<TicketItemLine> lines) {
        this.lines = lines;
    }

    public List<TicketItemLine> getLines() {
        return lines;
    }


    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getPaid() {
        return paid;
    }


}