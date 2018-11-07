package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.Order;
import com.joker.jokerapp.entity.OrderLine;
import com.joker.jokerapp.entity.ProductItem;
import com.joker.jokerapp.entity.ProductItemCategory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class OrderScreen extends AbstractEditor<Order> {


    @Inject
    private CollectionDatasource<ProductItemCategory, UUID> productItemCategoriesDs;

    @Inject
    private CollectionDatasource<ProductItem, UUID> productItemsDs;

    @Inject
    private CollectionDatasource<com.joker.jokerapp.entity.OrderLine, UUID> orderLinesDs;

    @Inject
    private Datasource<com.joker.jokerapp.entity.Order> orderDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("categoriesGrid")
    private GridLayout categoriesGrid;

    @Named("itemsGrid")
    private GridLayout itemsGrid;

    @Named("orderDataGrid")
    private DataGrid orderDataGrid;

    @Inject
    private Metadata metadata;

    Integer categoryBtnWidth = 180;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        productItemCategoriesDs.refresh();
        Float categoriesGridHeight = categoriesGrid.getHeight();
        Float categoriesGridWidth = categoriesGrid.getWidth();

        for (ProductItemCategory productItemCategory : productItemCategoriesDs.getItems()) {

            WebButton btn = componentsFactory.createComponent(WebButton.class);
            btn.setHeight("60px");
            btn.setWidth(categoryBtnWidth.toString());
            btn.setCaption(productItemCategory.getName());
            btn.setAction(new BaseAction("showItem".concat(productItemCategory.getName())).withHandler(e -> showProductItems(productItemCategory)));
            categoriesGrid.add(btn);

        }

    }

    private void showProductItems(ProductItemCategory productItemCategory) {

        itemsGrid.removeAll();
        productItemsDs.refresh();
        Float itemsGridHeight = itemsGrid.getHeight();
        Float itemsGridWidth = itemsGrid.getWidth();
        Float itemBtnWidth = (itemsGridWidth + 100);

        for (ProductItem productItem : productItemsDs.getItems()) {

            if (productItem.getCategory().getName().equals(productItemCategory.getName()) && productItem.getVisible()) {
                WebButton btn = componentsFactory.createComponent(WebButton.class);
                btn.setHeight("60px");
                btn.setWidth(itemBtnWidth.toString());
                btn.setCaption(productItem.getName());
                btn.setAction(new BaseAction("addToOrder".concat(productItemCategory.getName())).withHandler(e -> addToOrder(productItem)));
                itemsGrid.add(btn);
            }

        }

    }

    private void addToOrder(ProductItem productItemToAdd) {

        OrderLine newLine = metadata.create(OrderLine.class);
//        newLine.setName(productItemToAdd.getName());
        newLine.setPrice(productItemToAdd.getPrice());
        newLine.setOrder(orderDs.getItem());
        orderLinesDs.addItem(newLine);
        //orderLinesDs.commit();
    }



}
