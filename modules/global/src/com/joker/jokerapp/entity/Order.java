package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.OrderBy;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import javax.persistence.OneToOne;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_ORDER")
@Entity(name = "jokerapp$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = 7728262321009676563L;

    @NotNull
    @Column(name = "ACTUAL_SEATS", nullable = false)
    protected Integer actualSeats;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;





    @Column(name = "TABLE_ITEM_CAPTION")
    protected String tableItemCaption;

    @NotNull
    @Column(name = "WITH_SERVICE", nullable = false)
    protected Boolean withService = false;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "order")
    protected List<OrderLine> orderLines;

    @Column(name = "DISCOUNT", precision = 12, scale = 2)
    protected BigDecimal discount;

    @Column(name = "CHARGE")
    protected BigDecimal charge;

    @Column(name = "TAXES")
    protected BigDecimal taxes;


    public void setTableItemCaption(String tableItemCaption) {
        this.tableItemCaption = tableItemCaption;
    }

    public String getTableItemCaption() {
        return tableItemCaption;
    }


    public void setWithService(Boolean withService) {
        this.withService = withService;
    }

    public Boolean getWithService() {
        return withService;
    }


    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }


    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }



    public void setActualSeats(Integer actualSeats) {
        this.actualSeats = actualSeats;
    }

    public Integer getActualSeats() {
        return actualSeats;
    }


    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }


    public void setStatus(OrderStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public OrderStatus getStatus() {
        return status == null ? null : OrderStatus.fromId(status);
    }


    }