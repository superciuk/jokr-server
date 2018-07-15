package com.joker.jokerapp.web.productitemcategory;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.joker.jokerapp.entity.ProductItemCategory;

import java.util.Map;

public class ProductItemCategoryEdit extends AbstractEditor<ProductItemCategory> {


    @Override
    public void init(Map<String, Object> params) {
    }

    @Override
    protected void initNewItem(ProductItemCategory item) {
        super.initNewItem(item);

        item.setSortOrder(100);
    }
}