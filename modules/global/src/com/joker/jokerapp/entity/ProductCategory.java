package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRODUCT_CATEGORY")
@Entity(name = "jokerapp$ProductCategory")
public class ProductCategory extends StandardEntity {
    private static final long serialVersionUID = 7736488913065101229L;

    @Column(name = "NAME", nullable = false, length = 120)
    protected String name;

    @Column(name = "VISIBLE", nullable = false)
    protected Boolean visible = false;

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

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

}