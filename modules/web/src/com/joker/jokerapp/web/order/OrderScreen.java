package com.joker.jokerapp.web.order;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstancePropertyContainer;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.*;
import com.joker.jokerapp.web.dialogs.ItemManualModifierDialog;
import com.joker.jokerapp.web.dialogs.ItemPriceManualModifierDialog;
import com.joker.jokerapp.web.screens.OrderScreenOLD;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.Calendar;
import java.util.List;

@UiController("jokerapp_OrderScreen")
@UiDescriptor("order-screen.xml")
public class OrderScreen extends Screen {

    @Inject
    private Metadata metadata;

    @Inject
    private UserSession userSession;

    @Inject
    private InstancePropertyContainer<Order> orderDc;

    @Inject
    private CollectionPropertyContainer<Ticket> ticketsDc;

    @Inject
    private DataManager dataManager;

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

    @Named("doNotPrintBtn")
    private Button doNotPrintBtn;

    private String categoryBtnWidth = "180px";
    private String categoryBtnHeight = "140px";

    private String itemBtnWidth = "180px";
    private String itemBtnHeight = "160px";

    private UUID currentTicketId;

    private List<OrderLine> modifierOrderLinesToAdd = new ArrayList<>();

    private BigDecimal subTotal = new BigDecimal(0.0);
    private BigDecimal service = new BigDecimal(0.0);
    private BigDecimal total = new BigDecimal(0.0);

    private PrinterGroup printerGroupToSendTicket;

    private int categorySize = 0;
    private int categoriesPages = 0;
    private int categoriesActualPage = 1;

    private int productItemSize = 0;
    private int productItemsPages = 0;
    private int productItemsActualPage = 1;

    private ArrayList<Integer> spaceToConvert = new ArrayList<>();

    private ArrayList<ProductItemCategory> productItemCategoriesToShow = new ArrayList<>();

    private ArrayList<ProductItem> productItemsToShow = new ArrayList<>();

    private boolean doNotPrint = false;

    private boolean withFries = false;
    private boolean isGrillTicket = false;

    private boolean clientIsTablet = true;

    private UUID selectedLineId;

