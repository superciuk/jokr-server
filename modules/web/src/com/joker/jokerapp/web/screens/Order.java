package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.ProductItem;
import com.joker.jokerapp.entity.ProductItemCategory;
import com.joker.jokerapp.entity.TableItem;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class Order extends AbstractWindow {


    @Inject
    private CollectionDatasource<ProductItemCategory, UUID> productItemCategoriesDs;

    @Inject
    private CollectionDatasource<ProductItem, UUID> productItemsDs;

    @Inject
    private CollectionDatasource<com.joker.jokerapp.entity.Order, UUID> ordersDs;

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

    Integer categoryBtnWidth = 180;

    TableItem table = new TableItem();

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        table = (TableItem) params.get("table");
        productItemCategoriesDs.refresh();
        orderDs.refresh();
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


        com.joker.jokerapp.entity.Order item = new com.joker.jokerapp.entity.Order();
        item.setItemName(productItemToAdd.getName());
        item.setItemPrice(productItemToAdd.getPrice());
        item.setOrderId(table);
        ordersDs.addItem(item);
        ordersDs.commit();


//        Integer columns = ((int) orderDataGrid.getWidth()*3) / (categoryBtnWidth+5);
//        Integer rowCount = 0;

//        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
//        hBoxLayout.setWidth(Integer.toString((180+5)*columns));
//        hBoxLayout.setHeight("60px");



//        for (ProductItemCategory productItemCategory : productItemCategoriesDs.getItems()) {


//            WebButton btn = componentsFactory.createComponent(WebButton.class);
//            btn.setHeight("60px");
//            btn.setWidth(categoryBtnWidth.toString());
//            btn.setCaption(productItemCategory.getName());
//            btn.setAction(new BaseAction("showItem".concat(productItemCategory.getName())).withHandler(e -> showProductItems(productItemCategory)));
//            if (rowCount < columns) {
//                hBoxLayout.add(btn);
//                rowCount ++;
//            } else {
//                orderDataGrid.add(componentsFactory.createComponent(HBoxLayout.class));
//                rowCount = 0;
            }

}
