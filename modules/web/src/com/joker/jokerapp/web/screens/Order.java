package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
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

    @Named("categoryBtnPanel")
    private ButtonsPanel categoryBtnPanel;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        productItemCategoriesDs.refresh();

        for (ProductItemCategory productItemCategory: productItemCategoriesDs.getItems()) {


            WebButton btn = componentsFactory.createComponent(WebButton.class);
            btn.setCaption(productItemCategory.getName());
            categoryBtnPanel.add(btn);

        }

    }
}