    @Subscribe
    protected void onInit(InitEvent event) {

        //orderDc.setItem();

    }

/*

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        if (userSession.getUser().getLogin().equals("tablet")) {

            clientIsTablet = true;

        }

        subtotalLabel.setStyleName("subtotalLabel");
        serviceLabel.setStyleName("serviceLabel");
        totalLabel.setStyleName("totalLabel");

        subtotalField.setStyleName("subtotalField");
        serviceField.setStyleName("serviceField");
        totalField.setStyleName("totalField");

        doNotPrintBtn.setStyleName("doNotPrintBtnNotPushed");
        doNotPrintBtn.setCaption("STAMPA<br>LE COMANDE");

        tableItemDs.setItem((TableItem) params.get("selectedTable"));

        if (tableItemDs.getItem().getTableStatus().equals(TableItemStatus.free)) {

            Order order = metadata.create(Order.class);
            order.setStatus(OrderStatus.open);
            order.setTableItemCaption(tableItemDs.getItem().getTableCaption());
            order.setActualSeats((Integer) params.get("actualSeats"));
            order.setCharge(BigDecimal.valueOf(0));
            order.setTaxes(BigDecimal.valueOf(0));

            if (tableItemDs.getItem().getWithServiceByDefault()) order.setWithService(true);
            else order.setWithService(false);

            tableItemDs.getItem().setCurrentOrder(order);
            tableItemDs.getItem().setTableStatus(TableItemStatus.open);


        } else {

            for (Ticket ticket: ticketsDs.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {

                currentTicketId = ticket.getId();
                break;

            }

            drawOrderLinesGrid(null, null);

            refreshBill();

        }

        tableItemDs.getItem().setChecked(true);

        tableItemDs.commit();

        Long tableTime = Instant.now().getEpochSecond() - tableItemDs.getItem().getCurrentOrder().getCreateTs().toInstant().getEpochSecond();

        if (tableTime <= 3600) tableTimeField.setStyleName("tableTimeField-normal");
        else tableTimeField.setStyleName("tableTimeField-hot");

        tableTimeField.setValue(Date.from(Instant.ofEpochSecond(tableTime - 3600)));

        Timer clockTimer = componentsFactory.createTimer();
        addTimer(clockTimer);
        clockTimer.setDelay(5000);
        clockTimer.setRepeating(true);
        clockTimer.addActionListener(timer -> {

            if (Instant.now().getEpochSecond() - tableItemDs.getItem().getCurrentOrder().getCreateTs().toInstant().getEpochSecond() <= 3600)
                tableTimeField.setStyleName("tableTimeField-normal");
            else tableTimeField.setStyleName("tableTimeField-hot");
            tableTimeField.setValue(Date.from(Instant.ofEpochSecond(Instant.now().getEpochSecond() - tableItemDs.getItem().getCurrentOrder().getCreateTs().toInstant().getEpochSecond() - 3600)));

        });

        clockTimer.start();

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

            if (categorySize <= 10) {

                drawProductCategories(0, categorySize - 1);

            } else {


                drawProductCategories(0, 9);

            }

        } else {

            categoriesScrollBox.setHeightFull();
            drawProductCategories(0, categorySize - 1);

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

            if (Math.floorMod(categoryName.length(), 10) == 0) numberOfRow = Math.floorDiv(categoryName.length(), 10);
            else numberOfRow = Math.floorDiv(categoryName.length(), 10) + 1;

            int exactLineLength = Math.floorDiv(categoryName.length(), numberOfRow);

            if (categoryName.length() > exactLineLength && categoryName.contains(" ")) {

                int actualSpace = 0;
                int prevSpaceConverted = 0;

                spaceToConvert.clear();
                maxLineLength = 0;

                for (int l = 0; l < categoryName.length(); l++) {

                    char ch = categoryName.charAt(l);

                    if (Character.isWhitespace(ch) || l == categoryName.length() - 1) {

                        if (l - prevSpaceConverted > exactLineLength) {

                            if (actualSpace != 0 && prevSpaceConverted != actualSpace && (actualSpace - prevSpaceConverted <= l - actualSpace || l - prevSpaceConverted > 10)) {

                                spaceToConvert.add(actualSpace);
                                if (actualSpace - prevSpaceConverted > maxLineLength)
                                    maxLineLength = actualSpace - prevSpaceConverted;
                                prevSpaceConverted = actualSpace;

                            } else {

                                if (!(l == categoryName.length() - 1)) {

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

            if (maxLineLength <= 10 && spaceToConvert.size() < 3) cBtn.setStyleName("v-button-fontSize30");
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

            if (item.getCategory().getName().equals(productItemCategory.getName()) && item.getVisible())
                productItemsToShow.add(item);

        }

        productItemSize = productItemsToShow.size();

        if (!clientIsTablet) {

            productItemsPages = (productItemSize - 1) / 20 + 1;

            if (productItemSize <= 20) {

                DrawProductItems(0, productItemSize - 1);

            } else {

                DrawProductItems(0, 19);

            }

        } else {

            productItemScrollBox.setHeightFull();
            DrawProductItems(0, productItemSize - 1);

        }

    }

    private void DrawProductItems(int start, int end) {

        for (int c = start; c <= end; c++) {

            WebButton pBtn = componentsFactory.createComponent(WebButton.class);
            pBtn.setWidth(itemBtnWidth);
            pBtn.setHeight(itemBtnHeight);
            pBtn.setCaptionAsHtml(true);

            int numberOfRow;

            int maxLineLength = 0;

            String productName = productItemsToShow.get(c).getName();

            if (Math.floorMod(productName.length(), 14) == 0) numberOfRow = Math.floorDiv(productName.length(), 14);
            else numberOfRow = Math.floorDiv(productName.length(), 14) + 1;

            int exactLineLength = Math.floorDiv(productName.length(), numberOfRow);

            if (productName.length() > exactLineLength && productName.contains(" ")) {

                int actualSpace = 0;
                int prevSpaceConverted = 0;

                spaceToConvert.clear();
                maxLineLength = 0;

                for (int l = 0; l < productName.length(); l++) {

                    char ch = productName.charAt(l);

                    if (Character.isWhitespace(ch) || l == productName.length() - 1) {

                        if (l - prevSpaceConverted > exactLineLength) {

                            if (actualSpace != 0 && prevSpaceConverted != actualSpace && (actualSpace - prevSpaceConverted <= l - actualSpace || l - prevSpaceConverted > 14)) {

                                spaceToConvert.add(actualSpace);
                                if (actualSpace - prevSpaceConverted > maxLineLength)
                                    maxLineLength = actualSpace - prevSpaceConverted;
                                prevSpaceConverted = actualSpace;

                            } else {

                                if (!(l == productName.length() - 1)) {

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

            if (maxLineLength <= 14 && spaceToConvert.size() < 4) pBtn.setStyleName("v-button-fontSize20");
            else pBtn.setStyleName("v-button-fontSize16");

            ProductItem toAdd = productItemsToShow.get(c);
            pBtn.setAction(new BaseAction("addToOrder".concat(productItemsToShow.get(c).getName())).withHandler(e -> addToOrder(toAdd)));
            itemsGrid.add(pBtn);

        }

    }

    private void addToOrder(ProductItem productItemToAdd) {

        int max = 0;

        for (Ticket ticket: ticketsDs.getItems()) for (OrderLine line: ticket.getOrderLines()) {

            if (!line.getIsModifier() && line.getPosition() > max) {

                max = line.getPosition();

            }

        }

        max += 100;

        if (currentTicketId == null) {

            Ticket ticket = metadata.create(Ticket.class);
            ticket.setOrder(tableItemDs.getItem().getCurrentOrder());
            ticket.setTicketStatus(TicketStatus.notSended);
            ticket.setTicketNumber(ticketsDs.getItems().size() + 1);
            ticket.setSubticketStatus("bn-fn-gn");

            dataManager.commit(ticket);
            tableItemDs.refresh();

            currentTicketId = ticket.getId();

        } else for (OrderLine line: ticketsDs.getItem(currentTicketId).getOrderLines()) if ((line.getItemId().equals(productItemToAdd.getId())) && !line.getHasModifier()) {

            line.setQuantity(line.getQuantity() + 1);
            line.setPrice(line.getPrice().add(productItemToAdd.getPrice()));

            dataManager.commit(line);

            tableItemDs.refresh();

            drawOrderLinesGrid(line, "updated");

            refreshBill();

            return;

        }

        OrderLine newLine = metadata.create(OrderLine.class);

        newLine.setQuantity(1);
        newLine.setItemName(productItemToAdd.getName());
        newLine.setItemId(productItemToAdd.getId());
        newLine.setUnitPrice(productItemToAdd.getPrice());
        newLine.setPrice(productItemToAdd.getPrice());
        newLine.setTaxes(BigDecimal.ZERO);

        newLine.setTicket(ticketsDs.getItem(currentTicketId));
        newLine.setPosition(max);
        newLine.setNextModifierPosition(max + 1);
        newLine.setHasModifier(false);
        newLine.setIsModifier(false);
        newLine.setItemToModifyId(null);
        newLine.setPrinterGroup(productItemToAdd.getPrinterGroup());

        if (newLine.getPrinterGroup().equals(PrinterGroup.Bar) && ticketsDs.getItem(currentTicketId).getSubticketStatus().charAt(1) == 'n') {

            ticketsDs.getItem(currentTicketId).setSubticketStatus(ticketsDs.getItem(currentTicketId).getSubticketStatus().replace("bn", "bo"));
            dataManager.commit(ticketsDs.getItem(currentTicketId));

        } else if (newLine.getPrinterGroup().equals(PrinterGroup.Fryer) && ticketsDs.getItem(currentTicketId).getSubticketStatus().charAt(4) == 'n') {

            ticketsDs.getItem(currentTicketId).setSubticketStatus(ticketsDs.getItem(currentTicketId).getSubticketStatus().replace("fn", "fo"));
            dataManager.commit(ticketsDs.getItem(currentTicketId));


        } else if (newLine.getPrinterGroup().equals(PrinterGroup.Grill) && ticketsDs.getItem(currentTicketId).getSubticketStatus().charAt(7) == 'n') {

            ticketsDs.getItem(currentTicketId).setSubticketStatus(ticketsDs.getItem(currentTicketId).getSubticketStatus().replace("gn", "go"));
            dataManager.commit(ticketsDs.getItem(currentTicketId));

        }

        newLine.setChecked(false);
        newLine.setIsReversed(false);

        dataManager.commit(newLine);

        tableItemDs.refresh();

        drawOrderLinesGrid(newLine, "added");

        refreshBill();

    }

*/
/*    public void onAddModifierClick() {

        if (selectedLineId == null) return;

        OrderLine founded = null;

        for (Ticket ticket: ticketsDs.getItems()) for (OrderLine line: ticket.getOrderLines()) if (line.getId().equals(selectedLineId)) {

            founded = line;
            break;

        }

        if (founded.getIsModifier()) return;

        OrderLine selectedLine = founded;

        List<OrderLine> modifierOrderLines = new ArrayList<>();

        if (selectedLine.getHasModifier()) for (OrderLine line: selectedLine.getTicket().getOrderLines())
            if (line != selectedLine && line.getItemToModifyId().equals(selectedLine.getId())) modifierOrderLines.add(line);

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

        if (productItemsDs.size() == 0) productItemsDs.refresh();

        params.put("productItem", productItemsDs.getItem(selectedLine.getItemId()));
        params.put("modifierOrderLines", modifierOrderLines);

        openWindow("jokerapp$ItemModifier.dialog", WindowManager.OpenType.DIALOG, params).addCloseListener(closeString -> {

            if (closeString.equals("close")) {

                for (OrderLine line: selectedLine.getTicket().getOrderLines()) {

                    if (line.getIsModifier() && line.getItemToModifyId().equals(selectedLine.getId()) && !modifierOrderLinesToAdd.contains(line)) {

                        selectedLine.setPrice(selectedLine.getPrice().subtract(line.getPrice()));

                        selectedLine.getTicket().getOrderLines().remove(line);

                    }

                }

                if (modifierOrderLinesToAdd.isEmpty() && selectedLine.getHasModifier().equals(true)) {

                    selectedLine.setHasModifier(false);

                } else {

                    for (OrderLine newModifierLine: modifierOrderLinesToAdd) {

                        selectedLine.setPrice(selectedLine.getPrice().add(newModifierLine.getPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));
                        dataManager.commit(newModifierLine);

                    }

                }

                dataManager.commit(selectedLine);

                tableItemDs.refresh();

                drawOrderLinesGrid(null, null);

                refreshBill();

            }

        });

    }*//*


    public void onAddManualModifierClick() {

        if (selectedLineId == null) return;

        OrderLine founded = null;

        for (Ticket ticket: ticketsDs.getItems()) for (OrderLine line: ticket.getOrderLines()) if (line.getId().equals(selectedLineId)) {

            founded = line;
            break;
        }

        if (founded.getIsModifier()) return;

        OrderLine selectedLine = founded;

        OrderLine newModifierLine = metadata.create(OrderLine.class);

        Map<String, Object> params = new HashMap<>();

        ItemManualModifierDialog.CloseHandler handler = new ItemManualModifierDialog.CloseHandler() {

            @Override
            public void onClose(String itemName, BigDecimal itemModifierPrice) {

                if (itemName == null) return;

                newModifierLine.setItemName("  * ".concat(itemName));
                newModifierLine.setUnitPrice(itemModifierPrice);
                newModifierLine.setPrice(itemModifierPrice);

            }

        };

        params.put("handler", handler);

        openWindow("jokerapp$ItemManualModifier.dialog", WindowManager.OpenType.DIALOG, params).addCloseListener(closeString -> {

            if (closeString.equals("ok") && newModifierLine.getItemName() != null) {

                newModifierLine.setQuantity(1);
                newModifierLine.setTaxes(BigDecimal.ZERO);
                newModifierLine.setTicket(ticketsDs.getItem(currentTicketId));
                newModifierLine.setPosition(selectedLine.getNextModifierPosition());
                selectedLine.setNextModifierPosition(selectedLine.getNextModifierPosition() + 1);
                newModifierLine.setHasModifier(false);
                selectedLine.setHasModifier(true);
                newModifierLine.setIsModifier(true);
                newModifierLine.setItemToModifyId(selectedLine.getId());
                newModifierLine.setPrinterGroup(selectedLine.getPrinterGroup());
                newModifierLine.setChecked(false);
                newModifierLine.setIsReversed(false);

                selectedLine.getTicket().getOrderLines().add(newModifierLine);

                selectedLine.setPrice(selectedLine.getPrice().add(newModifierLine.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));

                dataManager.commit(newModifierLine,selectedLine);

                tableItemDs.refresh();

                refreshBill();

                drawOrderLinesGrid(null, null);

            }

        });

    }

    public void onAddBtnClick() {

        if (selectedLineId == null) return;

        OrderLine selectedLine = null;

        for (Ticket ticket: ticketsDs.getItems()) for (OrderLine line : ticket.getOrderLines()) if (line.getId().equals(selectedLineId)) {

            selectedLine = line;
            break;

        }

        if (selectedLine.getIsModifier()) return;

        if (selectedLine.getTicket().getTicketStatus().equals(TicketStatus.sended)) {

            if (productItemsDs.size() == 0) productItemsDs.refresh();

            addToOrder(productItemsDs.getItem(selectedLine.getItemId()));

        } else {

            BigDecimal SingleModifiersPrice = (selectedLine.getPrice().subtract(selectedLine.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity())))
                    .divide(BigDecimal.valueOf(selectedLine.getQuantity()), RoundingMode.FLOOR));

            selectedLine.setQuantity(selectedLine.getQuantity() + 1);
            selectedLine.setPrice(selectedLine.getPrice().add(selectedLine.getUnitPrice()).add(SingleModifiersPrice));

            dataManager.commit(selectedLine);

            tableItemDs.refresh();

            drawOrderLinesGrid(selectedLine, "updated");

            refreshBill();

        }

    }

    public void onSubtractBtnClick() {

        if (selectedLineId == null) return;

        OrderLine selectedLine = null;

        for (Ticket ticket : ticketsDs.getItems()) for (OrderLine line : ticket.getOrderLines()) if (line.getId().equals(selectedLineId)) {

            selectedLine = line;
            break;

        }

        if (selectedLine.getIsModifier() || !selectedLine.getTicket().getId().equals(currentTicketId)) return;

        if (selectedLine.getHasModifier()) if (selectedLine.getQuantity().equals(1)) {

            for (OrderLine line: selectedLine.getTicket().getOrderLines()) if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId())) {

                dataManager.remove(line);

                tableItemDs.refresh();

                drawOrderLinesGrid(line, "removed");

            }

            dataManager.remove(selectedLine);

            tableItemDs.refresh();

            drawOrderLinesGrid(selectedLine, "removed");

            refreshBill();

            return;

        }

        for (OrderLine line: selectedLine.getTicket().getOrderLines()) if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId()))
            selectedLine.setPrice(selectedLine.getPrice().subtract(line.getUnitPrice()));

        if (selectedLine.getQuantity().equals(1)) {

            dataManager.remove(selectedLine);

            tableItemDs.refresh();

            drawOrderLinesGrid(selectedLine, "removed");

        } else {

            selectedLine.setQuantity(selectedLine.getQuantity() - 1);
            selectedLine.setPrice(selectedLine.getPrice().subtract(selectedLine.getUnitPrice()));

            dataManager.commit(selectedLine);

            tableItemDs.refresh();

            drawOrderLinesGrid(selectedLine, "updated");

        }

        refreshBill();

    }

    public void onRemoveBtnClick() {

        if (selectedLineId == null) return;

        OrderLine lineToRemove = null;
        Ticket lineToRemoveTicket = null;

        for (Ticket ticket: ticketsDs.getItems()) for (OrderLine line: ticket.getOrderLines()) if (line.getId().equals(selectedLineId)) {

            lineToRemove = line;
            lineToRemoveTicket = line.getTicket();
            break;

        }

        if (!lineToRemoveTicket.getId().equals(currentTicketId)) {

            lineToRemove.setPrice(BigDecimal.ZERO);
            lineToRemove.setIsReversed(true);

            dataManager.commit(lineToRemove);

            tableItemDs.refresh();

            drawOrderLinesGrid(lineToRemove,"updated");

            refreshBill();

        } else {

            if (lineToRemove.getIsModifier()) {

                OrderLine orderLineModified = null;

                for (OrderLine line: lineToRemoveTicket.getOrderLines()) if (line.getId().equals(lineToRemove.getItemToModifyId())) { orderLineModified = line; break; }

                orderLineModified.setPrice(orderLineModified.getPrice().subtract(lineToRemove.getPrice().multiply(BigDecimal.valueOf(orderLineModified.getQuantity()))));

                lineToRemoveTicket.getOrderLines().remove(lineToRemove);
                dataManager.remove(lineToRemove);

                tableItemDs.refresh();

                drawOrderLinesGrid(lineToRemove, "removed");

                boolean modifiedItemHasMoreModifier = false;

                for (OrderLine line: lineToRemoveTicket.getOrderLines()) if (line != lineToRemove && (line.getItemToModifyId() != null) && line.getItemToModifyId().equals(orderLineModified.getId()))
                    modifiedItemHasMoreModifier = true;

                if (!modifiedItemHasMoreModifier) {

                    boolean orderLineModifiedDuplicateFound = false;

                    for (OrderLine line: lineToRemoveTicket.getOrderLines()) if (line != orderLineModified && line.getItemId().equals(orderLineModified.getItemId())) if (!line.getHasModifier()) {

                        line.setQuantity(line.getQuantity() + orderLineModified.getQuantity());
                        line.setPrice(line.getPrice().add(orderLineModified.getPrice()));

                        dataManager.commit(line);

                        dataManager.remove(orderLineModified);

                        tableItemDs.refresh();

                        drawOrderLinesGrid(orderLineModified, "removed");

                        drawOrderLinesGrid(line, "updated");

                        orderLineModifiedDuplicateFound = true;

                        break;

                    }

                    if (!orderLineModifiedDuplicateFound) { orderLineModified.setHasModifier(false); dataManager.commit(orderLineModified); tableItemDs.refresh(); drawOrderLinesGrid(orderLineModified, "updated"); }

                }

                refreshBill();

                return;

            } else if (lineToRemove.getHasModifier()) {

                for (OrderLine line: lineToRemoveTicket.getOrderLines()) if ((line.getItemToModifyId() != null) && line.getItemToModifyId().equals(lineToRemove.getId())) {

                    dataManager.remove(line);

                    tableItemDs.refresh();

                    drawOrderLinesGrid(line, "removed");

                }

            }

            dataManager.remove(lineToRemove);

            tableItemDs.refresh();

            drawOrderLinesGrid(lineToRemove, "removed");

            refreshBill();

        }

    }

    private void refreshBill() {

        subTotal = BigDecimal.ZERO;

        for (Ticket ticket: ticketsDs.getItems()) for (OrderLine line: ticket.getOrderLines()) if (!line.getIsModifier()) subTotal = subTotal.add(line.getPrice());

        tableItemDs.getItem().getCurrentOrder().setCharge(subTotal);

        subtotalField.setCaption(subTotal.toString().concat(" €"));

        if (tableItemDs.getItem().getCurrentOrder().getWithService()) {

            service = BigDecimal.valueOf(Math.round(subTotal.multiply(BigDecimal.valueOf(0.1)).subtract(BigDecimal.valueOf(0.2)).
                    multiply(BigDecimal.valueOf(2)).doubleValue()) / 2.0f).setScale(2);

            tableItemDs.getItem().getCurrentOrder().setTaxes(service);

            total = subTotal.add(service);

            serviceField.setCaption(service.toString().concat(" €"));

            totalField.setCaption(total.toString().concat(" €"));

        } else totalField.setCaption(subTotal.toString().concat(" €"));

        dataManager.commit(tableItemDs.getItem().getCurrentOrder());

    }

    public void onModifyPriceBtnClick() {

        OrderLine lineFounded = null;

        for (Ticket ticket: ticketsDs.getItems()) for (OrderLine line: ticket.getOrderLines()) if (line.getId().equals(selectedLineId)) {

            lineFounded = line;
            break;

        }

        if (lineFounded == null || lineFounded.getIsModifier()) return;

        OrderLine selectedLine = lineFounded;

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

                BigDecimal newPrice = (BigDecimal) params.get("newPrice");

                if (selectedLine.getHasModifier()) {

                    BigDecimal modifierPrice = new BigDecimal(0);

                    for (Ticket ticket: ticketsDs.getItems()) for (OrderLine line: ticket.getOrderLines()) if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId()))
                        modifierPrice = modifierPrice.add(line.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity())));

                    try {

                        selectedLine.setUnitPrice(newPrice.subtract(modifierPrice).divide(BigDecimal.valueOf(selectedLine.getQuantity()), RoundingMode.FLOOR));
                        selectedLine.setPrice(newPrice);

                    } catch (ArithmeticException a) {

                        a.printStackTrace();

                    }

                } else {

                    selectedLine.setUnitPrice(newPrice.divide(BigDecimal.valueOf(selectedLine.getQuantity())));
                    selectedLine.setPrice(newPrice);

                }

                dataManager.commit(selectedLine);

                drawOrderLinesGrid(null, null);

                refreshBill();

            }

        });

    }

    private void drawOrderLinesGrid(OrderLine lineToProcess, String operationPerformed) {

        if (lineToProcess != null) {

            if (operationPerformed.equals("added")) {

                HBoxLayout hBoxToAdd = createOrderLineHBox(lineToProcess);
                orderLineScrollBox.add(hBoxToAdd);

                if (selectedLineId != null) {

                    for (Ticket ticket : ticketsDs.getItems()) for (OrderLine orderLine: ticket.getOrderLines()) if (orderLine.getId().equals(selectedLineId)) {

                        selectedLineId = lineToProcess.getId();
                        setOrderLineStyle(orderLine, orderLineScrollBox);

                    }

                } else selectedLineId = lineToProcess.getId();

                setOrderLineStyle(lineToProcess, orderLineScrollBox);

                orderLineScrollBox.getComponent("itemName".concat(lineToProcess.getId().toString())).requestFocus();

                return;

            }

            if (operationPerformed.equals("removed")) {

                HBoxLayout hBoxToRemove = (HBoxLayout) orderLineScrollBox.getOwnComponent("hBoxLayout".concat(lineToProcess.getId().toString()));

                int index = orderLineScrollBox.indexOf(hBoxToRemove);

                orderLineScrollBox.remove(hBoxToRemove);

                if (orderLineScrollBox.getOwnComponents().size() == 0) {

                    selectedLineId = null;

                    return;

                }

                if (index == orderLineScrollBox.getOwnComponents().size()) index--;

                HBoxLayout hBoxToSelect = (HBoxLayout) orderLineScrollBox.getComponent(index);

                for (Ticket ticket: ticketsDs.getItems()) for (OrderLine orderLine: ticket.getOrderLines()) if (orderLine.getId().equals(UUID.fromString(hBoxToSelect.getId().substring(10)))) {

                    if (!lineToProcess.getId().equals(selectedLineId)) {

                        for (Ticket tkt: ticketsDs.getItems()) for (OrderLine line: tkt.getOrderLines()) if (orderLine.getId().equals(selectedLineId)) {

                            selectedLineId = orderLine.getId();

                            setOrderLineStyle(line, orderLineScrollBox);

                        }

                    } else selectedLineId = orderLine.getId();

                    setOrderLineStyle(orderLine, orderLineScrollBox);
                    return;

                }

            }

            if (operationPerformed.equals("updated")) {

                Label quantityToChange = (Label) orderLineScrollBox.getComponent("quantity".concat(lineToProcess.getId().toString()));
                quantityToChange.setValue(lineToProcess.getQuantity());

                Button itemNameToChange = (Button) orderLineScrollBox.getComponent("itemName".concat(lineToProcess.getId().toString()));
                itemNameToChange.requestFocus();

                Label priceToChange = (Label) orderLineScrollBox.getComponent("price".concat(lineToProcess.getId().toString()));
                priceToChange.setValue(lineToProcess.getPrice());

                if (!lineToProcess.getId().equals(selectedLineId)) for (Ticket ticket: ticketsDs.getItems()) for (OrderLine orderLine: ticket.getOrderLines()) if (orderLine.getId().equals(selectedLineId)) {

                    selectedLineId = lineToProcess.getId();
                    setOrderLineStyle(orderLine, orderLineScrollBox);

                }

                setOrderLineStyle(lineToProcess, orderLineScrollBox);

            }

        } else {

            orderLineScrollBox.removeAll();

            for (Ticket ticket: ticketsDs.getItems()) {

                ticket.getOrderLines().sort(Comparator.comparing(OrderLine::getPosition));

                for (OrderLine orderLine: ticket.getOrderLines()) {

                    orderLineScrollBox.add(createOrderLineHBox(orderLine));
                    setOrderLineStyle(orderLine, orderLineScrollBox);

                }

            }

        }

    }

    public void onSendAndCloseBtnClick() {

        if (ticketsDs.getItem(currentTicketId) != null && !doNotPrint) {

            printTicket(ticketsDs.getItem(currentTicketId));

            ticketsDs.getItem(currentTicketId).setTicketStatus(TicketStatus.sended);
            dataManager.commit(ticketsDs.getItem(currentTicketId));

        }

        tableItemDs.getItem().setChecked(false);
        tableItemDs.commit();

        getWindowManager().close(this);
        openWindow("tableselect", WindowManager.OpenType.THIS_TAB);

    }

    public void onSendBtnClick() {

        if (ticketsDs.getItem(currentTicketId) == null) return;

        if (!doNotPrint) printTicket(ticketsDs.getItem(currentTicketId));

        ticketsDs.getItem(currentTicketId).setTicketStatus(TicketStatus.sended);

        dataManager.commit(ticketsDs.getItem(currentTicketId));

        tableItemDs.refresh();

        drawOrderLinesGrid(null, null);

        currentTicketId = null;

    }

    public void onReSendBtnClick() {

        for (Ticket ticket : ticketsDs.getItems()) {

            if (!doNotPrint) printTicket(ticket);

            if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {

                ticket.setTicketStatus(TicketStatus.sended);
                dataManager.commit(ticket);

            }

        }

        tableItemDs.refresh();

        drawOrderLinesGrid(null, null);

        currentTicketId = null;

    }

    public void onBillBtnClick() {

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);

        if (printServices[0] != null) {

            MediaPrintableArea mpa = new MediaPrintableArea(1, 1, 74, 2000, MediaPrintableArea.MM);

            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.ISO_A0);
            printRequestAttributeSet.add(mpa);

            DocAttributeSet docAttributeSet = new HashDocAttributeSet();
            docAttributeSet.add(MediaSizeName.ISO_A0);

            docAttributeSet.add(mpa);

            OrderScreenOLD.Bill bill = new OrderScreenOLD.Bill();

            DocPrintJob docPrintJob = printServices[0].createPrintJob();
            SimpleDoc doc1 = new SimpleDoc(bill, flavor, docAttributeSet);

            try {

                docPrintJob.print(doc1, printRequestAttributeSet);

            } catch (PrintException e) {

                e.printStackTrace();

            }

        }

        tableItemDs.getItem().getCurrentOrder().setStatus(OrderStatus.bill);
        tableItemDs.getItem().setChecked(false);
        dataManager.commit(tableItemDs.getItem().getCurrentOrder());
        tableItemDs.commit();

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
                int xMin = (int) pageFormat.getImageableX() + 1;
                int y = 20;
                int paperWidth = (int) pageFormat.getImageableWidth();

                int yInc1 = font1.getSize() / 2;
                int yInc2 = font1.getSize() / 2;
                int yInc3 = font1.getSize() / 2;

                Graphics2D graphics2D = (Graphics2D) graphics;

                BufferedImage bufferedImage = null;

                try {

                    bufferedImage = ImageIO.read(new File("/home/joker/Desktop/logo3.jpg"));

                } catch (Exception e) {
                    System.err.println(e);
                }

                graphics2D.drawImage(bufferedImage, null, 30, -10);

                y += 60;
                graphics2D.setFont(font2);
                graphics2D.drawString("PRECONTO TAVOLO: ".concat(tableItemDs.getItem().getCurrentOrder().getTableItemCaption()), xMin, y);
                y += 20;

                graphics2D.drawLine(xMin, y, paperWidth, y);

                y = y + 2 * yInc2;

                for (Ticket ticket : ticketsDs.getItems()) for (OrderLine line : ticket.getOrderLines()) if (line.getTicket().getOrder().equals(tableItemDs.getItem().getCurrentOrder())) {

                    if (!line.getIsModifier()) graphics2D.drawString(line.getQuantity().toString(), xMin, y);

                    Integer linesToDraw = Math.round(line.getItemName().length() / 24) + 1;

                    String stringToDraw = "";

                    int spacePosition = 0;
                    int currentSpacePosition = 0;

                    for (int l = 1; l < linesToDraw; l++) {

                        String lineName = line.getItemName();

                        for (int i = spacePosition; i < line.getItemName().length(); i++) {

                            Character c = lineName.charAt(i);

                            if (Character.isWhitespace(c)) {

                                if (i > 24 * l) break;

                                spacePosition = i;

                            }

                        }

                        graphics2D.drawString(lineName.substring(currentSpacePosition, spacePosition), xMin + font2.getSize(), y);

                        currentSpacePosition = spacePosition;

                        if (l == 1 && !line.getIsModifier()) {

                            x = paperWidth - Math.multiplyExact(line.getPrice().toString().length(), font2.getSize() - 3);
                            graphics2D.drawString(line.getPrice().toString(), x, y);

                        }

                        y = y + yInc2 + 1;

                    }

                    graphics2D.drawString(line.getItemName().substring(currentSpacePosition), xMin + font2.getSize(), y);

                    if (currentSpacePosition == 0 && !line.getIsModifier()) {

                        x = paperWidth - Math.multiplyExact(line.getPrice().toString().length(), font2.getSize() - 3);
                        graphics2D.drawString(line.getPrice().toString(), x, y);

                    }

                    y = y + yInc2 + 4;

                }

                graphics2D.drawLine(xMin, y, paperWidth, y);
                y = y + 2 * yInc2;
                graphics2D.setFont(font3);
                graphics2D.drawString("SUBTOTALE", xMin, y);
                x = paperWidth - Math.multiplyExact(tableItemDs.getItem().getCurrentOrder().getCharge().toString().length(), font3.getSize() - 3);
                graphics2D.drawString(tableItemDs.getItem().getCurrentOrder().getCharge().toString(), x, y);
                y = y + yInc3 + 3;
                graphics2D.drawString("SERVIZIO", xMin, y);
                x = paperWidth - Math.multiplyExact(tableItemDs.getItem().getCurrentOrder().getTaxes().toString().length(), font3.getSize() - 3);
                graphics2D.drawString(tableItemDs.getItem().getCurrentOrder().getTaxes().toString(), x, y);
                y = y + yInc3 + 20;

                graphics2D.setFont(font1);

                graphics2D.drawString("TOTALE", xMin, y);
                x = paperWidth - 4 - Math.multiplyExact(tableItemDs.getItem().getCurrentOrder().getCharge().add(tableItemDs.getItem().getCurrentOrder().getTaxes()).toString().length(), font1.getSize() - 7);
                graphics2D.drawString(tableItemDs.getItem().getCurrentOrder().getCharge().add(tableItemDs.getItem().getCurrentOrder().getTaxes()).toString(), x, y);
                y = y + yInc3 + 10;

                graphics2D.setFont(font2);

                graphics2D.drawString("Coperti: ".concat(tableItemDs.getItem().getCurrentOrder().getActualSeats().toString()), xMin, y);
                y = y + 2 * yInc3;
                graphics2D.setFont(font2);
                graphics2D.drawString("NON FISCALE", 60, y);

                return Printable.PAGE_EXISTS;
            }

            return Printable.NO_SUCH_PAGE;

        }

    }

    private void printTicket(Ticket ticketToPrint) {

        withFries = false;
        isGrillTicket = false;

        for (PrinterGroup printerGroup: PrinterGroup.values()) {

            printerGroupToSendTicket = printerGroup;

            boolean printerGroupLinesExixts = false;

            for (OrderLine line: ticketToPrint.getOrderLines())
                if (line.getPrinterGroup().equals(printerGroupToSendTicket)) printerGroupLinesExixts = true;

            if (printerGroupToSendTicket.equals(PrinterGroup.Fryer) && printerGroupLinesExixts) withFries = true;
            if (printerGroupToSendTicket.equals(PrinterGroup.Grill)) isGrillTicket = true;

            if (printerGroupLinesExixts) {

                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

                PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);

                if (printServices[0] != null) {

                    MediaPrintableArea mpa = new MediaPrintableArea(1, 1, 74, 2000, MediaPrintableArea.MM);

                    PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                    printRequestAttributeSet.add(MediaSizeName.ISO_A0);
                    printRequestAttributeSet.add(mpa);

                    DocAttributeSet docAttributeSet = new HashDocAttributeSet();
                    docAttributeSet.add(MediaSizeName.ISO_A0);

                    docAttributeSet.add(mpa);

                    OrderScreenOLD.PrinterTicket printerticket = new OrderScreenOLD.PrinterTicket();
                    printerticket.setTicketToPrint(ticketToPrint);

                    DocPrintJob docPrintJob = printServices[0].createPrintJob();
                    SimpleDoc doc1 = new SimpleDoc(printerticket, flavor, docAttributeSet);

                    try {

                        docPrintJob.print(doc1, printRequestAttributeSet);

                    } catch (PrintException e) {

                        e.printStackTrace();

                    }

                }

            }

        }

    }

    class PrinterTicket implements Printable {

        private Ticket ticket;

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

            if (pageIndex == 0) {

                Font font1 = new Font("ZapfDingbats", Font.BOLD, 18);
                Font font2 = new Font("ZapfDingbats", Font.PLAIN, 14);
                Font font3 = new Font("ZapfDingbats", Font.BOLD, 12);

                int xMin = (int) pageFormat.getImageableX() + 1;
                int y = 20;

                int yInc = 12;


                Graphics2D graphics2D = (Graphics2D) graphics;
                graphics2D.setFont(font1);
                graphics2D.drawString(printerGroupToSendTicket.toString().toUpperCase(), xMin + 70, y);
                y += 30;
                graphics2D.drawString("TAVOLO: ".concat(tableItemDs.getItem().getTableCaption()), xMin, y);
                y += 30;
                graphics2D.setFont(font3);
                graphics2D.drawString("Coperti: ".concat(tableItemDs.getItem().getCurrentOrder().getActualSeats().toString()), xMin, y);
                y += 20;
                java.util.Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                graphics2D.drawString("Time: ".concat(sdf.format(cal.getTime())), xMin, y);
                y += 30;

                if (isGrillTicket && !withFries) {

                    graphics2D.drawString("NO FRITTURE", xMin + 10, y);
                    y += 30;

                }

                graphics2D.setFont(font2);

                for (OrderLine line : ticket.getOrderLines()) {

                    if (line.getPrinterGroup().equals(printerGroupToSendTicket) && line.getTicket().getTicketStatus().equals(TicketStatus.notSended)) {

                        if (!line.getIsModifier()) graphics2D.drawString(line.getQuantity().toString(), xMin, y);

                        Integer linesToDraw = Math.round(line.getItemName().length() / 20) + 1;

                        int spacePosition = 0;
                        int currentSpacePosition = 0;

                        for (int l = 1; l < linesToDraw; l++) {

                            String lineName = line.getItemName();

                            for (int i = spacePosition; i < line.getItemName().length(); i++) {

                                Character c = lineName.charAt(i);

                                if (Character.isWhitespace(c)) {

                                    if (i > 20 * l) break;

                                    spacePosition = i;

                                }

                            }

                            graphics2D.drawString(lineName.substring(currentSpacePosition, spacePosition), xMin + font2.getSize(), y);

                            currentSpacePosition = spacePosition;

                            y = y + yInc + 4;

                        }

                        graphics2D.drawString(line.getItemName().substring(currentSpacePosition), xMin + font2.getSize(), y);

                        y = y + yInc + 8;

                    }

                }

                graphics2D.drawString(".", xMin, y);

                return Printable.PAGE_EXISTS;
            }

            return Printable.NO_SUCH_PAGE;

        }

        public void setTicketToPrint(Ticket ticketToPrint) {

            ticket = ticketToPrint;

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

        tableItemDs.getItem().setChecked(false);
        tableItemDs.commit();

        getWindowManager().close(this);
        openWindow("tableselect", WindowManager.OpenType.THIS_TAB);

    }

    private HBoxLayout createOrderLineHBox(OrderLine orderLine) {

        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);

        hBoxLayout.setId("hBoxLayout".concat(orderLine.getId().toString()));

        Label quantity = componentsFactory.createComponent(Label.class);
        Button itemName = componentsFactory.createComponent(Button.class);
        Label price = componentsFactory.createComponent(Label.class);

        quantity.setWidth("20px");
        itemName.setWidth("475px");
        price.setWidth("55px");

        quantity.setHeight("40px");
        itemName.setHeight("40px");
        price.setHeight("40px");

        quantity.setId("quantity".concat(orderLine.getId().toString()));
        itemName.setId("itemName".concat(orderLine.getId().toString()));
        price.setId("price".concat(orderLine.getId().toString()));

        quantity.setAlignment(Component.Alignment.MIDDLE_LEFT);
        price.setAlignment(Component.Alignment.MIDDLE_RIGHT);

        itemName.setAction(new OrderScreenOLD.SelectCurrentLineAction());

        if (!orderLine.getIsModifier()) quantity.setValue(orderLine.getQuantity());

        if (orderLine.getItemName().length() < 50) itemName.setCaption(orderLine.getItemName());
        else
            itemName.setCaption(orderLine.getItemName().substring(0, 27).concat("...").concat(orderLine.getItemName().substring(orderLine.getItemName().length() - 20)));
        if (orderLine.getIsModifier() && !orderLine.getPrice().toString().equals("0.00")) {

            if (orderLine.getPrice().toString().charAt(0) == '-')
                price.setValue("(".concat(orderLine.getPrice().toString()).concat(")"));
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

            if (orderLine.getId().equals(selectedLineId)) {

                if (orderLine.getTicket().getTicketStatus().equals(TicketStatus.sended)) {

                    if (orderLine.getIsModifier()) {

                        if (orderLine.getIsReversed()) {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-selected-isModifier-isSended-even");
                                itemName.setStyleName("gridItem-button-selected-isModifier-isSended-isReversed-even");
                                price.setStyleName("gridItem-label-selected-isModifier-isSended-isReversed-even");

                            } else {

                                quantity.setStyleName("gridItem-label-selected-isModifier-isSended-odd");
                                itemName.setStyleName("gridItem-button-selected-isModifier-isSended-isReversed-odd");
                                price.setStyleName("gridItem-label-selected-isModifier-isSended-isReversed-odd");

                            }

                        } else {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-selected-isModifier-isSended-even");
                                itemName.setStyleName("gridItem-button-selected-isModifier-isSended-even");
                                price.setStyleName("gridItem-label-selected-isModifier-isSended-even");

                            } else {

                                quantity.setStyleName("gridItem-label-selected-isModifier-isSended-odd");
                                itemName.setStyleName("gridItem-button-selected-isModifier-isSended-odd");
                                price.setStyleName("gridItem-label-selected-isModifier-isSended-odd");

                            }

                        }

                    } else {

                        if (orderLine.getIsReversed()) {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-selected-isSended-even");
                                itemName.setStyleName("gridItem-button-selected-isSended-isReversed-even");
                                price.setStyleName("gridItem-label-selected-isSended-isReversed-even");

                            } else {

                                quantity.setStyleName("gridItem-label-selected-isSended-odd");
                                itemName.setStyleName("gridItem-button-selected-isSended-isReversed-odd");
                                price.setStyleName("gridItem-label-selected-isSended-isReversed-odd");

                            }

                        } else {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-selected-isSended-even");
                                itemName.setStyleName("gridItem-button-selected-isSended-even");
                                price.setStyleName("gridItem-label-selected-isSended-even");

                            } else {

                                quantity.setStyleName("gridItem-label-selected-isSended-odd");
                                itemName.setStyleName("gridItem-button-selected-isSended-odd");
                                price.setStyleName("gridItem-label-selected-isSended-odd");

                            }

                        }

                    }

                } else {

                    if (orderLine.getIsModifier()) {

                        quantity.setStyleName("gridItem-label-selected-isModifier");
                        itemName.setStyleName("gridItem-button-selected-isModifier");
                        price.setStyleName("gridItem-label-selected-isModifier");

                    } else {

                        quantity.setStyleName("gridItem-label-selected");
                        itemName.setStyleName("gridItem-button-selected");
                        price.setStyleName("gridItem-label-selected");

                    }

                }

            } else {

                if (orderLine.getTicket().getTicketStatus().equals(TicketStatus.sended)) {

                    if (orderLine.getIsModifier()) {

                        if (orderLine.getIsReversed()) {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-isModifier-isSended-isReversed-even");
                                itemName.setStyleName("gridItem-button-isModifier-isSended-isReversed-even");
                                price.setStyleName("gridItem-label-isModifier-isSended-isReversed-even");

                            } else {

                                quantity.setStyleName("gridItem-label-isModifier-isSended-isReversed-odd");
                                itemName.setStyleName("gridItem-button-isModifier-isSended-isReversed-odd");
                                price.setStyleName("gridItem-label-isModifier-isSended-isReversed-odd");

                            }

                        } else {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-isModifier-isSended-even");
                                itemName.setStyleName("gridItem-button-isModifier-isSended-even");
                                price.setStyleName("gridItem-label-isModifier-isSended-even");

                            } else {

                                quantity.setStyleName("gridItem-label-isModifier-isSended-odd");
                                itemName.setStyleName("gridItem-button-isModifier-isSended-odd");
                                price.setStyleName("gridItem-label-isModifier-isSended-odd");

                            }

                        }

                    } else {

                        if (orderLine.getIsReversed()) {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-isSended-isReversed-even");
                                itemName.setStyleName("gridItem-button-isSended-isReversed-even");
                                price.setStyleName("gridItem-label-isSended-isReversed-even");

                            } else {

                                quantity.setStyleName("gridItem-label-isSended-isReversed-odd");
                                itemName.setStyleName("gridItem-button-isSended-isReversed-odd");
                                price.setStyleName("gridItem-label-isSended-isReversed-odd");

                            }

                        } else {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-isSended-even");
                                itemName.setStyleName("gridItem-button-isSended-even");
                                price.setStyleName("gridItem-label-isSended-even");

                            } else {

                                quantity.setStyleName("gridItem-label-isSended-odd");
                                itemName.setStyleName("gridItem-button-isSended-odd");
                                price.setStyleName("gridItem-label-isSended-odd");

                            }

                        }

                    }

                } else {

                    if (orderLine.getIsModifier()) {

                        quantity.setStyleName("gridItem-label-isModifier");
                        itemName.setStyleName("gridItem-button-isModifier");
                        price.setStyleName("gridItem-label-isModifier");

                    } else {

                        quantity.setStyleName("gridItem-label");
                        itemName.setStyleName("gridItem-button");
                        price.setStyleName("gridItem-label");

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

            UUID newLineToSelectId = UUID.fromString(itemNameBtn.getId().substring(8));
            UUID newLineToDeselectId = selectedLineId;

            if (newLineToSelectId.equals(selectedLineId)) for (Ticket ticket: ticketsDs.getItems()) for (OrderLine orderLine: ticket.getOrderLines()) {

                if (orderLine.getId().equals(selectedLineId)) {

                    selectedLineId = null;
                    setOrderLineStyle(orderLine, orderLineScrollBox);
                    return;

                }

            } else {

                OrderLine newLineToDeselect = null;
                OrderLine newLineToSelect = null;

                for (Ticket ticket: ticketsDs.getItems()) for (OrderLine orderLine: ticket.getOrderLines()) if (orderLine.getId().equals(newLineToSelectId)) newLineToSelect = orderLine;
                else if (orderLine.getId().equals(newLineToDeselectId)) newLineToDeselect = orderLine;

                selectedLineId = newLineToSelectId;
                if (newLineToSelect != null) setOrderLineStyle(newLineToSelect, orderLineScrollBox);
                if (newLineToDeselect != null) setOrderLineStyle(newLineToDeselect, orderLineScrollBox);

            }

        }

    }

    public void onAddModifierClick() {

        openWindow("jokerapp$ItemModifier.dialog", WindowManager.OpenType.DIALOG);

    }


*/

}