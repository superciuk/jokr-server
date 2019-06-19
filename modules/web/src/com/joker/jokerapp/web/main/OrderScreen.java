package com.joker.jokerapp.web.main;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.*;
import com.joker.jokerapp.web.popups.ItemManualModifier;
import com.joker.jokerapp.web.popups.ItemModifier;
import com.joker.jokerapp.web.popups.ItemPriceManualModifier;

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

@UiController("jokerapp_OrderScreen")
@UiDescriptor("order-screen.xml")
public class OrderScreen extends Screen {

    @Inject
    private Metadata metadata;

    @Inject
    private UiComponents uiComponents;

    @Inject
    private ScreenBuilders screenBuilders;

    @Inject
    private DataContext dataContext;

    @Inject
    private InstanceContainer<Ticket> currentTicketDc;

    @Inject
    private InstanceContainer<OrderLine> orderLineDc;

    @Inject
    private CollectionContainer<ProductItemCategory> productItemCategoriesDc;

    @Inject
    private CollectionContainer<ProductItem> productItemsDc;

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

    private int actualSeats;

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

    private boolean resendTicket = false;

    private boolean withFries = false;
    private boolean isGrillTicket = false;

    private boolean clientIsTablet = true;

    private OrderLine selectedLine;

    private TableItem tableItem;

