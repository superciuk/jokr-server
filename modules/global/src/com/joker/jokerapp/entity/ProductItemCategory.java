package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRODUCT_ITEM_CATEGORY")
@Entity(name = "jokerapp$ProductItemCategory")
public class ProductItemCategory extends StandardEntity {
    private static final long serialVersionUID = 7736488913065101229L;

    @Column(name = "NAME", nullable = false, length = 120)
    protected String name;

    @Column(name = "VISIBLE", nullable = false)
    protected Boolean visible = false;

    @Column(name = "IS_BEVERAGE", nullable = false)
    protected Boolean isBeverage = false;

    @Column(name = "SORT_ORDER", nullable = false)
    protected Integer sortOrder;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setIsBeverage(Boolean isBeverage) { this.isBeverage = isBeverage; }

    public Boolean getIsBeverage() {
        return isBeverage;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

}