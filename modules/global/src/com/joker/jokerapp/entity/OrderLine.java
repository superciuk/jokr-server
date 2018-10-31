package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.math.BigDecimal;
import javax.persistence.Column;

@NamePattern("%s|productItem")
@Table(name = "JOKERAPP_ORDER_LINE")
@Entity(name = "jokerapp$OrderLine")
public class OrderLine extends StandardEntity {
    private static final long serialVersionUID = 2123398643400124806L;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @NotNull
    @Column(name = "PRICE", nullable = false, precision = 12, scale = 2)
    protected BigDecimal price;

    @NotNull
    @Column(name = "TAXES", nullable = false, precision = 12, scale = 2)
    protected BigDecimal taxes;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_ITEM_ID")
    protected ProductItem productItem;

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }


    public BigDecimal getTaxes() {
        return taxes;
    }


    public BigDecimal getPrice() {
        return price;
    }


    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public ProductItem getProductItem() {
        return productItem;
    }


}