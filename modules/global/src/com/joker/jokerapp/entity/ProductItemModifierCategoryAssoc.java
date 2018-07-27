package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;

@Table(name = "JOKERAPP_PRODUCT_ITEM_MODIFIER_CATEGORY_ASSOC")
@Entity(name = "jokerapp$ProductItemModifierCategoryAssoc")
public class ProductItemModifierCategoryAssoc extends StandardEntity {
    private static final long serialVersionUID = -86873518909824558L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_ITEM_ID")
    protected ProductItem productItem;

    @Lookup(type = LookupType.DROPDOWN)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_MODIFIER_CATEGORY_ID")
    protected ProductModifierCategory productModifierCategory;

    @NotNull
    @Column(name = "MIN_NUMBER", nullable = false)
    protected Integer minNumber;

    @NotNull
    @Column(name = "MAX_NUMBER", nullable = false)
    protected Integer maxNumber;

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public ProductItem getProductItem() {
        return productItem;
    }


    public void setProductModifierCategory(ProductModifierCategory productModifierCategory) {
        this.productModifierCategory = productModifierCategory;
    }

    public ProductModifierCategory getProductModifierCategory() {
        return productModifierCategory;
    }

    public void setMinNumber(Integer minNumber) {
        this.minNumber = minNumber;
    }

    public Integer getMinNumber() {
        return minNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }



}