package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.*;
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

    @Named("orderLineDataGrid")
    private DataGrid orderLineDataGrid;

    @Named("subTotalField")
    private CurrencyField subTotalField;

    @Named("serviceField")
    private CurrencyField serviceField;

    @Named("totalField")
    private CurrencyField totalField;

    @Named("categoriesBackBtn")
    private Button categoriesBackBtn;

    @Named("categoriesNextBtn")
    private Button categoriesNextBtn;

    @Named("itemsBackBtn")
    private Button itemsBackBtn;

    @Named("itemsNextBtn")
    private Button itemsNextBtn;




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

    private Integer categorySize = 0;
    private Integer categoriesPages = 0;
    private Integer categoriesActualPage = 1;

    private Integer productItemSize = 0;
    private Integer productItemsPages = 0;
    private Integer productItemsActualPage = 1;

    private ProductItemCategory categoryProductItemsToShow;

    private ArrayList <ProductItem> productItemsToShow = new ArrayList<>();

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
            currentOrder.setCharge(BigDecimal.valueOf(0));
            currentOrder.setTaxes(BigDecimal.valueOf(0));

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

        List<OrderLine> lines = dataManager.load(OrderLine.class)
                .query("select e from jokerapp$OrderLine e where e.order.id = :currentOrder order by e.position")
                .parameter("currentOrder", currentOrder.getId())
                .view("order-line-view")
                .list();

        for (OrderLine line : lines) {

            orderLinesDs.includeItem(line);

        }

        refreshBill();

        itemsBackBtn.setVisible(Boolean.FALSE);
        itemsNextBtn.setVisible(Boolean.FALSE);

        productItemCategoriesDs.refresh();

        if (productItemCategoriesDs!=null) categorySize = productItemCategoriesDs.getItems().size();

        categoriesPages = (categorySize - 1) / 8 + 1;

        categoriesGrid.setColumns(2);

        if (categorySize!=0) {

        } if (categorySize<=8) {

            categoriesBackBtn.setVisible(Boolean.FALSE);
            categoriesNextBtn.setVisible(Boolean.FALSE);
            showProductCategories(0, categorySize-1);

        } else {

            categoriesBackBtn.setVisible(Boolean.FALSE);
            categoriesNextBtn.setVisible(Boolean.TRUE);
            showProductCategories(0, 7);

        }

    }

    private void showProductCategories(int start, int end) {

        Integer btnNumber = start;

        ProductItemCategory [] productItemCategoryToShow = productItemCategoriesDs.getItems().toArray(new ProductItemCategory[productItemCategoriesDs.getItems().size()]);

        for (int c = start; c<=end; c++) {

            WebButton cBtn = componentsFactory.createComponent(WebButton.class);

            cBtn.setId("cBtn".concat(btnNumber.toString()));
            cBtn.setWidth(categoryBtnWidth);
            cBtn.setHeight(categoryBtnHeight);
            cBtn.setCaptionAsHtml(Boolean.TRUE);

            Integer nameLength = productItemCategoryToShow[c].getName().length();
            String categoryName = productItemCategoryToShow[c].getName();
            if (nameLength>16 && categoryName.contains(" ")) {

                int spacePosition = 0;

                for (int l = 0; l<categoryName.length(); l++) {

                    Character ch = categoryName.charAt(l);

                    if (Character.isSpace(ch)) {

                        if (l > 16) break;

                        spacePosition = l;

                    }

                }

                categoryName = categoryName.substring(0,spacePosition).concat("<br>").concat(categoryName.substring(spacePosition+1));
                cBtn.setCaption(categoryName);

            } else cBtn.setCaption(categoryName);

            ProductItemCategory toShow = productItemCategoryToShow[c];
            cBtn.setAction(new BaseAction("showItem".concat(productItemCategoryToShow[c].getName())).withHandler(e -> showProductItems(toShow)));

            categoriesGrid.add(cBtn);

            btnNumber ++;

        }

    }

    private void showProductItems(ProductItemCategory productItemCategory) {

        itemsGrid.removeAll();
        itemsGrid.setColumns(4);
        productItemsDs.refresh();
        productItemsActualPage = 1;
        productItemsToShow.clear();

        for (ProductItem item : productItemsDs.getItems()) {

            if (item.getCategory().getName().equals(productItemCategory.getName()) && item.getVisible()) productItemsToShow.add(item);

        }

        if (productItemsToShow!=null) productItemSize = productItemsToShow.size();

        productItemsPages = (productItemSize - 1) / 16 + 1;

        if (productItemSize!=0) {

            categoryProductItemsToShow = productItemCategory;

            if (productItemSize <= 16) {

                itemsBackBtn.setVisible(Boolean.FALSE);
                itemsNextBtn.setVisible(Boolean.FALSE);
                showProductItemsPaged(0, productItemSize-1);

            } else {

                itemsBackBtn.setVisible(Boolean.FALSE);
                itemsNextBtn.setVisible(Boolean.TRUE);
                showProductItemsPaged(0, 15);

            }

        }

    }

    private void showProductItemsPaged(int start, int end) {

        Integer btnNumber = start;
        ArrayList<Integer> spacePositions = new ArrayList<>();
        ArrayList<Integer> spaceToConvert = new ArrayList<>();
        int actualSpace;
        int prevSpaceConverted;

        for (int c = start; c<=end; c++) {

            WebButton pBtn = componentsFactory.createComponent(WebButton.class);
            pBtn.setId("pBtn".concat(btnNumber.toString()));
            pBtn.setWidth(itemBtnWidth);
            pBtn.setHeight(itemBtnHeight);
            pBtn.setCaptionAsHtml(Boolean.TRUE);

            Integer nameLength = productItemsToShow.get(c).getName().length();
            String productName = productItemsToShow.get(c).getName();

            if (nameLength > 16 && productName.contains(" ")) {

                spacePositions.clear();

                for (int l = 0; l < productName.length(); l++) {

                    Character ch = productName.charAt(l);
                    if (Character.isSpace(ch)) spacePositions.add(l);

                }

                spaceToConvert.clear();
                actualSpace = spacePositions.get(0);
                prevSpaceConverted = 0;

                for (int o = 0; o<spacePositions.size(); o++) {

                    if (spacePositions.get(o) - prevSpaceConverted > 16) {

                        spaceToConvert.add(actualSpace);
                        prevSpaceConverted = actualSpace;
                        actualSpace = spacePositions.get(o);

                    } else actualSpace = spacePositions.get(o);

                }

                for (int n = 0; n<spaceToConvert.size(); n++) {

                    productName = productName.substring(0, spaceToConvert.get(n) + (n*3)).concat("<br>").concat(productName.substring(spaceToConvert.get(n) + (n*3) + 1));

                }

                pBtn.setCaption(productName);

            } else pBtn.setCaption(productItemsToShow.get(c).getName());

            btnNumber ++;

            ProductItem toAdd = productItemsToShow.get(c);
            pBtn.setAction(new BaseAction("addToOrder".concat(productItemsToShow.get(c).getName())).withHandler(e -> addToOrder(toAdd)));
            itemsGrid.add(pBtn);

            }

        }

    private void addToOrder(ProductItem productItemToAdd) {

        for (OrderLine line : orderLinesDs.getItems()) {

            if ((line.getItemName().equals(productItemToAdd.getName())) && !line.getHasModifier()) {

                line.setQuantity(line.getQuantity() + 1);
                line.setPrice(line.getPrice().add(productItemToAdd.getPrice()));

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

            orderLinesDs.addItem(newLine);

            orderLinesDs.commit();

            refreshBill();

    }

    public void onPrintBtnClick() {

        for (PrinterGroup printerGroup : PrinterGroup.values()) {

            printerGroupToSendTicket = printerGroup.toString();

            Boolean printerGroupLinesExixts = Boolean.FALSE;

            for (OrderLine line : orderLinesDs.getItems()) {

                if (line.getOrder().equals(currentOrder) && line.getPrinterGroup().equals(printerGroupToSendTicket))
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
                    newModifierLine.setPrinterGroup(selectedLine.getPrinterGroup());
                    newModifierLine.setIsSended(Boolean.FALSE);
                    orderLinesDs.addItem(newModifierLine);

                }

                selectedLine.setPrice(selectedLine.getPrice().
                        add(newModifierLine.getUnitPrice().multiply(BigDecimal.valueOf(selectedLine.getQuantity()))));

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

            }

        });

    }

    public void onAddBtnClick() {

        if (orderLineDataGrid.getSelected().size() > 1 || orderLineDataGrid.getSingleSelected() == null) {
            return;
        }

        OrderLine selectedLine = (OrderLine) orderLineDataGrid.getSingleSelected();

        if (selectedLine.getIsModifier()) return;

        BigDecimal itemSinglePrice = selectedLine.getPrice().divide(BigDecimal.valueOf(selectedLine.getQuantity()), RoundingMode.FLOOR);

        selectedLine.setQuantity(selectedLine.getQuantity()+1);
        selectedLine.setPrice(selectedLine.getPrice().add(itemSinglePrice));

        orderLinesDs.commit();

        refreshBill();

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

                refreshBill();

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

            refreshBill();

            } else {

            selectedLine.setQuantity(selectedLine.getQuantity() - 1);
            selectedLine.setPrice(selectedLine.getPrice().subtract(selectedLine.getUnitPrice()));

            orderLinesDs.commit();

            refreshBill();

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

            refreshBill();

        } else {

            showOptionDialog("warning", "Please select an item to remove",MessageType.WARNING,
                    new Action[] {
                            new DialogAction(DialogAction.Type.OK)});

        }

    }

    public void onSaveBtnClick() {

        getWindowManager().close(this);

    }

    private void refreshBill() {

        subTotal = BigDecimal.ZERO;

        for (OrderLine line : orderLinesDs.getItems()) {

            if (line.getOrder().getId().equals(currentOrder.getId())) {

                subTotal = subTotal.add(line.getPrice());

            }

        }

        service = BigDecimal.valueOf(Math.round(subTotal.multiply(BigDecimal.valueOf(0.1)).subtract(BigDecimal.valueOf(0.2)).
                multiply(BigDecimal.valueOf(2)).doubleValue()) / 2.0f).setScale(2);
        total = subTotal.add(service);

        subTotalField.setValue(subTotal);
        serviceField.setValue(service);
        totalField.setValue(total);

        currentOrder = dataManager.load(Order.class)
                .query("select e from jokerapp$Order e where e.id = :currentOrder")
                .parameter("currentOrder", table.getCurrentOrder())
                .view("order-view")
                .one();

        currentOrder.setCharge(subTotal);
        currentOrder.setTaxes(service);

        dataManager.commit(currentOrder);

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

    class Ticket implements Printable {

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
                graphics2D.setFont(font1);
                graphics2D.drawString(printerGroupToSendTicket, xMin, y);
                y += 30;
                graphics2D.drawString("TAVOLO ".concat(table.getTableNumber().toString()), xMin, y);
                y += 20;
                graphics2D.setFont(font3);
                graphics2D.drawString("Coperti: ".concat(currentOrder.getActualSeats().toString()), xMin, y);
                y += 20;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                graphics2D.drawString("Time: ".concat(sdf.format(cal.getTime())), xMin, y);
                y += 20;
                graphics2D.setFont(font2);

                for (OrderLine line : orderLinesDs.getItems()) {

                    if (line.getOrder().equals(currentOrder) && line.getPrinterGroup().equals(printerGroupToSendTicket)) {

                        graphics2D.drawString(line.getQuantity().toString(),xMin, y);

                        Integer linesToDraw = Math.round(line.getItemName().length()/34) + 1 ;

                        int spacePosition = 0;
                        int currentSpacePosition = 0;

                        for (int l=1; l<linesToDraw; l++) {

                            String lineName = line.getItemName();

                            for (int i = spacePosition; i<line.getItemName().length(); i++) {

                                Character c = lineName.charAt(i);

                                if (Character.isSpace(c)) {

                                    if (i > 34*l) break;

                                    spacePosition = i;

                                }

                            }

                            graphics2D.drawString(lineName.substring(currentSpacePosition, spacePosition),xMin+font2.getSize(), y);

                            currentSpacePosition = spacePosition;

                            y = y + yInc2 + 1;

                        }

                        graphics2D.drawString(line.getItemName().substring(currentSpacePosition),xMin+font2.getSize(), y);

                    y = y + yInc2 + 4;

                    }

                }

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
            showProductCategories((categoriesActualPage-1)*8, ((categoriesActualPage-1)*8)+7);

        } else {

            categoriesActualPage = 1;
            categoriesBackBtn.setVisible(Boolean.FALSE);
            categoriesNextBtn.setVisible(Boolean.TRUE);
            showProductCategories(0, 7);

        }

    }

    public void onCategoriesNextBtnClick() {

        categoriesGrid.removeAll();

        categoriesActualPage++;

        if (categoriesActualPage>1 && categoriesActualPage<categoriesPages) {

            categoriesBackBtn.setVisible(Boolean.TRUE);
            categoriesNextBtn.setVisible(Boolean.TRUE);
            showProductCategories((categoriesActualPage-1)*8, ((categoriesActualPage-1)*8)+7);

        } else {

            categoriesBackBtn.setVisible(Boolean.TRUE);
            categoriesNextBtn.setVisible(Boolean.FALSE);
            showProductCategories((categoriesActualPage-1)*8, categorySize-1);

        }

    }

    public void onItemsBackBtnClick() {

        itemsGrid.removeAll();

        productItemsActualPage--;

        if (productItemsActualPage>1) {

            itemsBackBtn.setVisible(Boolean.TRUE);
            itemsNextBtn.setVisible(Boolean.TRUE);
            showProductItemsPaged((productItemsActualPage-1)*16, ((productItemsActualPage-1)*16)+15);

        } else {

            productItemsActualPage = 1;
            itemsBackBtn.setVisible(Boolean.FALSE);
            itemsNextBtn.setVisible(Boolean.TRUE);
            showProductItemsPaged(0, 15);

        }

    }

    public void onItemsNextBtnClick() {

        itemsGrid.removeAll();

        productItemsActualPage++;

        if (productItemsActualPage>1 && productItemsActualPage<productItemsPages) {

            itemsBackBtn.setVisible(Boolean.TRUE);
            itemsNextBtn.setVisible(Boolean.TRUE);
            showProductItemsPaged((productItemsActualPage-1)*16, ((productItemsActualPage-1)*16)+15);

        } else {

            itemsBackBtn.setVisible(Boolean.TRUE);
            itemsNextBtn.setVisible(Boolean.FALSE);
            showProductItemsPaged((productItemsActualPage-1)*16, productItemSize-1);

        }

    }
}