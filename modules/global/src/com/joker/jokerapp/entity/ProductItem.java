package com.joker.jokerapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import java.math.BigDecimal;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRODUCT_ITEM")
@Entity(name = "jokerapp$ProductItem")
public class ProductItem extends StandardEntity {
    private static final long serialVersionUID = 6093572631839975153L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true, length = 100)
    protected String name;

    @Column(name = "SORT_ORDER")
    protected Integer sortOrder;

    @Lookup(type = LookupType.DROPDOWN)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected ProductItemCategory category;

    @Column(name = "VISIBLE")
    protected Boolean visible;

    @NotNull
    @Column(name = "PRICE", nullable = false, precision = 12, scale = 2)
    protected BigDecimal price;

    @JoinTable(name = "JOKERAPP_PRODUCT_ITEM_PRODUCT_MODIFIER_CATEGORY_LINK",
            joinColumns = @JoinColumn(name = "PRODUCT_ITEM_ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_MODIFIER_CATEGORY_ID"))
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToMany
    protected List<ProductModifierCategory> modifierCategories;

    @Column(name = "PRINTER_GROUP")
    protected String printerGroup;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    protected FileDescriptor image;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileDescriptor getImage() {
        return image;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public PrinterGroup getPrinterGroup() {
        return printerGroup == null ? null : PrinterGroup.fromId(printerGroup);
    }

    public void setPrinterGroup(PrinterGroup printerGroup) {
        this.printerGroup = printerGroup == null ? null : printerGroup.getId();
    }

    public void setModifierCategories(List<ProductModifierCategory> modifierCategories) {
        this.modifierCategories = modifierCategories;
    }

    public List<ProductModifierCategory> getModifierCategories() {
        return modifierCategories;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
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