    @Subscribe
    private void onBeforeShow(BeforeShowEvent event) {


        /*--------------------------*/

        doNotPrint = true;
        doNotPrintBtn.setStyleName("doNotPrintBtnPushed");
        doNotPrintBtn.setCaption("NON STAMPARE<br>LE COMANDE");

        /*--------------------------*/


        getScreenData().loadAll();

        if (tableItem.getTableStatus().equals(TableItemStatus.free)) {

            tableItem.setCurrentOrder(metadata.create(Order.class));
            tableItem.getCurrentOrder().setStatus(OrderStatus.open);
            tableItem.getCurrentOrder().setTableItemCaption(tableItem.getTableCaption());
            tableItem.getCurrentOrder().setActualSeats(actualSeats);
            tableItem.getCurrentOrder().setCharge(BigDecimal.valueOf(0));
            tableItem.getCurrentOrder().setTaxes(BigDecimal.valueOf(0));

            if (tableItem.getWithServiceByDefault()) tableItem.getCurrentOrder().setWithService(true);
            else tableItem.getCurrentOrder().setWithService(false);

            tableItem.setTableStatus(TableItemStatus.open);

        } else {

            if (tableItem.getCurrentOrder().getTickets() != null) {

                for (Ticket ticket: tableItem.getCurrentOrder().getTickets()) if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {

                    currentTicketDc.setItem(dataContext.getParent().merge(ticket));
                    break;

                }

                drawOrderLinesGrid(null, null);

                refreshBill();

            }

        }

        //tableItem.setChecked(true);

        dataContext.getParent().commit();

        categoriesGrid.removeAll();

        if (productItemCategoriesDc.getItems() == null) return;

        categoriesActualPage = 1;
        productItemCategoriesToShow.clear();

        for (ProductItemCategory item: productItemCategoriesDc.getItems()) {

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

            WebButton cBtn = uiComponents.create(WebButton.class);

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

        if (productItemsDc.getItems() == null) return;

        productItemsActualPage = 1;
        productItemsToShow.clear();

        for (ProductItem item: productItemsDc.getItems()) {

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

            WebButton pBtn = uiComponents.create(WebButton.class);
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

        if (tableItem.getCurrentOrder().getTickets() != null) {

            for (Ticket ticket: tableItem.getCurrentOrder().getTickets()) for (OrderLine line: ticket.getOrderLines()) {

                if (!line.getIsModifier() && line.getPosition() > max) {

                    max = line.getPosition();

                }

            }

        }

        max += 100;

        if (currentTicketDc.getItemOrNull() == null) {

            currentTicketDc.setItem(dataContext.getParent().merge(metadata.create(Ticket.class)));
            currentTicketDc.getItem().setOrder(tableItem.getCurrentOrder());
            currentTicketDc.getItem().setTicketStatus(TicketStatus.notSended);
            if (tableItem.getCurrentOrder().getTickets() != null) currentTicketDc.getItem().setTicketNumber(tableItem.getCurrentOrder().getTickets().size() + 1);
            else currentTicketDc.getItem().setTicketNumber(1);
            currentTicketDc.getItem().setSubticketStatus("bn-fn-gn");
            currentTicketDc.getItem().setOrderLines(new ArrayList<>());

            if (tableItem.getCurrentOrder().getTickets() != null) tableItem.getCurrentOrder().getTickets().add(currentTicketDc.getItem());
            else {

                tableItem.getCurrentOrder().setTickets(new ArrayList<>());
                tableItem.getCurrentOrder().getTickets().add(currentTicketDc.getItem());

            }

        } else for (OrderLine line: currentTicketDc.getItem().getOrderLines()) if (!line.getIsModifier() && !line.getHasModifier() && line.getItemId().equals(productItemToAdd.getId())) {

            orderLineDc.setItem(line);

            dataContext.getParent().merge(orderLineDc.getItem());

            orderLineDc.getItem().setQuantity(line.getQuantity() + 1);
            orderLineDc.getItem().setPrice(line.getPrice().setScale(2).add(productItemToAdd.getPrice().setScale(2)));

            dataContext.getParent().commit();

            drawOrderLinesGrid(orderLineDc.getItem(), "updated");

            refreshBill();

            return;

        }

        orderLineDc.setItem(dataContext.getParent().merge(metadata.create(OrderLine.class)));
        orderLineDc.getItem().setQuantity(1);
        orderLineDc.getItem().setItemName(productItemToAdd.getName());
        orderLineDc.getItem().setItemId(productItemToAdd.getId());
        orderLineDc.getItem().setUnitPrice(productItemToAdd.getPrice().setScale(2));
        orderLineDc.getItem().setPrice(productItemToAdd.getPrice().setScale(2));
        orderLineDc.getItem().setTaxes(BigDecimal.ZERO);
        orderLineDc.getItem().setTicket(currentTicketDc.getItem());
        orderLineDc.getItem().setPosition(max);
        orderLineDc.getItem().setNextModifierPosition(max + 1);
        orderLineDc.getItem().setHasModifier(false);
        orderLineDc.getItem().setIsModifier(false);
        orderLineDc.getItem().setItemToModifyId(null);
        orderLineDc.getItem().setPrinterGroup(productItemToAdd.getPrinterGroup());

        if (orderLineDc.getItem().getPrinterGroup().equals(PrinterGroup.Bar) && currentTicketDc.getItem().getSubticketStatus().charAt(1) == 'n') {

            currentTicketDc.getItem().setSubticketStatus(currentTicketDc.getItem().getSubticketStatus().replace("bn", "bo"));

        } else if (orderLineDc.getItem().getPrinterGroup().equals(PrinterGroup.Fryer) && currentTicketDc.getItem().getSubticketStatus().charAt(4) == 'n') {

            currentTicketDc.getItem().setSubticketStatus(currentTicketDc.getItem().getSubticketStatus().replace("fn", "fo"));

        } else if (orderLineDc.getItem().getPrinterGroup().equals(PrinterGroup.Grill) && currentTicketDc.getItem().getSubticketStatus().charAt(7) == 'n') {

            currentTicketDc.getItem().setSubticketStatus(currentTicketDc.getItem().getSubticketStatus().replace("gn", "go"));

        }

        orderLineDc.getItem().setChecked(false);
        orderLineDc.getItem().setIsReversed(false);

        currentTicketDc.getItem().getOrderLines().add(orderLineDc.getItem());

        dataContext.getParent().commit();

        drawOrderLinesGrid(orderLineDc.getItem(), "added");

        refreshBill();

    }

    private void refreshBill() {

        subTotal = BigDecimal.ZERO;

        for (Ticket ticket: tableItem.getCurrentOrder().getTickets())
            for (OrderLine line: ticket.getOrderLines()) if (!line.getIsModifier() && !line.getIsReversed()) subTotal = subTotal.add(line.getPrice().setScale(2));

        tableItem.getCurrentOrder().setCharge(subTotal);

        subtotalField.setValue(subTotal.toString().concat(" €"));

        if (tableItem.getCurrentOrder().getWithService()) {

            service = BigDecimal.valueOf(Math.round(subTotal.multiply(BigDecimal.valueOf(0.1)).subtract(BigDecimal.valueOf(0.2)).multiply(BigDecimal.valueOf(2)).doubleValue()) / 2.0f).setScale(2);

            tableItem.getCurrentOrder().setTaxes(service);

            total = subTotal.add(service);

            serviceField.setValue(service.toString().concat(" €"));

            totalField.setValue(total.toString().concat(" €"));

        } else totalField.setValue(subTotal.toString().concat(" €"));

        dataContext.getParent().commit();

    }

    private void drawOrderLinesGrid(OrderLine lineToProcess, String operationPerformed) {

        if (lineToProcess != null) {

            if (operationPerformed.equals("added")) {

                HBoxLayout hBoxToAdd = createOrderLineHBox(lineToProcess);
                orderLineScrollBox.add(hBoxToAdd);

                if (selectedLine != null) {

                    for (Ticket ticket: tableItem.getCurrentOrder().getTickets())
                        for (OrderLine orderLine: ticket.getOrderLines()) if (orderLine == selectedLine) {

                        selectedLine = lineToProcess;
                        setOrderLineStyle(orderLine, orderLineScrollBox);

                    }

                } else selectedLine = lineToProcess;

                setOrderLineStyle(lineToProcess, orderLineScrollBox);

                ((Button) orderLineScrollBox.getComponent("itemName".concat(lineToProcess.getId().toString()))).focus();

                return;

            }

            if (operationPerformed.equals("removed")) {

                if (lineToProcess.getIsModifier()) for (OrderLine orderLine: lineToProcess.getTicket().getOrderLines()) {

                    if (lineToProcess.getItemToModifyId().equals(orderLine.getId())) drawOrderLinesGrid(orderLine, "updated");
                    break;

                }

                HBoxLayout hBoxToRemove = (HBoxLayout) orderLineScrollBox.getOwnComponent("hBoxLayout".concat(lineToProcess.getId().toString()));

                int index = orderLineScrollBox.indexOf(hBoxToRemove);

                orderLineScrollBox.remove(hBoxToRemove);

                if (orderLineScrollBox.getOwnComponents().size() == 0) {

                    selectedLine = null;

                    return;

                }

                if (index == orderLineScrollBox.getOwnComponents().size()) index--;

                HBoxLayout hBoxToSelect = (HBoxLayout) orderLineScrollBox.getComponent(index);

                for (Ticket ticket: tableItem.getCurrentOrder().getTickets())
                    for (OrderLine orderLine: ticket.getOrderLines()) if (orderLine.getId().equals(UUID.fromString(hBoxToSelect.getId().substring(10)))) {

                    if (lineToProcess != selectedLine) {

                        for (Ticket tkt: tableItem.getCurrentOrder().getTickets()) for (OrderLine line: tkt.getOrderLines()) if (orderLine == selectedLine) {

                            selectedLine = orderLine;

                            setOrderLineStyle(line, orderLineScrollBox);

                        }

                    } else selectedLine = orderLine;

                    setOrderLineStyle(orderLine, orderLineScrollBox);
                    return;

                }

            }

            if (operationPerformed.equals("updated")) {

                if (!lineToProcess.getIsModifier()) {

                    Label quantityToChange = (Label) orderLineScrollBox.getComponent("quantity".concat(lineToProcess.getId().toString()));
                    quantityToChange.setValue(lineToProcess.getQuantity());

                }

                if (!lineToProcess.getPrice().setScale(2).equals(BigDecimal.ZERO.setScale(2))) {

                    Label priceToChange = (Label) orderLineScrollBox.getComponent("price".concat(lineToProcess.getId().toString()));

                    if (lineToProcess.getIsModifier()) {

                        if (lineToProcess.getPrice().setScale(2).toString().charAt(0) == '-')
                            priceToChange.setValue("(".concat(lineToProcess.getPrice().setScale(2).toString()).concat(")"));
                        else priceToChange.setValue("(+".concat(lineToProcess.getPrice().setScale(2).toString()).concat(")"));

                    } else priceToChange.setValue(lineToProcess.getPrice().setScale(2).toString());

                }

                if (lineToProcess != selectedLine) for (Ticket ticket: tableItem.getCurrentOrder().getTickets())
                    for (OrderLine orderLine: ticket.getOrderLines()) if (orderLine == selectedLine) {

                    selectedLine = lineToProcess;
                    setOrderLineStyle(orderLine, orderLineScrollBox);

                }

                setOrderLineStyle(lineToProcess, orderLineScrollBox);

            }

        } else {

            orderLineScrollBox.removeAll();

            tableItem.getCurrentOrder().getTickets().sort(Comparator.comparing(Ticket::getTicketNumber));

            for (Ticket ticket: tableItem.getCurrentOrder().getTickets()) {

                ticket.getOrderLines().sort(Comparator.comparing(OrderLine::getPosition));

                for (OrderLine orderLine: ticket.getOrderLines()) {

                    orderLineScrollBox.add(createOrderLineHBox(orderLine));
                    setOrderLineStyle(orderLine, orderLineScrollBox);

                }

            }

        }

    }

    private HBoxLayout createOrderLineHBox(OrderLine orderLine) {

        HBoxLayout hBoxLayout = uiComponents.create(HBoxLayout.class);

        hBoxLayout.setId("hBoxLayout".concat(orderLine.getId().toString()));

        Label quantity = uiComponents.create(Label.class);
        Button itemName = uiComponents.create(Button.class);
        Label price = uiComponents.create(Label.class);

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

        itemName.setAction(new OrderScreen.SelectCurrentLineAction());

        if (!orderLine.getIsModifier()) quantity.setValue(orderLine.getQuantity());

        if (orderLine.getItemName().length() < 50) itemName.setCaption(orderLine.getItemName());
        else itemName.setCaption(orderLine.getItemName().substring(0, 27).concat("...").concat(orderLine.getItemName().substring(orderLine.getItemName().length() - 20)));

        if (orderLine.getIsModifier() && !orderLine.getPrice().setScale(2).equals(BigDecimal.ZERO.setScale(2))) {

            if (orderLine.getPrice().setScale(2).toString().charAt(0) == '-')
                price.setValue("(".concat(orderLine.getPrice().setScale(2).toString()).concat(")"));
            else price.setValue("(+".concat(orderLine.getPrice().setScale(2).toString()).concat(")"));

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

            if (orderLine == selectedLine) {

                if (orderLine.getTicket().getTicketStatus().equals(TicketStatus.sended) || orderLine.getTicket().getTicketStatus().equals(TicketStatus.closed)) {

                    if (orderLine.getIsModifier()) {

                        if (orderLine.getIsReversed()) {

                            if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                                quantity.setStyleName("gridItem-label-selected-isModifier-isSended-isReversed-even");
                                itemName.setStyleName("gridItem-button-selected-isModifier-isSended-isReversed-even");
                                price.setStyleName("gridItem-label-selected-isModifier-isSended-isReversed-even");

                            } else {

                                quantity.setStyleName("gridItem-label-selected-isModifier-isSended-isReversed-odd");
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

                                quantity.setStyleName("gridItem-label-selected-isSended-isReversed-even");
                                itemName.setStyleName("gridItem-button-selected-isSended-isReversed-even");
                                price.setStyleName("gridItem-label-selected-isSended-isReversed-even");

                            } else {

                                quantity.setStyleName("gridItem-label-selected-isSended-isReversed-odd");
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

                if (orderLine.getTicket().getTicketStatus().equals(TicketStatus.sended) || orderLine.getTicket().getTicketStatus().equals(TicketStatus.closed)) {

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

            OrderLine newLineToSelect = dataContext.getParent().find(OrderLine.class, UUID.fromString(itemNameBtn.getId().substring(8)));

            if (newLineToSelect == selectedLine) {

                    selectedLine = null;
                    setOrderLineStyle(newLineToSelect, orderLineScrollBox);
                    return;

            } else {

                OrderLine newLineToDeselect = selectedLine;

                selectedLine = newLineToSelect;

                setOrderLineStyle(newLineToSelect, orderLineScrollBox);
                if (newLineToDeselect != null) setOrderLineStyle(newLineToDeselect, orderLineScrollBox);

            }

        }

    }

    public void setTableItem(TableItem selectedTable, String seats) {

        tableItem = selectedTable;
        if (seats != null) actualSeats = Integer.parseInt(seats);

    }

    public void setParentDataContext(DataContext parentDataContext) {

        dataContext.setParent(parentDataContext);

    }

    private void removeEmptyTickets() {

        ArrayList<Ticket> removeList = new ArrayList<>();

        for (Ticket ticket: tableItem.getCurrentOrder().getTickets()) if (ticket.getOrderLines().size() == 0) {

            if (ticket == currentTicketDc.getItem()) currentTicketDc.setItem(null);
            removeList.add(ticket);

        }

        removeList.forEach(ticket -> dataContext.getParent().remove(ticket));

    }

    @Subscribe("doNotPrintBtn")
    public void onDoNotPrintBtnClick(Button.ClickEvent event) {

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

    @Subscribe("addBtn")
    protected void onAddBtnClick(Button.ClickEvent event) {

        if (selectedLine == null || selectedLine.getIsModifier()) return;

        if (selectedLine.getTicket().getTicketStatus().equals(TicketStatus.sended) || selectedLine.getTicket().getTicketStatus().equals(TicketStatus.closed)) {

            if (productItemsDc.getItems().size() == 0) getScreenData().loadAll();

            addToOrder(productItemsDc.getItem(selectedLine.getItemId()));

        } else {

            BigDecimal SingleModifiersPrice = (selectedLine.getPrice().setScale(2).subtract(selectedLine.getUnitPrice().setScale(2).
                    multiply(BigDecimal.valueOf(selectedLine.getQuantity()))).divide(BigDecimal.
                    valueOf(selectedLine.getQuantity()), RoundingMode.FLOOR));

            selectedLine.setQuantity(selectedLine.getQuantity() + 1);
            selectedLine.setPrice(selectedLine.getPrice().setScale(2).add(selectedLine.getUnitPrice()).add(SingleModifiersPrice).setScale(2));

            dataContext.getParent().commit();

            drawOrderLinesGrid(selectedLine, "updated");

            refreshBill();

        }

    }

    @Subscribe("subtractBtn")
    public void onSubtractBtnClick(Button.ClickEvent event) {

        if (selectedLine == null) return;

        if (selectedLine.getIsModifier() || selectedLine.getTicket() != currentTicketDc.getItemOrNull()) return;

        if (selectedLine.getHasModifier()) if (selectedLine.getQuantity().equals(1)) {

            ArrayList<OrderLine> removeList = new ArrayList<>();

            for (OrderLine line: selectedLine.getTicket().getOrderLines()) if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId())) removeList.add(line);

            removeList.forEach(orderLine -> {dataContext.getParent().remove(orderLine); drawOrderLinesGrid(orderLine, "removed");});

            dataContext.getParent().remove(selectedLine);

            dataContext.getParent().commit();
            drawOrderLinesGrid(selectedLine, "removed");
            refreshBill();
            return;

        }

        for (OrderLine line: selectedLine.getTicket().getOrderLines())
            if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId()))
                selectedLine.setPrice(selectedLine.getPrice().setScale(2).subtract(line.getUnitPrice()));

        if (selectedLine.getQuantity().equals(1)) {

            dataContext.getParent().remove(selectedLine);

            drawOrderLinesGrid(selectedLine, "removed");

        } else {

            selectedLine.setQuantity(selectedLine.getQuantity() - 1);
            selectedLine.setPrice(selectedLine.getPrice().setScale(2).subtract(selectedLine.getUnitPrice()));

            drawOrderLinesGrid(selectedLine, "updated");

        }

        dataContext.getParent().commit();

        refreshBill();

    }

    @Subscribe("removeBtn")
    public void onRemoveBtnClick(Button.ClickEvent event) {

        if (selectedLine == null) return;

        OrderLine lineToRemove = selectedLine;

        Ticket lineToRemoveTicket = lineToRemove.getTicket();

        if (lineToRemoveTicket != currentTicketDc.getItemOrNull()) {

            if (lineToRemove.getIsModifier()) return;

            if (lineToRemove.getHasModifier()) for (OrderLine orderLine: lineToRemoveTicket.getOrderLines())
                if (orderLine.getIsModifier() && orderLine.getItemToModifyId().equals(lineToRemove.getId())) {

                orderLine.setIsReversed(true);
                orderLine.setChecked(true);
                drawOrderLinesGrid(orderLine,"updated");

            }

            lineToRemove.setIsReversed(true);
            lineToRemove.setChecked(true);
            dataContext.getParent().commit();

            drawOrderLinesGrid(lineToRemove,"updated");

            refreshBill();

        } else {

            if (lineToRemove.getIsModifier()) {

                orderLineDc.setItem(dataContext.find(OrderLine.class, lineToRemove.getItemToModifyId()));

                orderLineDc.getItem().setPrice(orderLineDc.getItem().getPrice().setScale(2).subtract(lineToRemove.getPrice().setScale(2).multiply(BigDecimal.valueOf(lineToRemove.getQuantity()))));

                dataContext.getParent().remove(lineToRemove);

                drawOrderLinesGrid(lineToRemove, "removed");

                boolean modifiedItemHasMoreModifier = false;

                for (OrderLine line: lineToRemoveTicket.getOrderLines()) if (line != orderLineDc.getItem() && line.getIsModifier() && line.getItemToModifyId().equals(orderLineDc.getItem().getId())) {

                    modifiedItemHasMoreModifier = true;
                    break;

                }

                if (!modifiedItemHasMoreModifier) {

                    boolean orderLineModifiedDuplicateFound = false;

                    for (OrderLine line: lineToRemoveTicket.getOrderLines())
                        if (!line.getIsModifier() && line != orderLineDc.getItem() && line.getItemId().equals(orderLineDc.getItem().getItemId())) if (!line.getHasModifier()) {

                        line.setQuantity(line.getQuantity() + orderLineDc.getItem().getQuantity());
                        line.setPrice(line.getPrice().setScale(2).add(orderLineDc.getItem().getPrice().setScale(2)));

                        dataContext.getParent().remove(orderLineDc.getItem());

                        drawOrderLinesGrid(orderLineDc.getItem(), "removed");

                        drawOrderLinesGrid(line, "updated");

                        orderLineModifiedDuplicateFound = true;

                        break;

                    }

                    if (!orderLineModifiedDuplicateFound) {

                        orderLineDc.getItem().setHasModifier(false);

                        drawOrderLinesGrid(orderLineDc.getItem(), "updated");

                    }

                }

                dataContext.getParent().commit();

                refreshBill();

                return;

            } else if (lineToRemove.getHasModifier()) {

                ArrayList<OrderLine> removeList = new ArrayList<>();

                for (OrderLine line: lineToRemoveTicket.getOrderLines()) if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(lineToRemove.getId())) removeList.add(line);

                removeList.forEach(orderLine -> { dataContext.getParent().remove(orderLine); drawOrderLinesGrid(orderLine, "removed"); });

            }

            dataContext.getParent().remove(lineToRemove);

            drawOrderLinesGrid(lineToRemove, "removed");

            dataContext.getParent().commit();

            refreshBill();

        }

    }

    @Subscribe("addManualModifierBtn")
    public void onAddManualModifierBtnClick(Button.ClickEvent event) {

        if (selectedLine == null || selectedLine.getTicket().getTicketStatus().equals(TicketStatus.sended) || selectedLine.getTicket().getTicketStatus().equals(TicketStatus.closed) || selectedLine.getIsModifier()) return;

        orderLineDc.setItem(dataContext.merge(metadata.create(OrderLine.class)));

        ItemManualModifier itemManualModifier = screenBuilders.screen(this)
                .withScreenClass(ItemManualModifier.class)
                .withAfterCloseListener(itemManualModifierAfterScreenCloseEvent -> {
                    ItemManualModifier screen = itemManualModifierAfterScreenCloseEvent.getScreen();
                    if (itemManualModifierAfterScreenCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION)) {

                        String itemModifierName = screen.getItemModifierName();
                        BigDecimal itemModifierPrice = screen.getItemModifierPrice();

                        orderLineDc.getItem().setItemName("  * ".concat(itemModifierName));
                        orderLineDc.getItem().setUnitPrice(itemModifierPrice);
                        orderLineDc.getItem().setPrice(itemModifierPrice);

                        orderLineDc.getItem().setQuantity(1);
                        orderLineDc.getItem().setTaxes(BigDecimal.ZERO);
                        orderLineDc.getItem().setTicket(currentTicketDc.getItem());
                        orderLineDc.getItem().setPosition(selectedLine.getNextModifierPosition());
                        selectedLine.setNextModifierPosition(selectedLine.getNextModifierPosition() + 1);
                        orderLineDc.getItem().setHasModifier(false);
                        if (!selectedLine.getHasModifier()) selectedLine.setHasModifier(true);
                        orderLineDc.getItem().setIsModifier(true);
                        orderLineDc.getItem().setItemToModifyId(selectedLine.getId());
                        orderLineDc.getItem().setPrinterGroup(selectedLine.getPrinterGroup());
                        orderLineDc.getItem().setIsReversed(false);

                        selectedLine.setPrice(selectedLine.getPrice().setScale(2).add(orderLineDc.getItem().getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));

                        currentTicketDc.getItem().getOrderLines().add(orderLineDc.getItem());

                        dataContext.getParent().commit();

                        drawOrderLinesGrid(null, null);

                        refreshBill();

                    }
                })
                .build();

        itemManualModifier.show();

    }

    @Subscribe("addModifierBtn")
    public void onAddModifierBtnClick(Button.ClickEvent event) {

        if (selectedLine == null || selectedLine.getTicket().getTicketStatus().equals(TicketStatus.sended) || selectedLine.getTicket().getTicketStatus().equals(TicketStatus.closed) || selectedLine.getIsModifier()) return;

        ItemModifier itemModifier = screenBuilders.screen(this)
                .withScreenClass(ItemModifier.class)
                .withAfterCloseListener(itemModifierAfterScreenCloseEvent -> {
                    ItemModifier screen = itemModifierAfterScreenCloseEvent.getScreen();
                    if (itemModifierAfterScreenCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION)) {

                        drawOrderLinesGrid(null, null);

                        refreshBill();

                    }
                })
                .build();

        itemModifier.setParentDataContext(dataContext);
        itemModifier.setTicketDc(currentTicketDc);
        itemModifier.setLineToModify(selectedLine);
        itemModifier.show();


    }

    @Subscribe("modifyPriceBtn")
    public void onModifyPriceBtnClick(Button.ClickEvent event) {

        if (selectedLine == null || selectedLine.getIsModifier()) return;

        ItemPriceManualModifier itemPriceManualModifier = screenBuilders.screen(this)
                .withScreenClass(ItemPriceManualModifier.class)
                .withAfterCloseListener(itemPriceManualModifierAfterScreenCloseEvent -> {
                    ItemPriceManualModifier screen = itemPriceManualModifierAfterScreenCloseEvent.getScreen();
                    if (itemPriceManualModifierAfterScreenCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION)) {

                        BigDecimal newPrice = screen.getNewItemPrice();

                        if (selectedLine.getHasModifier()) {

                            BigDecimal modifierPrice = new BigDecimal(0);

                            for (Ticket ticket: tableItem.getCurrentOrder().getTickets()) for (OrderLine line: ticket.getOrderLines())
                                if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId()))
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

                        drawOrderLinesGrid(selectedLine, "updated");

                        refreshBill();

                    }
                })
                .build();

