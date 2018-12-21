package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.*;
import com.joker.jokerapp.web.dialogs.ItemManualModifierDialog;
import com.joker.jokerapp.web.dialogs.ItemModifierDialog;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;

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
    private Metadata metadata;

    @Inject
    protected HtmlAttributes html;

    @Named("categoriesGrid")
    private GridLayout categoriesGrid;

    @Named("itemsGrid")
    private GridLayout itemsGrid;

    @Named("orderLineDataGrid")
    private DataGrid orderLineDataGrid;

    String categoryBtnWidth = "180px";
    String categoryBtnHeight = "120px";

    String itemBtnWidth = "180px";
    String itemBtnHeight = "120px";

    private Order currentOrder = null;
    private TableItem table;
    private List <OrderLine> modifierOrderLinesToAdd = new ArrayList<>();

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        table = dataManager.load(TableItem.class)
                .query("select e from jokerapp$TableItem e where e.tableNumber = :tableNumber")
                .parameter("tableNumber", params.get("tableNumber"))
                .view("tableItem-view")
                .one();

        if (table.getTableStatus().equals(TableItemStatus.free)) {
            currentOrder = metadata.create(Order.class);
            currentOrder.setStatus(OrderStatus.open);
            currentOrder.setTableItemNumber((Integer)params.get("tableNumber"));
            currentOrder.setActualSeats((Integer)params.get("actualSeats"));
            table.setCurrentOrder(currentOrder);
            table.setTableStatus(TableItemStatus.open);

        } else if (table.getTableStatus().equals(TableItemStatus.open)) {
            currentOrder = table.getCurrentOrder();
        }

        if (currentOrder != null) {
            List<OrderLine> lines = dataManager.load(OrderLine.class)
                    .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.position")
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

        for (ProductItem productItem : productItemsDs.getItems()) {

            if (productItem.getCategory().getName().equals(productItemCategory.getName()) && productItem.getVisible()) {

                WebButton btn = componentsFactory.createComponent(WebButton.class);
                btn.setWidth(itemBtnWidth);
                btn.setHeight(itemBtnHeight);
                btn.setCaption(productItem.getName());
                btn.setAction(new BaseAction("addToOrder".concat(productItem.getName())).withHandler(e -> addToOrder(productItem)));
                itemsGrid.add(btn);

            }

        }

    }

    private void addToOrder(ProductItem productItemToAdd) {

        for (OrderLine line : orderLinesDs.getItems()) {

            if ((line.getItemName().equals(productItemToAdd.getName())) && !line.getHasModifier()) {

                line.setQuantity(line.getQuantity() + 1);
                line.setPrice(line.getPrice().add(productItemToAdd.getPrice()));

                orderLinesDs.commit();

                return;

            }

        }

            int max = 0;

            for (OrderLine line : orderLinesDs.getItems()) {

                if (!line.getIsModifier() && line.getPosition() > max) {

                    max = line.getPosition();

                }

            }

            max += 100;

            OrderLine newLine = metadata.create(OrderLine.class);

            newLine.setQuantity(1);
            newLine.setItemName(productItemToAdd.getName());
            newLine.setUnitPrice(productItemToAdd.getPrice());
            newLine.setPrice(productItemToAdd.getPrice());
            newLine.setTaxes(BigDecimal.ZERO);
            newLine.setOrder(currentOrder);
            newLine.setPosition(max);
            newLine.setNextModifierPosition(max+1);
            newLine.setHasModifier(Boolean.FALSE);
            newLine.setIsModifier(Boolean.FALSE);
            newLine.setItemToModifyId(null);
            newLine.setIsSended(Boolean.FALSE);

            orderLinesDs.addItem(newLine);
            orderLinesDs.commit();

    }

    public void onPrintBtnClick() {

        PrintService printService = PrinterJob.lookupPrintServices()[0];

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

    public void onAddModifierClick() {

        if (orderLineDataGrid.getSelected().size() > 1 || orderLineDataGrid.getSingleSelected() == null) {
            return;
        }

        OrderLine selectedLine = (OrderLine) orderLineDataGrid.getSingleSelected();

        if (selectedLine.getIsModifier()) return;

        List <OrderLine> modifierOrderLines = new ArrayList<>();

        if (selectedLine.getHasModifier()) {

            modifierOrderLines = dataManager.load(OrderLine.class)
                    .query("select e from jokerapp$OrderLine e where e.itemToModifyId = :selectedLineId")
                    .parameter("selectedLineId", selectedLine.getId())
                    .view("order-line-view")
                    .list();

        }

        Map<String, Object> params = new HashMap<>();

        ItemModifierDialog.CloseHandler handler = new ItemModifierDialog.CloseHandler() {

            @Override
            public void onClose(List<OrderLine> newModifierOrderLines) {

                if (newModifierOrderLines == null) return;

                modifierOrderLinesToAdd = newModifierOrderLines;

            }

        };

        params.put("handler", handler);
        params.put("selectedLine", selectedLine);
        params.put("modifierOrderLines", modifierOrderLines);

        openWindow("jokerapp$ItemModifier.dialog", WindowManager.OpenType.DIALOG, params).addCloseListener(closeString -> {

            if (closeString.equals("ok")) {

                for (OrderLine line : orderLinesDs.getItems()) {

                    if (line.getIsModifier() &&
                            line.getItemToModifyId().equals(selectedLine.getId()) &&
                                !modifierOrderLinesToAdd.contains(line)) {

                                        selectedLine.setPrice(selectedLine.getPrice().subtract(line.getPrice()));

                                        orderLinesDs.removeItem(line);

                    }

                }

                if (modifierOrderLinesToAdd.isEmpty() && selectedLine.getHasModifier().equals(Boolean.TRUE)) {

                    selectedLine.setHasModifier(Boolean.FALSE);

                } else {

                    for (OrderLine newModifierLine : modifierOrderLinesToAdd) {

                        Boolean modifierAlreadyExist = Boolean.FALSE;

                        for (OrderLine line : orderLinesDs.getItems()) {

                            if (!line.getItemName().equals(selectedLine.getItemName()) &&
                                    line.getItemName().equals(newModifierLine.getItemName()) &&
                                    line.getItemToModifyId().equals(selectedLine.getId())) {

                                if (!line.getQuantity().equals(newModifierLine.getQuantity())) {

                                    int newQuantity = newModifierLine.getQuantity() - line.getQuantity();

                                    line.setQuantity(newModifierLine.getQuantity());
                                    line.setPrice(line.getUnitPrice().multiply(BigDecimal.valueOf(newModifierLine.getQuantity())));

                                    selectedLine.setPrice(selectedLine.getPrice().add(line.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity))));

                                }

                                modifierAlreadyExist = Boolean.TRUE;

                            }

                        }

                        if (modifierAlreadyExist.equals(Boolean.FALSE)) {

                            selectedLine.setPrice(selectedLine.getPrice().
                                    add(newModifierLine.getPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));

                            orderLinesDs.addItem(newModifierLine);

                        }

                    }

                }

                orderLinesDs.commit();

                orderLinesDs.clear();

                List<OrderLine> lines = dataManager.load(OrderLine.class)
                        .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.position")
                        .parameter("currentOrder", currentOrder.getId())
                        .view("order-line-view")
                        .list();

                for (OrderLine line : lines) {

                    orderLinesDs.includeItem(line);

                }

            }

        });

    }

    public void onAddManualModifierClick() {

        if (orderLineDataGrid.getSelected().size() > 1 || orderLineDataGrid.getSingleSelected() == null) {
            return;
        }

        OrderLine selectedLine = (OrderLine) orderLineDataGrid.getSingleSelected();

        if (selectedLine.getIsModifier()) return;

        OrderLine newModifierLine = metadata.create(OrderLine.class);

        Map<String, Object> params = new HashMap<>();

        ItemManualModifierDialog.CloseHandler handler = new ItemManualModifierDialog.CloseHandler() {

            @Override
            public void onClose(String itemName,BigDecimal itemModifierPrice) {

                if (itemName == null) return;
                newModifierLine.setItemName("  * ".concat(itemName));
                newModifierLine.setUnitPrice(itemModifierPrice);
                newModifierLine.setPrice(itemModifierPrice);

            }

        };

        params.put("handler", handler);

        openWindow("jokerapp$ItemManualModifier.dialog", WindowManager.OpenType.DIALOG, params).addCloseListener(closeString -> {

            if (closeString.equals("ok") && newModifierLine.getItemName() != null ) {

                Boolean modifierAlreadyExist = Boolean.FALSE;

                for (OrderLine line : orderLinesDs.getItems()) {

                    if ((!line.getItemName().equals(selectedLine.getItemName())) &&
                            line.getItemName().equals(newModifierLine.getItemName())) {

                        line.setQuantity(line.getQuantity()+1);
                        line.setPrice(line.getPrice().add(newModifierLine.getPrice()));
                        modifierAlreadyExist = Boolean.TRUE;

                    }

                }

                if (modifierAlreadyExist == Boolean.FALSE) {

                    newModifierLine.setQuantity(1);
                    newModifierLine.setTaxes(BigDecimal.ZERO);
                    newModifierLine.setOrder(currentOrder);
                    newModifierLine.setPosition(selectedLine.getNextModifierPosition());
                    selectedLine.setNextModifierPosition(selectedLine.getNextModifierPosition()+1);
                    newModifierLine.setHasModifier(Boolean.FALSE);
                    selectedLine.setHasModifier(Boolean.TRUE);
                    newModifierLine.setIsModifier(Boolean.TRUE);
                    newModifierLine.setItemToModifyId(selectedLine.getId());
                    newModifierLine.setIsSended(Boolean.FALSE);
                    orderLinesDs.addItem(newModifierLine);

                }

                selectedLine.setPrice(selectedLine.getPrice().
                        add(newModifierLine.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));

                orderLinesDs.commit();

                orderLinesDs.clear();

                List<OrderLine> lines = dataManager.load(OrderLine.class)
                        .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.position")
                        .parameter("currentOrder", currentOrder.getId())
                        .view("order-line-view")
                        .list();

                for (OrderLine line : lines) {

                    orderLinesDs.includeItem(line);

                }

            }

        });

    }

    public void onAddBtnClick() {

        if (orderLineDataGrid.getSelected().size() > 1 || orderLineDataGrid.getSingleSelected() == null) {
            return;
        }

        OrderLine selectedLine = (OrderLine) orderLineDataGrid.getSingleSelected();

        if (selectedLine.getIsModifier()) return;

        BigDecimal itemSinglePrice = selectedLine.getPrice().divide(BigDecimal.valueOf(selectedLine.getQuantity()));

        selectedLine.setQuantity(selectedLine.getQuantity()+1);
        selectedLine.setPrice(selectedLine.getPrice().add(itemSinglePrice));

        orderLinesDs.commit();

    }

    public void onSubtractBtnClick() {

        if (orderLineDataGrid.getSelected().size() > 1 || orderLineDataGrid.getSingleSelected() == null) {
            return;
        }

        OrderLine selectedLine = (OrderLine) orderLineDataGrid.getSingleSelected();

        if (selectedLine.getIsModifier()) return;

        if (selectedLine.getHasModifier()) {

            if (selectedLine.getQuantity().equals(1)) {

                List<OrderLine> toRemove = new ArrayList();

                for (OrderLine line: orderLinesDs.getItems()) {

                    if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId())) {

                        toRemove.add(line);

                    }
                }

                for (OrderLine line : toRemove) orderLinesDs.removeItem(line);
                orderLinesDs.removeItem(selectedLine);
                orderLinesDs.commit();

                return;

            }

            for (OrderLine line: orderLinesDs.getItems()) {

                if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId())) {

                        selectedLine.setPrice(selectedLine.getPrice().subtract(line.getPrice()));

                    }

                }

        }

        if (selectedLine.getQuantity().equals(1)) {

            orderLinesDs.removeItem(selectedLine);

            orderLinesDs.commit();

            } else {

            selectedLine.setQuantity(selectedLine.getQuantity() - 1);
            selectedLine.setPrice(selectedLine.getPrice().subtract(selectedLine.getUnitPrice()));

            orderLinesDs.commit();

        }

    }

    public void onRemoveBtnClick() {

        if (orderLineDataGrid.getSelected().size() > 0) {

            List<OrderLine> toRemove = new ArrayList();

            for (OrderLine lineToRemove: (Set<OrderLine>) orderLineDataGrid.getSelected()) {

                if (lineToRemove.getIsModifier()) {

                    OrderLine orderLineModified = orderLinesDs.getItem(lineToRemove.getItemToModifyId());
                    orderLineModified.setPrice(orderLineModified.getPrice().subtract(lineToRemove.getPrice().
                            multiply(BigDecimal.valueOf(orderLineModified.getQuantity()))));

                    toRemove.add(lineToRemove);

                    Boolean modifiedItemHasMoreModifier = Boolean.FALSE;

                    for (OrderLine line: orderLinesDs.getItems()) {

                        if ((line.getItemToModifyId() != null) && line.getItemToModifyId().equals(orderLineModified.getId()))
                            if (!toRemove.contains(line)) modifiedItemHasMoreModifier = Boolean.TRUE;

                    }

                    if (modifiedItemHasMoreModifier.equals(Boolean.FALSE)) {

                        for (OrderLine line: orderLinesDs.getItems()) {

                            if ((line != orderLineModified) && line.getItemName().equals(orderLineModified.getItemName())) {

                                if (line.getHasModifier()) {

                                    orderLineModified.setHasModifier(Boolean.FALSE);

                                } else {

                                    line.setQuantity(line.getQuantity() + orderLineModified.getQuantity());
                                    line.setPrice(line.getPrice().add(orderLineModified.getPrice()));

                                    toRemove.add(orderLineModified);

                                }


                            }

                        }

                    }

                } else if (lineToRemove.getHasModifier()) {

                    for (OrderLine line: orderLinesDs.getItems()) {

                        if ((line.getItemToModifyId() != null) && line.getItemToModifyId().equals(lineToRemove.getId()))
                            toRemove.add(line);

                    }

                    toRemove.add(lineToRemove);

                } else toRemove.add(lineToRemove);

            }

            for (OrderLine line : toRemove) orderLinesDs.removeItem(line);
            orderLinesDs.commit();

        } else {

            showOptionDialog("warning", "Please select an item to remove",MessageType.WARNING,
                    new Action[] {
                            new DialogAction(DialogAction.Type.OK)});

        }

    }

    public void onSaveBtnClick() {

        dataManager.commit(table,currentOrder);
        getWindowManager().close(this);
    }

}