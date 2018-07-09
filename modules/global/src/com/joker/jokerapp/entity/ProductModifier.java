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

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRODUCT_MODIFIER")
@Entity(name = "jokerapp$ProductModifier")
public class ProductModifier extends StandardEntity {
    private static final long serialVersionUID = 1886051332521846708L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Column(name = "SORT_ORDER")
    protected Integer sortOrder;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected ProductModifierCategory category;

    @Column(name = "PRICE")
    protected Integer price;

    public ProductModifierCategory getCategory() {
        return category;
    }

    public void setCategory(ProductModifierCategory category) {
        this.category = category;
    }


    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }


    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}