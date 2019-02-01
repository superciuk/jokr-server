package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.*;
import com.joker.jokerapp.web.dialogs.ItemPriceManualModifierDialog;
import com.joker.jokerapp.web.dialogs.ItemManualModifierDialog;
import com.joker.jokerapp.web.dialogs.ItemModifierDialog;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.awt.print.*;

import java.io.File;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
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

    @Named("categoriesGrid")
    private GridLayout categoriesGrid;

    @Named("itemsGrid")
    private GridLayout itemsGrid;

    @Named("orderLineScrollBox")
    private ScrollBoxLayout orderLineScrollBox;

    @Named("categoriesBackBtn")
    private Button categoriesBackBtn;

    @Named("categoriesNextBtn")
    private Button categoriesNextBtn;

    @Named("itemsBackBtn")
    private Button itemsBackBtn;

    @Named("itemsNextBtn")
    private Button itemsNextBtn;

    @Named("tableTimeField")
    private TimeField tableTimeField;

    @Named("subtotalLabel")
    private Label subtotalLabel;

    @Named("serviceLabel")
    private Label serviceLabel;

    @Named("totalLabel")
    private Label totalLabel;

    @Named("subtotalField")
    private TextField subtotalField;

    @Named("serviceField")
    private TextField serviceField;

    @Named("totalField")
    private TextField totalField;

    @Named("orderLinesScrollUp")
    private Button orderLinesScrollUp;

    @Named("orderLinesScrollDown")
    private Button orderLinesScrollDown;

    @Named("doNotPrintBtn")
    private Button doNotPrintBtn;


    private String categoryBtnWidth = "180px";
    private String categoryBtnHeight = "120px";

    private String itemBtnWidth = "180px";
    private String itemBtnHeight = "120px";

    private Order currentOrder;
    private TableItem table;
    private List <OrderLine> modifierOrderLinesToAdd = new ArrayList<>();

    private BigDecimal subTotal = new BigDecimal(0.0);
    private BigDecimal service = new BigDecimal(0.0);
    private BigDecimal total = new BigDecimal(0.0);

    private String printerGroupToSendTicket;

    private int categorySize = 0;
    private int categoriesPages = 0;
    private int categoriesActualPage = 1;

    private int productItemSize = 0;
    private int productItemsPages = 0;
    private int productItemsActualPage = 1;

    private ArrayList<Integer> spaceToConvert = new ArrayList<>();

    private ArrayList <ProductItemCategory> productItemCategoriesToShow = new ArrayList<>();

    private ArrayList <ProductItem> productItemsToShow = new ArrayList<>();

    private ArrayList <OrderLine> orderLines = new ArrayList<>();

    private int orderLinesBegin = 1;

    private Boolean doNotPrint = Boolean.FALSE;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        categoriesBackBtn.setStyleName("v-button-special");
        categoriesNextBtn.setStyleName("v-button-special");
        itemsBackBtn.setStyleName("v-button-special");
        itemsNextBtn.setStyleName("v-button-special");
        subtotalLabel.setStyleName("subtotalLabel");
        serviceLabel.setStyleName("serviceLabel");
        totalLabel.setStyleName("totalLabel");
        
        subtotalField.setStyleName("subtotalField");
        serviceField.setStyleName("serviceField");
        totalField.setStyleName("totalField");

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
            currentOrder.setCharge(BigDecimal.valueOf(0));
            currentOrder.setTaxes(BigDecimal.valueOf(0));
            if (table.getWithServiceByDefault()) currentOrder.setWithService(Boolean.TRUE);
                else currentOrder.setWithService(Boolean.FALSE);

            table.setCurrentOrder(currentOrder);
            table.setTableStatus(TableItemStatus.open);

            dataManager.commit(currentOrder,table);

            currentOrder = dataManager.load(Order.class)
                    .query("select e from jokerapp$Order e where e.id = :currentOrder")
                    .parameter("currentOrder", table.getCurrentOrder())
                    .view("order-view")
                    .one();


        } else if (table.getTableStatus().equals(TableItemStatus.open)) {

            currentOrder = dataManager.load(Order.class)
                    .query("select e from jokerapp$Order e where e.id = :currentOrder")
                    .parameter("currentOrder", table.getCurrentOrder())
                    .view("order-view")
                    .one();

        }

        Long tableTime = Instant.now().getEpochSecond()-currentOrder.getCreateTs().toInstant().getEpochSecond();

        if (tableTime<=3600) tableTimeField.setStyleName("tableTimeField-normal");
        else tableTimeField.setStyleName("tableTimeField-hot");

        tableTimeField.setValue(Date.from(Instant.ofEpochSecond(tableTime-3600)));

        Timer clockTimer = componentsFactory.createTimer();
        addTimer(clockTimer);
        clockTimer.setDelay(5000);
        clockTimer.setRepeating(true);
        clockTimer.addActionListener(timer -> {

            if (Instant.now().getEpochSecond() - currentOrder.getCreateTs().toInstant().getEpochSecond()<=3600) tableTimeField.setStyleName("tableTimeField-normal");
            else tableTimeField.setStyleName("tableTimeField-hot");
            tableTimeField.setValue(Date.from(Instant.ofEpochSecond(Instant.now().getEpochSecond() - currentOrder.getCreateTs().toInstant().getEpochSecond() - 3600)));

        });

        clockTimer.start();

        List<OrderLine> lines = dataManager.load(OrderLine.class)
                .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.position")
                .parameter("currentOrder", currentOrder.getId())
                .view("order-line-view")
                .list();

        for (OrderLine line : lines) {

            orderLinesDs.includeItem(line);

        }

        drawOrderLinesGrid();

        refreshBill();

        itemsBackBtn.setVisible(Boolean.FALSE);
        itemsNextBtn.setVisible(Boolean.FALSE);

        categoriesGrid.removeAll();
        productItemCategoriesDs.refresh();
        categoriesActualPage = 1;
        productItemCategoriesToShow.clear();

        for (ProductItemCategory item : productItemCategoriesDs.getItems())
            if (item.getVisible()) productItemCategoriesToShow.add(item);

        if (productItemCategoriesDs!=null) categorySize = productItemCategoriesDs.getItems().size();

        categoriesPages = (categorySize - 1) / 10 + 1;

        if (categorySize!=0) {

            if (categorySize<=10) {

                categoriesBackBtn.setVisible(Boolean.FALSE);
                categoriesNextBtn.setVisible(Boolean.FALSE);
                showProductCategories(0, categorySize-1);

            } else {

                categoriesBackBtn.setVisible(Boolean.FALSE);
                categoriesNextBtn.setVisible(Boolean.TRUE);
                showProductCategories(0, 9);

            }

        }

    }

    private void showProductCategories(int start, int end) {

        for (int c = start; c <= end; c++) {

            WebButton cBtn = componentsFactory.createComponent(WebButton.class);

            cBtn.setWidth(categoryBtnWidth);
            cBtn.setHeight(categoryBtnHeight);
            cBtn.setCaptionAsHtml(Boolean.TRUE);

            int numberOfRow;

            int maxLineLength = 0;

            String categoryName = productItemCategoriesToShow.get(c).getName();

            if (Math.floorMod(categoryName.length(),10)==0) numberOfRow = Math.floorDiv(categoryName.length(),10);
            else numberOfRow = Math.floorDiv(categoryName.length(),10) + 1;

            int exactLineLength = Math.floorDiv(categoryName.length(), numberOfRow);

            if (categoryName.length() > exactLineLength && categoryName.contains(" ")) {

                int actualSpace = 0;
                int prevSpaceConverted = 0;

                spaceToConvert.clear();
                maxLineLength = 0;

                for (int l = 0; l < categoryName.length(); l++) {

                    Character ch = categoryName.charAt(l);

                    if (Character.isSpace(ch) || l == categoryName.length()-1) {

                        if (l - prevSpaceConverted > exactLineLength) {

                            if (actualSpace != 0 && prevSpaceConverted != actualSpace && (actualSpace - prevSpaceConverted <= l - actualSpace || l - prevSpaceConverted > 10)) {

                                spaceToConvert.add(actualSpace);
                                if (actualSpace - prevSpaceConverted > maxLineLength) maxLineLength = actualSpace - prevSpaceConverted;
                                prevSpaceConverted = actualSpace;

                            } else {

                                if (!(l==categoryName.length()-1)) {

                                    spaceToConvert.add(l);
                                    if (l - prevSpaceConverted > maxLineLength) maxLineLength = l - prevSpaceConverted;
                                    prevSpaceConverted = l;

                                }

                            }

                        }

                        actualSpace = l;

                    }

                }

                for (int n = 0; n < spaceToConvert.size(); n++) {

                    categoryName = categoryName.substring(0, (n * 3) + spaceToConvert.get(n)).concat("<br>").concat(categoryName.substring((n * 3) + spaceToConvert.get(n) + 1));

                }

            }

            cBtn.setCaption(categoryName);

            if (maxLineLength <= 10 && spaceToConvert.size()<3) cBtn.setStyleName("v-button-fontSize30");
            else cBtn.setStyleName("v-button-fontSize20");

            ProductItemCategory toShow = productItemCategoriesToShow.get(c);
            cBtn.setAction(new BaseAction("showItem".concat(productItemCategoriesToShow.get(c).getName())).withHandler(e -> showProductItems(toShow)));

            categoriesGrid.add(cBtn);

        }

    }

    private void showProductItems(ProductItemCategory productItemCategory) {

        itemsGrid.removeAll();
        productItemsDs.refresh();
        productItemsActualPage = 1;
        productItemsToShow.clear();

        for (ProductItem item : productItemsDs.getItems()) {

            if (item.getCategory().getName().equals(productItemCategory.getName()) && item.getVisible()) productItemsToShow.add(item);

        }

        if (productItemsToShow!=null) productItemSize = productItemsToShow.size();

        productItemsPages = (productItemSize - 1) / 20 + 1;

        if (productItemSize!=0) {

            if (productItemSize <= 20) {

                itemsBackBtn.setVisible(Boolean.FALSE);
                itemsNextBtn.setVisible(Boolean.FALSE);
                showProductItemsPaged(0, productItemSize-1);

            } else {

                itemsBackBtn.setVisible(Boolean.FALSE);
                itemsNextBtn.setVisible(Boolean.TRUE);
                showProductItemsPaged(0, 19);

            }

        }

    }

    private void showProductItemsPaged(int start, int end) {

        for (int c = start; c<=end; c++) {

            WebButton pBtn = componentsFactory.createComponent(WebButton.class);
            pBtn.setWidth(itemBtnWidth);
            pBtn.setHeight(itemBtnHeight);
            pBtn.setCaptionAsHtml(Boolean.TRUE);

            int numberOfRow;

            int maxLineLength = 0;

            String productName = productItemsToShow.get(c).getName();

            if (Math.floorMod(productName.length(),14)==0) numberOfRow = Math.floorDiv(productName.length(),14);
            else numberOfRow = Math.floorDiv(productName.length(),14) + 1;

            int exactLineLength = Math.floorDiv(productName.length(), numberOfRow);

            if (productName.length() > exactLineLength && productName.contains(" ")) {

                int actualSpace = 0;
                int prevSpaceConverted = 0;

                spaceToConvert.clear();
                maxLineLength = 0;

                for (int l = 0; l < productName.length(); l++) {

                    Character ch = productName.charAt(l);

                    if (Character.isSpace(ch) || l == productName.length()-1) {

                        if (l - prevSpaceConverted > exactLineLength) {

                            if (actualSpace != 0 && prevSpaceConverted != actualSpace && (actualSpace - prevSpaceConverted <= l - actualSpace || l - prevSpaceConverted > 14)) {

                                spaceToConvert.add(actualSpace);
                                if (actualSpace - prevSpaceConverted > maxLineLength) maxLineLength = actualSpace - prevSpaceConverted;
                                prevSpaceConverted = actualSpace;

                            } else {

                                if (!(l==productName.length()-1)) {

                                    spaceToConvert.add(l);
                                    if (l - prevSpaceConverted > maxLineLength) maxLineLength = l - prevSpaceConverted;
                                    prevSpaceConverted = l;

                                }

                            }

                        }

                        actualSpace = l;

                    }

                }

                for (int n = 0; n < spaceToConvert.size(); n++) {

                    productName = productName.substring(0, (n * 3) + spaceToConvert.get(n)).concat("<br>").concat(productName.substring((n * 3) + spaceToConvert.get(n) + 1));

                }

            }

            pBtn.setCaption(productName);

            if (maxLineLength <= 14 && spaceToConvert.size()<4) pBtn.setStyleName("v-button-fontSize20");
            else pBtn.setStyleName("v-button-fontSize16");

            ProductItem toAdd = productItemsToShow.get(c);
            pBtn.setAction(new BaseAction("addToOrder".concat(productItemsToShow.get(c).getName())).withHandler(e -> addToOrder(toAdd)));
            itemsGrid.add(pBtn);

            }

        }

    private void addToOrder(ProductItem productItemToAdd) {

        for (OrderLine line : orderLinesDs.getItems()) {

            if ((line.getItemName().equals(productItemToAdd.getName())) && !line.getHasModifier() && !line.getIsSended()) {

                line.setQuantity(line.getQuantity() + 1);
                line.setPrice(line.getPrice().add(productItemToAdd.getPrice()));

                orderLinesDs.commit();

                drawOrderLinesGrid();

                refreshBill();

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
            newLine.setItemId(productItemToAdd.getId());
            newLine.setUnitPrice(productItemToAdd.getPrice());
            newLine.setPrice(productItemToAdd.getPrice());
            newLine.setTaxes(BigDecimal.ZERO);
            newLine.setOrder(currentOrder);
            newLine.setPosition(max);
            newLine.setNextModifierPosition(max+1);
            newLine.setHasModifier(Boolean.FALSE);
            newLine.setIsModifier(Boolean.FALSE);
            newLine.setItemToModifyId(null);
            newLine.setPrinterGroup(productItemToAdd.getPrinterGroup().toString());
            newLine.setIsSended(Boolean.FALSE);
            newLine.setIsSelected(Boolean.FALSE);

            orderLinesDs.addItem(newLine);

            orderLinesDs.commit();

            if (orderLinesDs.size()>20) orderLinesBegin = orderLinesDs.size()-19;

            drawOrderLinesGrid();

            refreshBill();

    }

    public void onAddModifierClick() {

        orderLines.clear();

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected())

                if (orderLines.size()==0) orderLines.add(line);
                else return;

        }

        if (orderLines.size()==0) return;

        OrderLine selectedLine = orderLines.get(0);

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

                ArrayList<OrderLine> toRemove = new ArrayList<>();

                for (OrderLine line : orderLinesDs.getItems()) {

                    if (line.getIsModifier() &&
                            line.getItemToModifyId().equals(selectedLine.getId()) &&
                                !modifierOrderLinesToAdd.contains(line)) {

                                        selectedLine.setPrice(selectedLine.getPrice().subtract(line.getPrice()));

                                        toRemove.add(line);

                    }

                }

                Iterator<OrderLine> iterator = toRemove.iterator();

                while (iterator.hasNext()) orderLinesDs.removeItem(iterator.next());

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

                selectedLine.setIsSelected(Boolean.FALSE);
                orderLinesDs.commit();

                refreshBill();

                orderLinesDs.clear();

                List<OrderLine> lines = dataManager.load(OrderLine.class)
                        .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.position")
                        .parameter("currentOrder", currentOrder.getId())
                        .view("order-line-view")
                        .list();

                for (OrderLine line : lines) {

                    orderLinesDs.includeItem(line);

                }

                drawOrderLinesGrid();

            }

        });

    }

    public void onAddManualModifierClick() {

        orderLines.clear();

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected())

                if (orderLines.size()==0) orderLines.add(line);
                else return;

        }

        if (orderLines.size()==0) return;

        OrderLine selectedLine = orderLines.get(0);

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
                    newModifierLine.setPrinterGroup(selectedLine.getPrinterGroup());
                    newModifierLine.setIsSended(Boolean.FALSE);
                    newModifierLine.setIsSelected(Boolean.FALSE);
                    orderLinesDs.addItem(newModifierLine);

                }

                selectedLine.setPrice(selectedLine.getPrice().
                        add(newModifierLine.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));

                selectedLine.setIsSelected(Boolean.FALSE);

                orderLinesDs.commit();

                refreshBill();

                orderLinesDs.clear();

                List<OrderLine> lines = dataManager.load(OrderLine.class)
                        .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.position")
                        .parameter("currentOrder", currentOrder.getId())
                        .view("order-line-view")
                        .list();

                for (OrderLine line : lines) {

                    orderLinesDs.includeItem(line);

                }

                drawOrderLinesGrid();

            }

        });

    }

    public void onAddBtnClick() {

        orderLines.clear();

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected())

                if (orderLines.size()==0) orderLines.add(line);
                else return;

        }

        if (orderLines.size()==0) return;

        OrderLine selectedLine = orderLines.get(0);

        if (selectedLine.getIsModifier()) return;

        if (selectedLine.getIsSended()) {

            productItemsDs.refresh();
            addToOrder(productItemsDs.getItem(selectedLine.getItemId()));

        } else {

            BigDecimal SingleModifiersPrice = (selectedLine.getPrice().subtract(selectedLine.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity())))
                    .divide(BigDecimal.valueOf(selectedLine.getQuantity()), RoundingMode.FLOOR));

            selectedLine.setQuantity(selectedLine.getQuantity()+1);
            selectedLine.setPrice(selectedLine.getPrice().add(selectedLine.getUnitPrice()).add(SingleModifiersPrice));

            orderLinesDs.commit();

            drawOrderLinesGrid();

            refreshBill();

        }

    }

    public void onSubtractBtnClick() {

        orderLines.clear();

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected())

                if (orderLines.size()==0) orderLines.add(line);
                else return;

        }

        if (orderLines.size()==0) return;

        OrderLine selectedLine = orderLines.get(0);

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

                drawOrderLinesGrid();

                refreshBill();

                return;

            }

            for (OrderLine line: orderLinesDs.getItems()) {

                if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId())) {

                        selectedLine.setPrice(selectedLine.getPrice().subtract(line.getUnitPrice()));

                }

            }

        }

        if (selectedLine.getQuantity().equals(1)) {

            orderLinesDs.removeItem(selectedLine);
            orderLinesDs.commit();

            drawOrderLinesGrid();

            refreshBill();

            } else {

            selectedLine.setQuantity(selectedLine.getQuantity() - 1);
            selectedLine.setPrice(selectedLine.getPrice().subtract(selectedLine.getUnitPrice()));

            orderLinesDs.commit();

            drawOrderLinesGrid();

            refreshBill();

        }

    }

    public void onRemoveBtnClick() {

        orderLines.clear();

        int initialSize = orderLinesDs.size();

        for (OrderLine line: orderLinesDs.getItems()) if (line.getIsSelected()) orderLines.add(line);

        if (orderLines.size() > 0) {

            List<OrderLine> toRemove = new ArrayList();

            for (OrderLine lineToRemove: orderLines) {

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

            for (int i=0; i< (initialSize-orderLinesDs.size()); i++) {

                if (orderLinesDs.size() < orderLinesBegin + 19 && orderLinesBegin > 1) orderLinesBegin -= 1;
                if (orderLinesBegin==1) break;

            }

            orderLinesDs.commit();

            drawOrderLinesGrid();

            refreshBill();

        } else {

            showOptionDialog("warning", "Please select an item to remove",MessageType.WARNING,
                    new Action[] {
                            new DialogAction(DialogAction.Type.OK)});

        }

    }

    private void refreshBill() {

        subTotal = BigDecimal.ZERO;

        for (OrderLine line : orderLinesDs.getItems()) {

            if (line.getOrder().getId().equals(currentOrder.getId())) {

                if (!line.getIsModifier()) subTotal = subTotal.add(line.getPrice());

            }

        }

        subtotalField.setValue(subTotal.toString().concat(" €"));

        if (currentOrder.getWithService()) {

            service = BigDecimal.valueOf(Math.round(subTotal.multiply(BigDecimal.valueOf(0.1)).subtract(BigDecimal.valueOf(0.2)).
                    multiply(BigDecimal.valueOf(2)).doubleValue()) / 2.0f).setScale(2);

            total = subTotal.add(service);

            serviceField.setValue(service.toString().concat(" €"));
            totalField.setValue(total.toString().concat(" €"));

        } else totalField.setValue(subTotal.toString().concat(" €"));

        currentOrder = dataManager.load(Order.class)
                .query("select e from jokerapp$Order e where e.id = :currentOrder")
                .parameter("currentOrder", table.getCurrentOrder())
                .view("order-view")
                .one();

        currentOrder.setCharge(subTotal);
        if (currentOrder.getWithService()) currentOrder.setTaxes(service);

        dataManager.commit(currentOrder);

    }

    class Ticket implements Printable {

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

            if (pageIndex == 0) {

                Font font1 = new Font("ZapfDingbats", Font.BOLD, 18);
                Font font2 = new Font("ZapfDingbats", Font.PLAIN, 14);
                Font font3 = new Font("ZapfDingbats", Font.BOLD, 12);

                int xMin = (int) pageFormat.getImageableX()+1;
                int y = 20;

                int yInc = 12;


                Graphics2D graphics2D = (Graphics2D) graphics;
                graphics2D.setFont(font1);
                graphics2D.drawString(printerGroupToSendTicket, xMin, y);
                y += 30;
                graphics2D.drawString("TAVOLO ".concat(table.getTableNumber().toString()), xMin, y);
                y += 30;
                graphics2D.setFont(font3);
                graphics2D.drawString("Coperti: ".concat(currentOrder.getActualSeats().toString()), xMin, y);
                y += 20;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                graphics2D.drawString("Time: ".concat(sdf.format(cal.getTime())), xMin, y);
                y += 30;
                graphics2D.setFont(font2);

                for (OrderLine line : orderLinesDs.getItems()) {

                    if (line.getOrder().equals(currentOrder) && line.getPrinterGroup().equals(printerGroupToSendTicket) && !line.getIsSended()) {

                        if (!line.getIsModifier()) graphics2D.drawString(line.getQuantity().toString(),xMin, y);

                        Integer linesToDraw = Math.round(line.getItemName().length()/24) + 1 ;

                        int spacePosition = 0;
                        int currentSpacePosition = 0;

                        for (int l=1; l<linesToDraw; l++) {

                            String lineName = line.getItemName();

                            for (int i = spacePosition; i<line.getItemName().length(); i++) {

                                Character c = lineName.charAt(i);

                                if (Character.isSpace(c)) {

                                    if (i > 24*l) break;

                                    spacePosition = i;

                                }

                            }

                            graphics2D.drawString(lineName.substring(currentSpacePosition, spacePosition),xMin+font2.getSize(), y);

                            currentSpacePosition = spacePosition;

                            y = y + yInc + 4;

                        }

                        graphics2D.drawString(line.getItemName().substring(currentSpacePosition),xMin+font2.getSize(), y);

                    y = y + yInc + 6;

                    }

                }

                graphics2D.drawString(".", xMin, y);

                return Printable.PAGE_EXISTS;
            }

            return Printable.NO_SUCH_PAGE;

        }

    }

    class Bill implements Printable {

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

            if (pageIndex == 0) {

                Font font1 = new Font("ZapfDingbats", Font.BOLD, 20);
                Font font2 = new Font("ZapfDingbats", Font.PLAIN, 10);
                Font font3 = new Font("ZapfDingbats", Font.BOLD, 11);

                int x;
                int xMin = (int) pageFormat.getImageableX()+1;
                int y = 20;
                int paperWidth = (int) pageFormat.getImageableWidth();

                int yInc1 = font1.getSize()/2;
                int yInc2 = font1.getSize()/2;
                int yInc3 = font1.getSize()/2;

                Graphics2D graphics2D = (Graphics2D) graphics;

                BufferedImage bufferedImage = null;

                try {

                    bufferedImage = ImageIO.read(new File("/home/joker/Desktop/logo3.jpg"));

                } catch (Exception e) { System.err.println(e); }

                graphics2D.drawImage(bufferedImage, null, 30, -10);

                y += 60;
                graphics2D.setFont(font2);
                graphics2D.drawString("PRECONTO TAVOLO: ".concat(currentOrder.getTableItemNumber().toString()), xMin, y);
                y += 20;

                graphics2D.drawLine(xMin, y, paperWidth, y);

                y = y + 2*yInc2;

                for (OrderLine line : orderLinesDs.getItems()) {

                    if (line.getOrder().equals(currentOrder)) {

                        if (!line.getIsModifier()) {

                            graphics2D.drawString(line.getQuantity().toString(),xMin, y);

                        }

                        Integer linesToDraw = Math.round(line.getItemName().length()/24) + 1 ;

                        String stringToDraw = "";

                        int spacePosition = 0;
                        int currentSpacePosition = 0;

                        for (int l=1; l<linesToDraw; l++) {

                            String lineName = line.getItemName();

                            for (int i = spacePosition; i<line.getItemName().length(); i++) {

                                Character c = lineName.charAt(i);

                                if (Character.isSpace(c)) {

                                    if (i > 24*l) break;

                                    spacePosition = i;

                                }

                            }

                            graphics2D.drawString(lineName.substring(currentSpacePosition, spacePosition),xMin+font2.getSize(), y);

                            currentSpacePosition = spacePosition;

                            if (l==1 && !line.getIsModifier()) {

                                x = paperWidth - Math.multiplyExact(line.getPrice().toString().length(), font2.getSize() - 3);
                                graphics2D.drawString(line.getPrice().toString(), x, y);

                            }

                            y = y + yInc2 + 1;

                        }

                        graphics2D.drawString(line.getItemName().substring(currentSpacePosition),xMin+font2.getSize(), y);

                        if (currentSpacePosition == 0 && !line.getIsModifier()) {

                            x = paperWidth - Math.multiplyExact(line.getPrice().toString().length(), font2.getSize() - 3);
                            graphics2D.drawString(line.getPrice().toString(), x, y);

                        }

                        y = y + yInc2 + 4;

                    }

                }

                graphics2D.drawLine(xMin, y, paperWidth, y);
                y = y + 2*yInc2;
                graphics2D.setFont(font3);
                graphics2D.drawString("SUBTOTALE", xMin, y);
                x = paperWidth - Math.multiplyExact(currentOrder.getCharge().toString().length(), font3.getSize()-3);
                graphics2D.drawString(currentOrder.getCharge().toString(), x, y);
                y = y + yInc3 +3;
                graphics2D.drawString("SERVIZIO", xMin, y);
                x = paperWidth - Math.multiplyExact(currentOrder.getTaxes().toString().length(), font3.getSize()-3);
                graphics2D.drawString(currentOrder.getTaxes().toString(), x, y);
                y = y + yInc3 +20;

                graphics2D.setFont(font1);

                graphics2D.drawString("TOTALE", xMin, y);
                x = paperWidth -2 - Math.multiplyExact(currentOrder.getCharge().add(currentOrder.getTaxes()).toString().length(), font1.getSize()-7);
                graphics2D.drawString(currentOrder.getCharge().add(currentOrder.getTaxes()).toString(), x, y);
                y = y + yInc3 +10;

                graphics2D.setFont(font2);

                graphics2D.drawString("Coperti: ".concat(currentOrder.getActualSeats().toString()), xMin, y);
                y = y + 2*yInc3;
                graphics2D.setFont(font2);
                graphics2D.drawString("NON FISCALE", 60, y);

                return Printable.PAGE_EXISTS;
            }

            return Printable.NO_SUCH_PAGE;

        }

    }

    public void onCategoriesBackBtnClick() {

        categoriesGrid.removeAll();

        categoriesActualPage--;

        if (categoriesActualPage>1) {

            categoriesBackBtn.setVisible(Boolean.TRUE);
            categoriesNextBtn.setVisible(Boolean.TRUE);
            showProductCategories((categoriesActualPage-1)*10, ((categoriesActualPage-1)*10)+9);

        } else {

            categoriesActualPage = 1;
            categoriesBackBtn.setVisible(Boolean.FALSE);
            categoriesNextBtn.setVisible(Boolean.TRUE);
            showProductCategories(0, 9);

        }

    }

    public void onCategoriesNextBtnClick() {

        categoriesGrid.removeAll();

        categoriesActualPage++;

        if (categoriesActualPage>1 && categoriesActualPage<categoriesPages) {

            categoriesBackBtn.setVisible(Boolean.TRUE);
            categoriesNextBtn.setVisible(Boolean.TRUE);
            showProductCategories((categoriesActualPage-1)*10, ((categoriesActualPage-1)*10)+9);

        } else {

            categoriesBackBtn.setVisible(Boolean.TRUE);
            categoriesNextBtn.setVisible(Boolean.FALSE);
            showProductCategories((categoriesActualPage-1)*10, categorySize-1);

        }

    }

    public void onItemsBackBtnClick() {

        itemsGrid.removeAll();

        productItemsActualPage--;

        if (productItemsActualPage>1) {

            itemsBackBtn.setVisible(Boolean.TRUE);
            itemsNextBtn.setVisible(Boolean.TRUE);
            showProductItemsPaged((productItemsActualPage-1)*20, ((productItemsActualPage-1)*20)+19);

        } else {

            productItemsActualPage = 1;
            itemsBackBtn.setVisible(Boolean.FALSE);
            itemsNextBtn.setVisible(Boolean.TRUE);
            showProductItemsPaged(0, 19);

        }

    }

    public void onItemsNextBtnClick() {

        itemsGrid.removeAll();

        productItemsActualPage++;

        if (productItemsActualPage>1 && productItemsActualPage<productItemsPages) {

            itemsBackBtn.setVisible(Boolean.TRUE);
            itemsNextBtn.setVisible(Boolean.TRUE);
            showProductItemsPaged((productItemsActualPage-1)*20, ((productItemsActualPage-1)*20)+19);

        } else {

            itemsBackBtn.setVisible(Boolean.TRUE);
            itemsNextBtn.setVisible(Boolean.FALSE);
            showProductItemsPaged((productItemsActualPage-1)*20, productItemSize-1);

        }

    }

    public void onModifyPriceBtnClick() {

        orderLines.clear();

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected())

                if (orderLines.size()==0) orderLines.add(line);
                else return;

        }

        if (orderLines.size()==0) return;

        OrderLine selectedLine = orderLines.get(0);

        if (selectedLine.getIsModifier()) return;

        Map<String, Object> params = new HashMap<>();

        ItemPriceManualModifierDialog.CloseHandler handler = new ItemPriceManualModifierDialog.CloseHandler() {

            @Override
            public void onClose(BigDecimal newPrice) {

                params.put("newPrice", newPrice);

                }
            };

        params.put("handler", handler);
        openWindow("jokerapp$itemPriceManualModifier.dialog", WindowManager.OpenType.DIALOG, params).addCloseListener(closeString -> {

            if (closeString.equals("ok")) {

                BigDecimal newPrice = (BigDecimal)params.get("newPrice");

                if (selectedLine.getHasModifier()) {

                    BigDecimal modifierPrice = new BigDecimal(0);

                    for (OrderLine line : orderLinesDs.getItems()) {

                        if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId())) {

                            modifierPrice = modifierPrice.add(line.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity())));

                        }

                    }

                    try {

                        selectedLine.setUnitPrice(newPrice.subtract(modifierPrice).divide(BigDecimal.valueOf(selectedLine.getQuantity()), RoundingMode.FLOOR));
                        selectedLine.setPrice(newPrice);

                    } catch (ArithmeticException a) {

                        a.printStackTrace();

                    }

                }

                orderLinesDs.commit();

                drawOrderLinesGrid();

                refreshBill();

            }

        });

    }

    private void drawOrderLinesGrid () {

        orderLineScrollBox.removeAll();

        if (orderLinesBegin == 1) orderLinesScrollUp.setVisible(Boolean.FALSE);
        else orderLinesScrollUp.setVisible(Boolean.TRUE);
        if (orderLinesDs.size()-orderLinesBegin<=19) orderLinesScrollDown.setVisible(Boolean.FALSE);
        else orderLinesScrollDown.setVisible(Boolean.TRUE);

        int index = 1;

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            if (index>=orderLinesBegin && index<=orderLinesBegin+19) {

                HBoxLayout hBoxLayout= componentsFactory.createComponent(HBoxLayout.class);

                Label quantity = componentsFactory.createComponent(Label.class);
                Button itemName = componentsFactory.createComponent(Button.class);
                Label price = componentsFactory.createComponent(Label.class);

                quantity.setWidth("35px");
                itemName.setWidth("480px");
                price.setWidth("55px");

                quantity.setHeight("30px");
                itemName.setHeight("30px");
                price.setHeight("30px");

                itemName.setAction(new BaseAction("selectCurrentLine") {

                    @Override
                    public boolean isPrimary() {

                        return true;

                    }

                    @Override
                    public void actionPerform(Component component) {

                        if (!orderLine.getIsSelected()) {

                            orderLine.setIsSelected(Boolean.TRUE);

                            if (orderLine.getIsSended()) {

                                if (orderLine.getIsModifier()) {

                                    quantity.setStyleName("label-quantity-selected-isModifier-isSended");
                                    itemName.setStyleName("button-itemName-selected-isModifier-isSended");
                                    price.setStyleName("label-price-selected-isModifier-isSended");

                                } else {

                                    quantity.setStyleName("label-quantity-selected-isSended");
                                    itemName.setStyleName("button-itemName-selected-isSended");
                                    price.setStyleName("label-price-selected-isSended");

                                }

                            } else {

                                if (orderLine.getIsModifier()) {

                                    quantity.setStyleName("label-quantity-selected-isModifier");
                                    itemName.setStyleName("button-itemName-selected-isModifier");
                                    price.setStyleName("label-price-selected-isModifier");

                                } else {

                                    quantity.setStyleName("label-quantity-selected");
                                    itemName.setStyleName("button-itemName-selected");
                                    price.setStyleName("label-price-selected");

                                }

                            }

                        } else {

                            orderLine.setIsSelected(Boolean.FALSE);

                            if (orderLine.getIsSended()) {

                                if (orderLine.getIsModifier()) {

                                    quantity.setStyleName("label-quantity-isModifier-isSended");
                                    itemName.setStyleName("button-itemName-isModifier-isSended");
                                    price.setStyleName("label-price-isModifier-isSended");

                                } else {

                                    quantity.setStyleName("label-quantity-isSended");
                                    itemName.setStyleName("button-itemName-isSended");
                                    price.setStyleName("label-price-isSended");

                                }

                            } else {

                                if (orderLine.getIsModifier()) {

                                    quantity.setStyleName("label-quantity-isModifier");
                                    itemName.setStyleName("button-itemName-isModifier");
                                    price.setStyleName("label-price-isModifier");

                                } else {

                                    quantity.setStyleName("label-quantity");
                                    itemName.setStyleName("button-itemName");
                                    price.setStyleName("label-price");

                                }

                            }

                        }

                    }

                });

                if (orderLine.getIsSelected()) {

                    if (orderLine.getIsSended()) {

                        if (orderLine.getIsModifier()) {

                            quantity.setStyleName("label-quantity-selected-isModifier-isSended");
                            itemName.setStyleName("button-itemName-selected-isModifier-isSended");
                            price.setStyleName("label-price-selected-isModifier-isSended");

                        } else {

                            quantity.setStyleName("label-quantity-selected-isSended");
                            itemName.setStyleName("button-itemName-selected-isSended");
                            price.setStyleName("label-price-selected-isSended");

                        }

                    } else {

                        if (orderLine.getIsModifier()) {

                            quantity.setStyleName("label-quantity-selected-isModifier");
                            itemName.setStyleName("button-itemName-selected-isModifier");
                            price.setStyleName("label-price-selected-isModifier");

                        } else {

                            quantity.setStyleName("label-quantity-selected");
                            itemName.setStyleName("button-itemName-selected");
                            price.setStyleName("label-price-selected");

                        }

                    }

                } else {

                    if (orderLine.getIsSended()) {

                        if (orderLine.getIsModifier()) {

                            quantity.setStyleName("label-quantity-isModifier-isSended");
                            itemName.setStyleName("button-itemName-isModifier-isSended");
                            price.setStyleName("label-price-isModifier-isSended");

                        } else {

                            quantity.setStyleName("label-quantity-isSended");
                            itemName.setStyleName("button-itemName-isSended");
                            price.setStyleName("label-price-isSended");

                        }

                    } else {

                        if (orderLine.getIsModifier()) {

                            quantity.setStyleName("label-quantity-isModifier");
                            itemName.setStyleName("button-itemName-isModifier");
                            price.setStyleName("label-price-isModifier");

                        } else {

                            quantity.setStyleName("label-quantity");
                            itemName.setStyleName("button-itemName");
                            price.setStyleName("label-price");

                        }

                    }

                }

                if (!orderLine.getIsModifier()) quantity.setValue(orderLine.getQuantity());
                itemName.setCaption(orderLine.getItemName());
                if (orderLine.getIsModifier() && !orderLine.getPrice().toString().equals("0.00")) {

                    if (orderLine.getPrice().toString().charAt(0)=='-') price.setValue("(".concat(orderLine.getPrice().toString()).concat(")"));
                    else price.setValue("(+".concat(orderLine.getPrice().toString()).concat(")"));

                } else if (!orderLine.getIsModifier()) price.setValue(orderLine.getPrice().setScale(2).toString());

                hBoxLayout.add(quantity);
                hBoxLayout.add(itemName);
                hBoxLayout.add(price);

                orderLineScrollBox.add(hBoxLayout);

            }

            index+=1;

        }

    }

    public void onOrderLinesScrollUpClick() {

        orderLinesBegin -=1;

        drawOrderLinesGrid();

    }

    public void onOrderLinesScrollDownClick() {

        orderLinesBegin +=1;

        drawOrderLinesGrid();

    }

    public void onSendAndCloseBtnClick() {

        if (!doNotPrint) printTickets();

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSended(true);
            orderLine.setIsSelected(false);

        }

        orderLinesDs.commit();

        getWindowManager().close(this);
        openWindow("tableselect", WindowManager.OpenType.THIS_TAB);

    }

    public void onSendBtnClick() {

        if (!doNotPrint) printTickets();

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSended(true);

        }

        orderLinesDs.commit();

        drawOrderLinesGrid();

    }

    public void onReSendBtnClick() {

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSended(false);

        }

        if (!doNotPrint) printTickets();

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSended(true);

        }

        orderLinesDs.commit();

        drawOrderLinesGrid();

    }

    public void onBillBtnClick() {

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);

        if (printServices[0] != null) {

            MediaPrintableArea mpa = new MediaPrintableArea(1,1,74,2000,MediaPrintableArea.MM);

            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.ISO_A0);
            printRequestAttributeSet.add(mpa);

            DocAttributeSet docAttributeSet = new HashDocAttributeSet();
            docAttributeSet.add(MediaSizeName.ISO_A0);

            docAttributeSet.add(mpa);

            Bill bill = new Bill();

            DocPrintJob docPrintJob = printServices[0].createPrintJob();
            SimpleDoc doc1 = new SimpleDoc(bill, flavor, docAttributeSet);

            try {

                docPrintJob.print(doc1, printRequestAttributeSet);

            } catch (PrintException e) {

                e.printStackTrace();

            }

        }


    }

    public void onDoNotPrintBtnClick() {

        if (doNotPrint) {

            doNotPrintBtn.setStyleName("doNotPrintBtnNotPushed");
            doNotPrintBtn.setCaption("PRINT");
            doNotPrint = Boolean.FALSE;

        } else {

            doNotPrintBtn.setStyleName("doNotPrintBtnPushed");
            doNotPrintBtn.setCaption("NO<br>PRINT");
            doNotPrint = Boolean.TRUE;
        }

    }

    public void printTickets() {

        for (PrinterGroup printerGroup : PrinterGroup.values()) {

            printerGroupToSendTicket = printerGroup.toString();

            Boolean printerGroupLinesExixts = Boolean.FALSE;

            for (OrderLine line : orderLinesDs.getItems()) {

                if (line.getOrder().equals(currentOrder) && line.getPrinterGroup().equals(printerGroupToSendTicket) && !line.getIsSended())
                    printerGroupLinesExixts = Boolean.TRUE;

            }

            if (printerGroupLinesExixts) {

                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

                PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);

                if (printServices[0] != null) {

                    MediaPrintableArea mpa = new MediaPrintableArea(1,1,74,2000,MediaPrintableArea.MM);

                    PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                    printRequestAttributeSet.add(MediaSizeName.ISO_A0);
                    printRequestAttributeSet.add(mpa);

                    DocAttributeSet docAttributeSet = new HashDocAttributeSet();
                    docAttributeSet.add(MediaSizeName.ISO_A0);

                    docAttributeSet.add(mpa);

                    Ticket ticket = new Ticket();

                    DocPrintJob docPrintJob = printServices[0].createPrintJob();
                    SimpleDoc doc1 = new SimpleDoc(ticket, flavor, docAttributeSet);

                    try {

                        docPrintJob.print(doc1, printRequestAttributeSet);

                    } catch (PrintException e) {

                        e.printStackTrace();

                    }

                }

            }

        }

    }

}