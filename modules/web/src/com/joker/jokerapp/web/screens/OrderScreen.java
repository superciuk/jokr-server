package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.*;
import javafx.print.Printer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.print.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

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

    @Inject
    protected HtmlAttributes html;

    @Named("categoriesGrid")
    private GridLayout categoriesGrid;

    @Named("itemsGrid")
    private GridLayout itemsGrid;

    @Named("orderLineDataGrid")
    private DataGrid orderLineDataGrid;

    @Inject
    private Metadata metadata;

    String categoryBtnWidth = "180px";
    String categoryBtnHeight = "120px";

    String itemBtnWidth = "180px";
    String itemBtnHeight = "120px";

    private Order currentOrder = null;
    private TableItem table;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        table = dataManager.load(TableItem.class)
                .query("select e from jokerapp$TableItem e where e.tableNumber = :tableNumber")
                .parameter("tableNumber", params.get("tableNumber"))
                .view("tableItem-view")
                .one();

        if (table.getTableStatus() == TableItemStatus.free) {
            currentOrder = metadata.create(Order.class);
            currentOrder.setStatus(OrderStatus.open);
            currentOrder.setTableItemNumber((Integer)params.get("tableNumber"));
            currentOrder.setActualSeats((Integer)params.get("actualSeats"));
            table.setCurrentOrder(currentOrder);
            table.setTableStatus(TableItemStatus.open);

        } else if (table.getTableStatus() == TableItemStatus.open) {
            currentOrder = table.getCurrentOrder();
        }

        if (currentOrder != null) {
            List<OrderLine> lines = dataManager.load(OrderLine.class)
                    .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.createTs")
                    .parameter("currentOrder", currentOrder.getId())
                    .view("order-line-view")
                    .list();

            for (OrderLine line : lines) {

                orderLinesDs.includeItem(line);

            }
        }

        productItemCategoriesDs.refresh();

        categoriesGrid.setColumns(2);

        Integer btnNumber = 1;

        for (ProductItemCategory productItemCategory : productItemCategoriesDs.getItems()) {

            WebButton btn = componentsFactory.createComponent(WebButton.class);

            btn.setId("btn".concat(btnNumber.toString()));
//            html.setCssProperty(categoriesGrid, HtmlAttributes.CSS.BACKGROUND_COLOR , "red");
            btn.setWidth(categoryBtnWidth);
            btn.setHeight(categoryBtnHeight);
            btn.setCaptionAsHtml(Boolean.TRUE);

            Integer nameLength = productItemCategory.getName().length();
            String categoryName = productItemCategory.getName();
            if (nameLength>16 && categoryName.contains(" ")) {

                categoryName = categoryName.replace(" ", "<br>");
                btn.setCaption(categoryName);

            } else btn.setCaption(categoryName);

            btn.setAction(new BaseAction("showItem".concat(productItemCategory.getName())).withHandler(e -> showProductItems(productItemCategory)));

            categoriesGrid.add(btn);

            btnNumber ++;

        }

    }

    private void showProductItems(ProductItemCategory productItemCategory) {

        itemsGrid.removeAll();
        itemsGrid.setColumns(4);
        productItemsDs.refresh();
//        Float itemsGridHeight = itemsGrid.getHeight();
//        Float itemsGridWidth = itemsGrid.getWidth();

        for (ProductItem productItem : productItemsDs.getItems()) {

            if (productItem.getCategory().getName().equals(productItemCategory.getName()) && productItem.getVisible()) {

                WebButton btn = componentsFactory.createComponent(WebButton.class);
                btn.setWidth(itemBtnWidth);
                btn.setHeight(itemBtnHeight);
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

        if (orderLineDataGrid.getSelected().size() > 0) {

            for (Object line: orderLineDataGrid.getSelected()) {
                orderLinesDs.removeItem((OrderLine) line);
            }

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

    public void onPrintBtnClick() {

        PrintService printService = PrinterJob.lookupPrintServices()[0];

         //Prova di testo formattato

        AttributedString atString = new AttributedString("PROVA");
        Font normalFont = new Font ("serif", Font.PLAIN, 18);
        Font boldFont = new Font ("serif", Font.BOLD, 12);

        atString.addAttribute(TextAttribute.FONT, normalFont);

        String printString = null;

        printString = "          TAVOLO ".concat(table.getTableNumber().toString()).concat("\n\n\n");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        printString = printString.concat("Time: ").concat(sdf.format(cal.getTime())).concat("\n\n");

        for (OrderLine line : orderLinesDs.getItems()) {

            if (line.getItemName().length() > 28) {

                String lineName = line.getItemName();
                Integer spacePosition = 0;

                for (int i = 0;i<line.getItemName().length();i++) {

                    Character c = lineName.charAt(i);

                    if (Character.isSpace(c)) {

                        if (i>28) break;

                        spacePosition = i;

                    }

                }

                printString = printString.concat(lineName.substring(0,spacePosition)).concat("\n ").concat(lineName.substring(spacePosition).concat("\n\n"));

            } else printString = printString.concat(line.getItemName().concat("\n\n"));

        }

        Doc ticket= new SimpleDoc(printString, DocFlavor.STRING.TEXT_PLAIN, null);


        try {

            printService.createPrintJob().print(ticket, null);

        } catch (PrintException e) {

            e.printStackTrace();

        }

    }

}