        itemPriceManualModifier.show();

    }

    @Subscribe("sendAndCloseBtn")
    public void onSendAndCloseBtnClick(Button.ClickEvent event) {

        removeEmptyTickets();

        if (currentTicketDc.getItemOrNull() != null) {

            if (!doNotPrint) printTicket(currentTicketDc.getItem());

            currentTicketDc.getItem().setTicketStatus(TicketStatus.sended);

        }

        tableItem.setChecked(false);

        dataContext.getParent().commit();

        close(WINDOW_COMMIT_AND_CLOSE_ACTION);

    }

    @Subscribe("sendBtn")
    public void onSendBtnClick(Button.ClickEvent event) {

        removeEmptyTickets();

        if (currentTicketDc.getItemOrNull() == null) return;

        if (!doNotPrint) printTicket(currentTicketDc.getItem());

        currentTicketDc.getItem().setTicketStatus(TicketStatus.sended);

        dataContext.getParent().commit();

        drawOrderLinesGrid(null, null);

        currentTicketDc.setItem(null);

    }

    @Subscribe("reSendBtn")
    public void onReSendBtnClick(Button.ClickEvent event) {

        if (doNotPrint) return;

        resendTicket = true;

        for (Ticket ticket: tableItem.getCurrentOrder().getTickets()) {

            printTicket(ticket);

            if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {

                ticket.setTicketStatus(TicketStatus.sended);


            }

        }

        resendTicket = false;

        currentTicketDc.setItem(null);

        dataContext.getParent().commit();

        drawOrderLinesGrid(null, null);

    }

    @Subscribe("closeBtn")
    public void onCloseBtnClick(Button.ClickEvent event) {

        removeEmptyTickets();

        tableItem.setChecked(false);
        dataContext.getParent().commit();

        close(WINDOW_COMMIT_AND_CLOSE_ACTION);

    }

    @Subscribe("billBtn")
    public void onBillBtnClick(Button.ClickEvent event) {

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

            OrderScreen.Bill bill = new OrderScreen.Bill();

            DocPrintJob docPrintJob = printServices[0].createPrintJob();
            SimpleDoc doc1 = new SimpleDoc(bill, flavor, docAttributeSet);

            try {

                docPrintJob.print(doc1, printRequestAttributeSet);

            } catch (PrintException e) {

                e.printStackTrace();

            }

        }

        tableItem.getCurrentOrder().setStatus(OrderStatus.bill);
        tableItem.setChecked(false);

        dataContext.getParent().commit();

        close(WINDOW_COMMIT_AND_CLOSE_ACTION);

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
                graphics2D.drawString("PRECONTO TAVOLO: ".concat(tableItem.getCurrentOrder().getTableItemCaption()), xMin, y);
                y += 20;

                graphics2D.drawLine(xMin, y, paperWidth, y);

                y = y + 2 * yInc2;

                for (Ticket ticket: tableItem.getCurrentOrder().getTickets()) for (OrderLine line: ticket.getOrderLines()) if (line.getTicket().getOrder().equals(tableItem.getCurrentOrder())) {

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
                x = paperWidth - Math.multiplyExact(tableItem.getCurrentOrder().getCharge().toString().length(), font3.getSize() - 3);
                graphics2D.drawString(tableItem.getCurrentOrder().getCharge().toString(), x, y);
                y = y + yInc3 + 3;
                graphics2D.drawString("SERVIZIO", xMin, y);
                x = paperWidth - Math.multiplyExact(tableItem.getCurrentOrder().getTaxes().toString().length(), font3.getSize() - 3);
                graphics2D.drawString(tableItem.getCurrentOrder().getTaxes().toString(), x, y);
                y = y + yInc3 + 20;

                graphics2D.setFont(font1);

                graphics2D.drawString("TOTALE", xMin, y);
                x = paperWidth - 4 - Math.multiplyExact(tableItem.getCurrentOrder().getCharge().add(tableItem.getCurrentOrder().getTaxes()).toString().length(), font1.getSize() - 7);
                graphics2D.drawString(tableItem.getCurrentOrder().getCharge().add(tableItem.getCurrentOrder().getTaxes()).toString(), x, y);
                y = y + yInc3 + 10;

                graphics2D.setFont(font2);

                graphics2D.drawString("Coperti: ".concat(tableItem.getCurrentOrder().getActualSeats().toString()), xMin, y);
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

                    OrderScreen.PrinterTicket printerticket = new OrderScreen.PrinterTicket();
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
                graphics2D.drawString("TAVOLO: ".concat(tableItem.getTableCaption()), xMin, y);
                y += 30;
                graphics2D.setFont(font3);
                graphics2D.drawString("Coperti: ".concat(tableItem.getCurrentOrder().getActualSeats().toString()), xMin, y);
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

                    if (line.getPrinterGroup().equals(printerGroupToSendTicket) && (line.getTicket().getTicketStatus().equals(TicketStatus.notSended) || resendTicket)) {

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

}