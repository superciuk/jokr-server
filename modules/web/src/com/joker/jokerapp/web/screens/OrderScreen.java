package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AbstractUserSessionSource;
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
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.SessionAttribute;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.haulmont.cuba.web.gui.components.WebScrollBoxLayout;
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
    private UserSession userSession;

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

    @Named("categoriesScrollBox")
    private ScrollBoxLayout categoriesScrollBox;

    @Named("productItemScrollBox")
    private ScrollBoxLayout productItemScrollBox;

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
    private String categoryBtnHeight = "140px";

    private String itemBtnWidth = "180px";
    private String itemBtnHeight = "160px";

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

    private int orderLinesBegin = 1;

    private Boolean doNotPrint = false;

    private Boolean withFries = false;
    private Boolean isGrillTicket = false;

    private Boolean clientIsTablet = false;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        if (userSession.getUser().getLogin().equals("tablet")) {

            clientIsTablet = true;

        }

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

        doNotPrintBtn.setStyleName("doNotPrintBtnNotPushed");
        doNotPrintBtn.setCaption("STAMPA<br>LE COMANDE");

        table = dataManager.load(TableItem.class)
                .query("select e from jokerapp$TableItem e where e.tableCaption = :tableCaption")
                .parameter("tableCaption", params.get("tableCaption"))
                .view("tableItem-view")
                .one();

        if (table.getTableStatus().equals(TableItemStatus.free)) {

            currentOrder = metadata.create(Order.class);
            currentOrder.setStatus(OrderStatus.open);
            currentOrder.setTableItemCaption((String)params.get("tableCaption"));
            currentOrder.setActualSeats((Integer)params.get("actualSeats"));
            currentOrder.setCharge(BigDecimal.valueOf(0));
            currentOrder.setTaxes(BigDecimal.valueOf(0));
            if (table.getWithServiceByDefault()) currentOrder.setWithService(true);
                else currentOrder.setWithService(false);

            table.setCurrentOrder(currentOrder);
            table.setTableStatus(TableItemStatus.open);

            dataManager.commit(currentOrder);

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

        table.setChecked(true);
        dataManager.commit(table);

        table = dataManager.load(TableItem.class)
                .query("select e from jokerapp$TableItem e where e.tableCaption = :tableCaption")
                .parameter("tableCaption", params.get("tableCaption"))
                .view("tableItem-view")
                .one();

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

        drawOrderLinesGrid(null, null);

        refreshBill();

        itemsBackBtn.setVisible(false);
        itemsNextBtn.setVisible(false);

        categoriesGrid.removeAll();
        productItemCategoriesDs.refresh();

        if (productItemCategoriesDs.size() == 0) return;

        categoriesActualPage = 1;
        productItemCategoriesToShow.clear();

        for (ProductItemCategory item : productItemCategoriesDs.getItems()) {

            if (item.getVisible()) productItemCategoriesToShow.add(item);

        }

        categorySize = productItemCategoriesToShow.size();

        if (!clientIsTablet) {

            categoriesPages = (categorySize - 1) / 10 + 1;

            if (categorySize<=10) {

                categoriesBackBtn.setVisible(false);
                categoriesNextBtn.setVisible(false);
                drawProductCategories(0, categorySize-1);

            } else {

                categoriesBackBtn.setVisible(false);
                categoriesNextBtn.setVisible(true);
                drawProductCategories(0, 9);

            }

        } else {

            categoriesBackBtn.setVisible(false);
            categoriesNextBtn.setVisible(false);
            categoriesScrollBox.setHeightFull();
            drawProductCategories(0, categorySize-1);
            
        }

    }

    private void drawProductCategories(int start, int end) {

        for (int c = start; c <= end; c++) {

            WebButton cBtn = componentsFactory.createComponent(WebButton.class);

            cBtn.setWidth(categoryBtnWidth);
            cBtn.setHeight(categoryBtnHeight);
            cBtn.setCaptionAsHtml(true);

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

        if (productItemsDs.size() == 0) return;

        productItemsActualPage = 1;
        productItemsToShow.clear();

        for (ProductItem item : productItemsDs.getItems()) {

            if (item.getCategory().getName().equals(productItemCategory.getName()) && item.getVisible()) productItemsToShow.add(item);

        }

        productItemSize = productItemsToShow.size();

        if (!clientIsTablet) {

            productItemsPages = (productItemSize - 1) / 20 + 1;

            if (productItemSize <= 20) {

                itemsBackBtn.setVisible(false);
                itemsNextBtn.setVisible(false);
                DrawProductItems(0, productItemSize-1);

            } else {

                itemsBackBtn.setVisible(false);
                itemsNextBtn.setVisible(true);
                DrawProductItems(0, 19);

            }

        } else {

            itemsBackBtn.setVisible(false);
            itemsNextBtn.setVisible(false);
            productItemScrollBox.setHeightFull();
            DrawProductItems(0, productItemSize-1);

        }

    }

    private void DrawProductItems(int start, int end) {

        for (int c = start; c<=end; c++) {

            WebButton pBtn = componentsFactory.createComponent(WebButton.class);
            pBtn.setWidth(itemBtnWidth);
            pBtn.setHeight(itemBtnHeight);
            pBtn.setCaptionAsHtml(true);

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

                drawOrderLinesGrid(line, "updated");

                orderLinesDs.commit();

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
            newLine.setHasModifier(false);
            newLine.setIsModifier(false);
            newLine.setItemToModifyId(null);
            newLine.setPrinterGroup(productItemToAdd.getPrinterGroup().toString());
            newLine.setIsSended(false);

            newLine.setIsSelected(false);

            orderLinesDs.addItem(newLine);

            if (orderLinesDs.size()>15) orderLinesBegin = orderLinesDs.size()-14;

            drawOrderLinesGrid(newLine, "added");

            orderLinesDs.commit();

            refreshBill();

    }

    public void onAddModifierClick() {

        OrderLine selectedLineExist = null;

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected()) { selectedLineExist = line; break;}

        }

        if (selectedLineExist == null) return;

        OrderLine selectedLine = selectedLineExist;

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

                if (modifierOrderLinesToAdd.isEmpty() && selectedLine.getHasModifier().equals(true)) {

                    selectedLine.setHasModifier(false);

                } else {

                    for (OrderLine newModifierLine : modifierOrderLinesToAdd) {

                        Boolean modifierAlreadyExist = false;

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

                                modifierAlreadyExist = true;

                            }

                        }

                        if (modifierAlreadyExist.equals(false)) {

                            selectedLine.setPrice(selectedLine.getPrice().
                                    add(newModifierLine.getPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));

                            orderLinesDs.addItem(newModifierLine);

                        }

                    }

                }

                selectedLine.setIsSelected(true);
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

                drawOrderLinesGrid(null, null);

            }

        });

    }

    public void onAddManualModifierClick() {

        OrderLine lineFounded = null;

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected()) {

                lineFounded = line;
                break;

            }

        }

        if (lineFounded==null) return;

        OrderLine selectedLine = lineFounded;

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

                Boolean modifierAlreadyExist = false;

                for (OrderLine line : orderLinesDs.getItems()) {

                    if ((!line.getItemName().equals(selectedLine.getItemName())) &&
                            line.getItemName().equals(newModifierLine.getItemName())) {

                        line.setQuantity(line.getQuantity()+1);
                        line.setPrice(line.getPrice().add(newModifierLine.getPrice()));
                        modifierAlreadyExist = true;

                    }

                }

                if (modifierAlreadyExist == false) {

                    newModifierLine.setQuantity(1);
                    newModifierLine.setTaxes(BigDecimal.ZERO);
                    newModifierLine.setOrder(currentOrder);
                    newModifierLine.setPosition(selectedLine.getNextModifierPosition());
                    selectedLine.setNextModifierPosition(selectedLine.getNextModifierPosition()+1);
                    newModifierLine.setHasModifier(false);
                    selectedLine.setHasModifier(true);
                    newModifierLine.setIsModifier(true);
                    newModifierLine.setItemToModifyId(selectedLine.getId());
                    newModifierLine.setPrinterGroup(selectedLine.getPrinterGroup());
                    newModifierLine.setIsSended(false);
                    newModifierLine.setIsSelected(false);
                    orderLinesDs.addItem(newModifierLine);

                }

                selectedLine.setPrice(selectedLine.getPrice().
                        add(newModifierLine.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));

                selectedLine.setIsSelected(true);

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

                drawOrderLinesGrid(null, null);

            }

        });

    }

    public void onAddBtnClick() {

        OrderLine selectedLine = null;

        for (OrderLine line: orderLinesDs.getItems()) if (line.getIsSelected()) { selectedLine = line; break; }

        if (selectedLine == null) return;

        if (selectedLine.getIsModifier()) return;

        if (selectedLine.getIsSended()) {

            productItemsDs.refresh();
            addToOrder(productItemsDs.getItem(selectedLine.getItemId()));

        } else {

            BigDecimal SingleModifiersPrice = (selectedLine.getPrice().subtract(selectedLine.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity())))
                    .divide(BigDecimal.valueOf(selectedLine.getQuantity()), RoundingMode.FLOOR));

            selectedLine.setQuantity(selectedLine.getQuantity()+1);
            selectedLine.setPrice(selectedLine.getPrice().add(selectedLine.getUnitPrice()).add(SingleModifiersPrice));

            drawOrderLinesGrid(selectedLine, "updated");

            orderLinesDs.commit();

            refreshBill();

        }

    }

    public void onSubtractBtnClick() {

        OrderLine lineFounded = null;

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected()) {

                lineFounded = line;
                break;

            }

        }

        if (lineFounded == null) return;

        OrderLine selectedLine = lineFounded;

        if (selectedLine.getIsModifier()) return;

        if (selectedLine.getHasModifier()) {

            if (selectedLine.getQuantity().equals(1)) {

                List<OrderLine> toRemove = new ArrayList();

                for (OrderLine line: orderLinesDs.getItems()) {

                    if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId())) {

                        toRemove.add(line);

                    }
                }

                for (OrderLine line : toRemove) {

                    drawOrderLinesGrid(line, "removed");
                    orderLinesDs.removeItem(line);

                }

                orderLinesDs.removeItem(selectedLine);
                drawOrderLinesGrid(selectedLine, "removed");

                orderLinesDs.commit();

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
            drawOrderLinesGrid(selectedLine, "removed");
            orderLinesDs.commit();

            refreshBill();

            } else {

            selectedLine.setQuantity(selectedLine.getQuantity() - 1);
            selectedLine.setPrice(selectedLine.getPrice().subtract(selectedLine.getUnitPrice()));

            drawOrderLinesGrid(selectedLine, "updated");

            orderLinesDs.commit();

            refreshBill();

        }

    }

    public void onRemoveBtnClick() {

        int initialSize = orderLinesDs.size();

        OrderLine lineToRemove = null;

        for (OrderLine line: orderLinesDs.getItems()) if (line.getIsSelected()) { lineToRemove = line; break; }

        if (lineToRemove != null) {

            List<OrderLine> bufferedLinesToRemove = new ArrayList();

            if (lineToRemove.getIsModifier()) {

                OrderLine orderLineModified = orderLinesDs.getItem(lineToRemove.getItemToModifyId());
                orderLineModified.setPrice(orderLineModified.getPrice().subtract(lineToRemove.getPrice().
                        multiply(BigDecimal.valueOf(orderLineModified.getQuantity()))));

                drawOrderLinesGrid(orderLineModified, "updated");

                bufferedLinesToRemove.add(lineToRemove);

                Boolean modifiedItemHasMoreModifier = false;

                for (OrderLine line: orderLinesDs.getItems()) {

                    if ((line.getItemToModifyId() != null) && line.getItemToModifyId().equals(orderLineModified.getId()))
                        if (!bufferedLinesToRemove.contains(line)) modifiedItemHasMoreModifier = true;

                }

                if (!modifiedItemHasMoreModifier) {

                    for (OrderLine line: orderLinesDs.getItems()) {

                        if ((line != orderLineModified) && line.getItemName().equals(orderLineModified.getItemName())) {

                            if (line.getHasModifier()) {

                                orderLineModified.setHasModifier(false);

                            } else {

                                line.setQuantity(line.getQuantity() + orderLineModified.getQuantity());
                                line.setPrice(line.getPrice().add(orderLineModified.getPrice()));

                                drawOrderLinesGrid(line, "updated");

                                bufferedLinesToRemove.add(orderLineModified);

                            }


                        }

                    }

                }

            } else if (lineToRemove.getHasModifier()) {

                for (OrderLine line: orderLinesDs.getItems()) {

                    if ((line.getItemToModifyId() != null) && line.getItemToModifyId().equals(lineToRemove.getId()))
                        bufferedLinesToRemove.add(line);

                }

                bufferedLinesToRemove.add(lineToRemove);

            } else bufferedLinesToRemove.add(lineToRemove);

            for (int i=0; i< (initialSize-orderLinesDs.size()); i++) {

                if (orderLinesDs.size() < orderLinesBegin + 14 && orderLinesBegin > 1) orderLinesBegin -= 1;
                if (orderLinesBegin==1) break;

            }

            for (OrderLine line : bufferedLinesToRemove) { orderLinesDs.removeItem(line); drawOrderLinesGrid(line, "removed"); }

            orderLinesDs.commit();

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

    public void onCategoriesBackBtnClick() {

        categoriesGrid.removeAll();

        categoriesActualPage--;

        if (categoriesActualPage>1) {

            categoriesBackBtn.setVisible(true);
            categoriesNextBtn.setVisible(true);
            drawProductCategories((categoriesActualPage-1)*10, ((categoriesActualPage-1)*10)+9);

        } else {

            categoriesActualPage = 1;
            categoriesBackBtn.setVisible(false);
            categoriesNextBtn.setVisible(true);
            drawProductCategories(0, 9);

        }

    }

    public void onCategoriesNextBtnClick() {

        categoriesGrid.removeAll();

        categoriesActualPage++;

        if (categoriesActualPage>1 && categoriesActualPage<categoriesPages) {

            categoriesBackBtn.setVisible(true);
            categoriesNextBtn.setVisible(true);
            drawProductCategories((categoriesActualPage-1)*10, ((categoriesActualPage-1)*10)+9);

        } else {

            categoriesBackBtn.setVisible(true);
            categoriesNextBtn.setVisible(false);
            drawProductCategories((categoriesActualPage-1)*10, categorySize-1);

        }

    }

    public void onItemsBackBtnClick() {

        itemsGrid.removeAll();

        productItemsActualPage--;

        if (productItemsActualPage>1) {

            itemsBackBtn.setVisible(true);
            itemsNextBtn.setVisible(true);
            DrawProductItems((productItemsActualPage-1)*20, ((productItemsActualPage-1)*20)+19);

        } else {

            productItemsActualPage = 1;
            itemsBackBtn.setVisible(false);
            itemsNextBtn.setVisible(true);
            DrawProductItems(0, 19);

        }

    }

    public void onItemsNextBtnClick() {

        itemsGrid.removeAll();

        productItemsActualPage++;

        if (productItemsActualPage>1 && productItemsActualPage<productItemsPages) {

            itemsBackBtn.setVisible(true);
            itemsNextBtn.setVisible(true);
            DrawProductItems((productItemsActualPage-1)*20, ((productItemsActualPage-1)*20)+19);

        } else {

            itemsBackBtn.setVisible(true);
            itemsNextBtn.setVisible(false);
            DrawProductItems((productItemsActualPage-1)*20, productItemSize-1);

        }

    }

    public void onModifyPriceBtnClick() {

        OrderLine lineFounded = null;

        for (OrderLine line: orderLinesDs.getItems()) {

            if (line.getIsSelected()) {

                lineFounded = line;
                break;

            }

        }

        if (lineFounded==null) return;

        OrderLine selectedLine = lineFounded;

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

                drawOrderLinesGrid(null, null);

                refreshBill();

            }

        });

    }

    private void drawOrderLinesGrid (OrderLine lineToProcess, String operationPerformed) {

        if (!clientIsTablet) {

            orderLineScrollBox.removeAll();

            if (orderLinesBegin == 1) orderLinesScrollUp.setVisible(false);
            else orderLinesScrollUp.setVisible(true);
            if (orderLinesDs.size()-orderLinesBegin<=14) orderLinesScrollDown.setVisible(false);
            else orderLinesScrollDown.setVisible(true);

            int index = 1;

            for (OrderLine orderLine : orderLinesDs.getItems()) {

                if (index>=orderLinesBegin && index<=orderLinesBegin+14) orderLineScrollBox.add(createOrderLineHBox(orderLine));
                setOrderLineStyle(orderLine, orderLineScrollBox);

                index+=1;

            }

        } else {

            if (lineToProcess != null) {

                if (operationPerformed.equals("added")) {

                    HBoxLayout hBoxToAdd = createOrderLineHBox(lineToProcess);
                    orderLineScrollBox.add(hBoxToAdd);

                    for (OrderLine line: orderLinesDs.getItems()) {

                        if (line.getIsSelected()) {

                            line.setIsSelected(false);
                            setOrderLineStyle(line, orderLineScrollBox);
                            break;

                        }
                    }

                    lineToProcess.setIsSelected(true);
                    setOrderLineStyle(lineToProcess, orderLineScrollBox);
                    orderLineScrollBox.getComponent("itemName".concat(lineToProcess.getId().toString())).requestFocus();
                    return;

                }

                if (operationPerformed.equals("removed")) {

                    HBoxLayout hBoxToRemove = (HBoxLayout) orderLineScrollBox.getOwnComponent("hBoxLayout".concat(lineToProcess.getId().toString()));

                    int index = orderLineScrollBox.indexOf(hBoxToRemove);

                    orderLineScrollBox.remove(hBoxToRemove);

                    if (orderLineScrollBox.getOwnComponents().size()==0) return;

                    if (index==orderLineScrollBox.getOwnComponents().size()) index--;

                    if (!lineToProcess.getIsModifier()) {

                        HBoxLayout hBoxToSelect = (HBoxLayout) orderLineScrollBox.getComponent(index);
                        OrderLine newLineToSelect = orderLinesDs.getItem(UUID.fromString(hBoxToSelect.getId().substring(10)));
                        newLineToSelect.setIsSelected(true);
                        setOrderLineStyle(newLineToSelect, orderLineScrollBox);

                    }

                    return;

                }

                if (operationPerformed.equals("updated")) {

                    Label quantityToChange = (Label) orderLineScrollBox.getComponent("quantity".concat(lineToProcess.getId().toString()));
                    quantityToChange.setValue(lineToProcess.getQuantity());

                    Button itemNameToChange = (Button) orderLineScrollBox.getComponent("itemName".concat(lineToProcess.getId().toString()));
                    itemNameToChange.requestFocus();

                    Label priceToChange = (Label) orderLineScrollBox.getComponent("price".concat(lineToProcess.getId().toString()));
                    priceToChange.setValue(lineToProcess.getPrice());

                    for (OrderLine line: orderLinesDs.getItems()) {

                        if (line.getIsSelected()) {

                            line.setIsSelected(false);
                            setOrderLineStyle(line, orderLineScrollBox);
                            break;

                        }
                    }

                    lineToProcess.setIsSelected(true);
                    setOrderLineStyle(lineToProcess, orderLineScrollBox);

                    return;

                }

            }

             else {

                orderLineScrollBox.removeAll();

                orderLinesScrollUp.setVisible(false);
                orderLinesScrollDown.setVisible(false);

                for (OrderLine orderLine : orderLinesDs.getItems()) {

                    orderLineScrollBox.add(createOrderLineHBox(orderLine));
                    setOrderLineStyle(orderLine, orderLineScrollBox);

                }

            }

        }

    }

    public void onOrderLinesScrollUpClick() {

        orderLinesBegin -=1;

        drawOrderLinesGrid(null, null);

    }

    public void onOrderLinesScrollDownClick() {

        orderLinesBegin +=1;

        drawOrderLinesGrid(null, null);

    }

    public void onSendAndCloseBtnClick() {

        if (!doNotPrint) printTickets();

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSended(true);
            orderLine.setIsSelected(false);

        }

        table.setChecked(false);
        dataManager.commit(table);
        orderLinesDs.commit();

        getWindowManager().close(this);
        openWindow("tableselect", WindowManager.OpenType.THIS_TAB);

    }

    public void onSendBtnClick() {

        if (!doNotPrint) printTickets();

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSended(true);

        }

        drawOrderLinesGrid(null, null);

        orderLinesDs.commit();


    }

    public void onReSendBtnClick() {

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSended(false);

        }

        if (!doNotPrint) printTickets();

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSended(true);

        }

        drawOrderLinesGrid(null, null);

        orderLinesDs.commit();

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

        currentOrder = dataManager.load(Order.class)
                .query("select e from jokerapp$Order e where e.id = :currentOrder")
                .parameter("currentOrder", table.getCurrentOrder())
                .view("order-view")
                .one();

        currentOrder.setStatus(OrderStatus.bill);

        dataManager.commit(currentOrder);

        table.setChecked(false);
        dataManager.commit(table);
        getWindowManager().close(this);
        openWindow("tableselect", WindowManager.OpenType.THIS_TAB);

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
                graphics2D.drawString("PRECONTO TAVOLO: ".concat(currentOrder.getTableItemCaption().toString()), xMin, y);
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
                x = paperWidth -4 - Math.multiplyExact(currentOrder.getCharge().add(currentOrder.getTaxes()).toString().length(), font1.getSize()-7);
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

    private void printTickets() {

        withFries = false;
        isGrillTicket = false;

        for (PrinterGroup printerGroup : PrinterGroup.values()) {

            printerGroupToSendTicket = printerGroup.toString();

            Boolean printerGroupLinesExixts = false;

            for (OrderLine line : orderLinesDs.getItems()) {

                if (line.getOrder().equals(currentOrder) && line.getPrinterGroup().equals(printerGroupToSendTicket) && !line.getIsSended())
                    printerGroupLinesExixts = true;

            }

            if (printerGroupToSendTicket.equals("Fryer") && printerGroupLinesExixts) withFries = true;
            if (printerGroupToSendTicket.equals("Grill")) isGrillTicket = true;

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
                graphics2D.drawString(printerGroupToSendTicket.toUpperCase(), xMin+70, y);
                y += 30;
                graphics2D.drawString("TAVOLO: ".concat(table.getTableCaption()), xMin, y);
                y += 30;
                graphics2D.setFont(font3);
                graphics2D.drawString("Coperti: ".concat(currentOrder.getActualSeats().toString()), xMin, y);
                y += 20;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                graphics2D.drawString("Time: ".concat(sdf.format(cal.getTime())), xMin, y);
                y += 30;

                if (isGrillTicket && !withFries) {

                    graphics2D.drawString("NO FRITTURE", xMin+10, y);
                    y += 30;

                }

                graphics2D.setFont(font2);

                for (OrderLine line : orderLinesDs.getItems()) {

                    if (line.getOrder().equals(currentOrder) && line.getPrinterGroup().equals(printerGroupToSendTicket) && !line.getIsSended()) {

                        if (!line.getIsModifier()) graphics2D.drawString(line.getQuantity().toString(),xMin, y);

                        Integer linesToDraw = Math.round(line.getItemName().length()/20) + 1 ;

                        int spacePosition = 0;
                        int currentSpacePosition = 0;

                        for (int l=1; l<linesToDraw; l++) {

                            String lineName = line.getItemName();

                            for (int i = spacePosition; i<line.getItemName().length(); i++) {

                                Character c = lineName.charAt(i);

                                if (Character.isSpace(c)) {

                                    if (i > 20*l) break;

                                    spacePosition = i;

                                }

                            }

                            graphics2D.drawString(lineName.substring(currentSpacePosition, spacePosition),xMin+font2.getSize(), y);

                            currentSpacePosition = spacePosition;

                            y = y + yInc + 4;

                        }

                        graphics2D.drawString(line.getItemName().substring(currentSpacePosition),xMin+font2.getSize(), y);

                        y = y + yInc + 8;

                    }

                }

                graphics2D.drawString(".", xMin, y);

                return Printable.PAGE_EXISTS;
            }

            return Printable.NO_SUCH_PAGE;

        }

    }

    public void onDoNotPrintBtnClick() {

        if (doNotPrint) {

            doNotPrintBtn.setStyleName("doNotPrintBtnNotPushed");
            doNotPrintBtn.setCaption("STAMPA LE<br>COMANDE");
            doNotPrint = false;

        } else {

            doNotPrintBtn.setStyleName("doNotPrintBtnPushed");
            doNotPrintBtn.setCaption("NON STAMPARE<br>LE COMANDE");
            doNotPrint = true;
        }

    }

    public void onCloseBtnClick() {

        for (OrderLine orderLine : orderLinesDs.getItems()) {

            orderLine.setIsSelected(false);

        }

        table.setChecked(false);
        dataManager.commit(table);
        orderLinesDs.commit();

        getWindowManager().close(this);
        openWindow("tableselect", WindowManager.OpenType.THIS_TAB);

    }

    private HBoxLayout createOrderLineHBox (OrderLine orderLine) {

        HBoxLayout hBoxLayout= componentsFactory.createComponent(HBoxLayout.class);

        hBoxLayout.setId("hBoxLayout".concat(orderLine.getId().toString()));

        Label quantity = componentsFactory.createComponent(Label.class);
        Button itemName = componentsFactory.createComponent(Button.class);
        Label price = componentsFactory.createComponent(Label.class);

        quantity.setWidth("35px");
        itemName.setWidth("480px");
        price.setWidth("55px");

        quantity.setHeight("40px");
        itemName.setHeight("40px");
        price.setHeight("40px");

        quantity.setId("quantity".concat(orderLine.getId().toString()));
        itemName.setId("itemName".concat(orderLine.getId().toString()));
        price.setId("price".concat(orderLine.getId().toString()));

        quantity.setAlignment(Alignment.MIDDLE_LEFT);
        price.setAlignment(Alignment.MIDDLE_RIGHT);

        itemName.setAction(new SelectCurrentLineAction());

        setOrderLineStyle(orderLine, orderLineScrollBox);

        if (!orderLine.getIsModifier()) quantity.setValue(orderLine.getQuantity());

        if (orderLine.getItemName().length()<50) itemName.setCaption(orderLine.getItemName());
        else itemName.setCaption(orderLine.getItemName().substring(0,27).concat("...").concat(orderLine.getItemName().substring(orderLine.getItemName().length()-20)));
        if (orderLine.getIsModifier() && !orderLine.getPrice().toString().equals("0.00")) {

            if (orderLine.getPrice().toString().charAt(0)=='-') price.setValue("(".concat(orderLine.getPrice().toString()).concat(")"));
            else price.setValue("(+".concat(orderLine.getPrice().toString()).concat(")"));

        } else if (!orderLine.getIsModifier()) price.setValue(orderLine.getPrice().setScale(2).toString());

        hBoxLayout.add(quantity);
        hBoxLayout.add(itemName);
        hBoxLayout.add(price);

        return hBoxLayout;

    }

    private void setOrderLineStyle(OrderLine orderLine, ScrollBoxLayout scrollBox) {

        if (scrollBox.getOwnComponent("hBoxLayout".concat(orderLine.getId().toString())) != null) {

            Label quantity = (Label) scrollBox.getComponent("quantity".concat(orderLine.getId().toString()));
            Button itemName = (Button) scrollBox.getComponent("itemName".concat(orderLine.getId().toString()));
            Label price = (Label) scrollBox.getComponent("price".concat(orderLine.getId().toString()));

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

        }

    }

    private class SelectCurrentLineAction extends BaseAction {

        public SelectCurrentLineAction() {
            super("SelectCurrentLine");
        }

        @Override
        public boolean isPrimary() {

            return true;

        }

        @Override
        public void actionPerform(Component component) {

            Button itemNameBtn = (Button) component;
            OrderLine orderLine = orderLinesDs.getItem(UUID.fromString(itemNameBtn.getId().substring(8)));

            if (!orderLine.getIsSelected()) {

                orderLine.setIsSelected(true);

                for (OrderLine line : orderLinesDs.getItems()) {

                    if (line.getIsSelected() && !line.getId().equals(orderLine.getId())) {

                        line.setIsSelected(false);

                        setOrderLineStyle(line, orderLineScrollBox);

                        break;

                    }

                }

            } else orderLine.setIsSelected(false);

            setOrderLineStyle(orderLine, orderLineScrollBox);

            orderLinesDs.commit();

        }

    }

}