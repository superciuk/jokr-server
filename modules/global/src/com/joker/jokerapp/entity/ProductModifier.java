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
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import java.math.BigDecimal;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRODUCT_MODIFIER")
@Entity(name = "jokerapp$ProductModifier")
public class ProductModifier extends StandardEntity {
    private static final long serialVersionUID = 1886051332521846708L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "ADD_PRICE", nullable = false)
    protected BigDecimal addPrice;

    @NotNull
    @Column(name = "SUBTRACT_PRICE", nullable = false)
    protected BigDecimal subtractPrice;

    @Lookup(type = LookupType.DROPDOWN)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected ProductModifierCategory category;

    @Column(name = "SORT_ORDER")
    protected Integer sortOrder;


    public BigDecimal getAddPrice() {
        return addPrice;
    }

    public void setAddPrice(BigDecimal addPrice) {
        this.addPrice = addPrice;
    }


    public BigDecimal getSubtractPrice() {
        return subtractPrice;
    }

    public void setSubtractPrice(BigDecimal subtractPrice) {
        this.subtractPrice = subtractPrice;
    }










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



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}