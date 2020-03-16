package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRODUCT_MODIFIER_CATEGORY")
@Entity(name = "jokerapp$ProductModifierCategory")
public class ProductModifierCategory extends StandardEntity {
    private static final long serialVersionUID = -6619597231071001995L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Column(name = "SORT_ORDER")
    protected Integer sortOrder;

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}