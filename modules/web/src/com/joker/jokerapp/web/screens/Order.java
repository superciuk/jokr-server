package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.ProductItemCategory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class Order extends AbstractWindow {


    @Inject
    private CollectionDatasource<ProductItemCategory, UUID> productItemCategoriesDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("categoryGrid")
    private GridLayout categoryGrid;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        productItemCategoriesDs.refresh();
        Float categoryBtnPanelHeight = categoryGrid.getHeight();
        Float categoryBtnPanelWidth = categoryGrid.getWidth();
        Float btnWidth = (categoryBtnPanelWidth+80);

        for (ProductItemCategory productItemCategory: productItemCategoriesDs.getItems()) {


            WebButton btn = componentsFactory.createComponent(WebButton.class);
            btn.setHeight("60px");
            btn.setWidth(btnWidth.toString());
            btn.setCaption(productItemCategory.getName());
            btn.setAction(Action showItems);
            categoryGrid.add(btn);

        }

    }

    public Action showItems(String id) {
        return super.getAction(id);
    }
}