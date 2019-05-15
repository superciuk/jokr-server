package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_TICKET")
@Entity(name = "jokerapp$Ticket")
public class Ticket extends StandardEntity {
    private static final long serialVersionUID = 2784699364790594152L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @Column(name = "TICKET_NUMBER")
    protected Integer ticketNumber;

    @Column(name = "TICKET_STATUS")
    protected String ticketStatus;

    @Column(name = "SUBTICKET_STATUS")
    protected String subticketStatus;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "ticket")
    protected List<OrderLine> orderLines;

    public void setSubticketStatus(String subticketStatus) {
        this.subticketStatus = subticketStatus;
    }

    public String getSubticketStatus() {
        return subticketStatus;
    }

    public void setTicketNumber(Integer ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Integer getTicketNumber() {
        return ticketNumber;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus == null ? null : ticketStatus.getId();
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus == null ? null : TicketStatus.fromId(ticketStatus);
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

}