package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderScreen extends AbstractWindow {


    @Inject
    private CollectionDatasource<ProductItemCategory, UUID> productItemCategoriesDs;

    @Inject
    private CollectionDatasource<ProductItem, UUID> productItemsDs;

    @Inject
    private CollectionDatasource<OrderLine, UUID> orderLinesDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private DataManager dataManager;

    @Named("categoriesGrid")
    private GridLayout categoriesGrid;

    @Named("itemsGrid")
    private GridLayout itemsGrid;

    @Named("orderLineDataGrid")
    private DataGrid orderLineDataGrid;

    @Inject
    private Metadata metadata;

    Integer categoryBtnWidth = 180;

    private Order currentOrder;
    private TableItem table;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        currentOrder = metadata.create(Order.class);

        table = dataManager.load(TableItem.class)
                .query("select e from jokerapp$TableItem e where e.tableNumber = :tableNumber")
                .parameter("tableNumber", params.get("tableNumber"))
                .view("tableItem-view")
                .one();

        if (table.getTableStatus() == TableItemStatus.free) {

            currentOrder.setStatus(OrderStatus.open);
            currentOrder.setTableItemNumber((Integer)params.get("tableNumber"));
            currentOrder.setActualSeats((Integer)params.get("actualSeats"));
            table.setCurrentOrder(currentOrder);
            table.setTableStatus(TableItemStatus.open);

        } else if (table.getTableStatus() == TableItemStatus.open) {

            currentOrder = dataManager.load(Order.class)
                    .query("select e from jokerapp$Order e where e.id = :id")
                    .parameter("id", table.getCurrentOrder())
                    .view("order-view")
                    .one();
        }

        List<OrderLine> lines = dataManager.load(OrderLine.class)
                .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.createTs")
                .parameter("currentOrder", currentOrder.getId())
                .view("order-line-view")
                .list();

        for (OrderLine line : lines) {

            orderLinesDs.includeItem(line);

        }

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

        newLine.setItemName(productItemToAdd.getName());
        newLine.setPrice(productItemToAdd.getPrice());
        newLine.setTaxes(BigDecimal.ONE);
        newLine.setOrder(currentOrder);

        orderLinesDs.addItem(newLine);

    }

    public void onRemoveBtnClick() {

        if (orderLineDataGrid.getSingleSelected() != null) {

            orderLinesDs.removeItem((OrderLine)orderLineDataGrid.getSingleSelected());

        } else {

            showOptionDialog("warning", "Please select an item to remove",MessageType.WARNING,
                    new Action[] {
                    new DialogAction(DialogAction.Type.OK)});

        }

    }

    public void onSaveBtnClick() {

        dataManager.commit(table,currentOrder);
        orderLinesDs.commit();
        getWindowManager().close(this);
    }

}