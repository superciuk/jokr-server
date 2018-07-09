package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRODUCT_ITEM")
@Entity(name = "jokerapp$ProductItem")
public class ProductItem extends StandardEntity {
    private static final long serialVersionUID = 6093572631839975153L;

    @NotNull
    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected ProductItemCategory category;

    @Column(name = "VISIBLE")
    protected Boolean visible;

    public ProductItemCategory getCategory() {
        return category;
    }

    public void setCategory(ProductItemCategory category) {
        this.category = category;
    }


    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getVisible() {
        return visible;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}