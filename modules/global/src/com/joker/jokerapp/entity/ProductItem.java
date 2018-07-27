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
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRODUCT_ITEM")
@Entity(name = "jokerapp$ProductItem")
public class ProductItem extends StandardEntity {
    private static final long serialVersionUID = 6093572631839975153L;

    @NotNull
    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @Column(name = "SORT_ORDER")
    protected Integer sortOrder;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected ProductItemCategory category;

    @NotNull
    @Column(name = "PRICE", nullable = false)
    protected Double price;

    @Column(name = "VISIBLE")
    protected Boolean visible;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "productItem")
    protected List<ProductItemModifierCategoryAssoc> productItemModifierCategoryAssocs;

    public void setProductItemModifierCategoryAssocs(List<ProductItemModifierCategoryAssoc> productItemModifierCategoryAssocs) {
        this.productItemModifierCategoryAssocs = productItemModifierCategoryAssocs;
    }

    public List<ProductItemModifierCategoryAssoc> getProductItemModifierCategoryAssocs() {
        return productItemModifierCategoryAssocs;
    }


    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }


    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }


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