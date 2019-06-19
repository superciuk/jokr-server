package com.joker.jokerapp.web.popups;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UiController("jokerapp_ItemModifier")
@UiDescriptor("item-modifier.xml")
public class ItemModifier extends Screen {

    @Inject
    private Metadata metadata;

    @Inject
    private UiComponents uiComponents;

    @Inject
    private DataManager dataManager;

    @Inject
    private DataContext dataContext;

    @Inject
    private InstanceContainer<Ticket> ticketDc;

    @Named("modifierOrderLinesScrollBox")
    private ScrollBoxLayout modifierOrderLinesScrollBox;

    @Named("modifierCategoriesGrid")
    private GridLayout modifierCategoriesGrid;

    @Named("modifierItemGrid")
    private GridLayout modifierItemGrid;

    @Named("addModifierBtn")
    private Button addModifierBtn;

    @Named("removeModifierBtn")
    private Button removeModifierBtn;

    private String categoryBtnWidth = "180px";
    private String categoryBtnHeight = "120px";

    private String itemBtnWidth = "180px";
    private String itemBtnHeight = "120px";

    private List<ProductModifierCategory> modifierCategories;

    private OrderLine lineToModify;
    private OrderLine selectedLine;

    private ArrayList<Integer> spaceToConvert = new ArrayList<>();

    private boolean addModifierBtnPushed = true;

    @Subscribe
    private void onBeforeShow(BeforeShowEvent event) {

        drawOrderLinesGrid(null, null);

        modifierCategoriesGrid.setColumns(1);

        modifierCategories = dataContext.getParent().find(ProductItem.class, lineToModify.getItemId()).getModifierCategories();

        for (ProductModifierCategory productModifierCategory: modifierCategories) {

            WebButton btn = uiComponents.create(WebButton.class);

            btn.setWidth(categoryBtnWidth);
            btn.setHeight(categoryBtnHeight);
            btn.setAlignment(Component.Alignment.TOP_CENTER);
            btn.setCaptionAsHtml(Boolean.TRUE);

            int numberOfRow;

            int maxLineLength = 0;

            String categoryName = productModifierCategory.getName();

            if (Math.floorMod(categoryName.length(),10) == 0) numberOfRow = Math.floorDiv(categoryName.length(),10);
            else numberOfRow = Math.floorDiv(categoryName.length(),10) + 1;

            int exactLineLength = Math.floorDiv(categoryName.length(), numberOfRow);

            if (categoryName.length() > exactLineLength && categoryName.contains(" ")) {

                int actualSpace = 0;
                int prevSpaceConverted = 0;

                spaceToConvert.clear();
                maxLineLength = 0;

                for (int l = 0; l < categoryName.length(); l++) {

                    char ch = categoryName.charAt(l);

                    if (Character.isWhitespace(ch) || l == categoryName.length()-1) {

                        if (l - prevSpaceConverted > exactLineLength) {

                            if (actualSpace != 0 && prevSpaceConverted != actualSpace && (actualSpace - prevSpaceConverted <= l - actualSpace || l - prevSpaceConverted > 10)) {

                                spaceToConvert.add(actualSpace);
                                if (actualSpace - prevSpaceConverted > maxLineLength) maxLineLength = actualSpace - prevSpaceConverted;
                                prevSpaceConverted = actualSpace;

                            } else {

                                if (!(l == categoryName.length()-1)) {

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

            btn.setCaption(categoryName);

            if (maxLineLength <= 10 && spaceToConvert.size()<3) btn.setStyleName("v-button-fontSize30");
            else btn.setStyleName("v-button-fontSize20");

            btn.setAction(new BaseAction("showItem".concat(productModifierCategory.getName())).withHandler(e -> showItemModifiers(productModifierCategory)));

            modifierCategoriesGrid.add(btn);

        }

    }

    public void setParentDataContext(DataContext parentDataContext) {

        dataContext.setParent(parentDataContext);

    }

    public void setTicketDc(InstanceContainer currentTicket) {

        ticketDc = currentTicket;

    }

    public void setLineToModify(OrderLine line) {

        lineToModify = line;

    }

    private void showItemModifiers(ProductModifierCategory productModifierCategory) {

        List <ProductModifier> productModifiers = dataManager.load(ProductModifier.class)
                .query("select e from jokerapp$ProductModifier e where e.category.id = :productModifierCategory order by e.sortOrder")
                .parameter("productModifierCategory", productModifierCategory.getId())
                .view("productModifier-view")
                .list();


        modifierItemGrid.removeAll();

        for (ProductModifier modifier : productModifiers) {

            WebButton btn = uiComponents.create(WebButton.class);
            btn.setWidth(itemBtnWidth);
            btn.setHeight(itemBtnHeight);
            btn.setAlignment(Component.Alignment.TOP_LEFT);
            btn.setCaptionAsHtml(Boolean.TRUE);

            int numberOfRow;

            int maxLineLength = 0;

            String productName = modifier.getName();

            if (Math.floorMod(productName.length(),12)==0) numberOfRow = Math.floorDiv(productName.length(),12);
            else numberOfRow = Math.floorDiv(productName.length(),12) + 1;

            int exactLineLength = Math.floorDiv(productName.length(), numberOfRow);

            if (productName.length() > exactLineLength && productName.contains(" ")) {

                int actualSpace = 0;
                int prevSpaceConverted = 0;

                spaceToConvert.clear();
                maxLineLength = 0;

                for (int l = 0; l < productName.length(); l++) {

                    Character ch = productName.charAt(l);

                    if (Character.isWhitespace(ch) || l == productName.length()-1) {

                        if (l - prevSpaceConverted > exactLineLength) {

                            if (actualSpace != 0 && prevSpaceConverted != actualSpace && (actualSpace - prevSpaceConverted <= l - actualSpace || l - prevSpaceConverted > 12)) {

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

            btn.setCaption(productName);

            if (maxLineLength <= 12 && spaceToConvert.size()<4) btn.setStyleName("v-button-fontSize20");
            else btn.setStyleName("v-button-fontSize16");

            btn.setAction(new BaseAction("addModifierToOrder".concat(modifier.getName())).withHandler(e -> addModifierToOrder(modifier)));
            modifierItemGrid.add(btn);

        }

    }

    private void addModifierToOrder(ProductModifier productModifierToAdd) {

        for (OrderLine line: ticketDc.getItem().getOrderLines()) {

            if ((line.getIsModifier() && line.getItemToModifyId().equals(lineToModify.getId())) &&
                    (line.getItemName().equals(" + ".concat(productModifierToAdd.getName())) || line.getItemName().equals(" - ".concat(productModifierToAdd.getName())))) return;

        }

        OrderLine newLine = dataContext.getParent().getParent().merge(metadata.create(OrderLine.class));

        newLine.setQuantity(1);

        if (addModifierBtnPushed) {

            newLine.setItemName(" + ".concat(productModifierToAdd.getName()));
            newLine.setUnitPrice(productModifierToAdd.getAddPrice().setScale(2));
            newLine.setPrice(productModifierToAdd.getAddPrice().setScale(2));

        } else {

            newLine.setItemName(" - ".concat(productModifierToAdd.getName()));
            newLine.setUnitPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())).setScale(2));
            newLine.setPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())).setScale(2));

        }

        newLine.setItemId(productModifierToAdd.getId());
        newLine.setTaxes(BigDecimal.ZERO.setScale(2));
        newLine.setTicket(lineToModify.getTicket());
        newLine.setPosition(lineToModify.getNextModifierPosition());
        lineToModify.setNextModifierPosition(lineToModify.getNextModifierPosition()+1);
        newLine.setHasModifier(false);
        newLine.setIsModifier(Boolean.TRUE);
        newLine.setItemToModifyId(lineToModify.getId());
        newLine.setPrinterGroup(lineToModify.getPrinterGroup());
        newLine.setIsReversed(false);

        if (lineToModify.getHasModifier().equals(Boolean.FALSE)) lineToModify.setHasModifier(Boolean.TRUE);

        lineToModify.setPrice(lineToModify.getPrice().add(newLine.getUnitPrice().multiply(BigDecimal.valueOf(lineToModify.getQuantity()))));

        ticketDc.getItem().getOrderLines().add(newLine);

        dataContext.getParent().getParent().commit();

        drawOrderLinesGrid(newLine,"added");

    }

    @Subscribe("modifierOrderLinesRemoveBtn")
    public void onModifierOrderLinesRemoveBtnClick(Button.ClickEvent event) {

        if (selectedLine != null) {

            ticketDc.getItem().getOrderLines().remove(selectedLine);
            dataContext.getParent().getParent().remove(selectedLine);
            dataContext.getParent().getParent().commit();
            drawOrderLinesGrid(selectedLine, "removed");

        }

    }

    @Subscribe("okBtn")
    protected void onOkBtnClick(Button.ClickEvent event) {

        close(WINDOW_COMMIT_AND_CLOSE_ACTION);

    }

    @Subscribe("addModifierBtn")
    public void onAddModifierBtnClick(Button.ClickEvent event) {

        addModifierBtn.setStyleName("modifierButtonPushed");
        removeModifierBtn.setStyleName("modifierButtonNotPushed");
        addModifierBtnPushed = true;

    }

    @Subscribe("removeModifierBtn")
    public void onRemoveModifierBtnClick(Button.ClickEvent event) {

        addModifierBtn.setStyleName("modifierButtonNotPushed");
        removeModifierBtn.setStyleName("modifierButtonPushed");
        addModifierBtnPushed = false;

    }

    private void drawOrderLinesGrid(OrderLine lineToProcess, String operationPerformed) {

        OrderLine lineToDeselect = null;

        if (lineToProcess != null) {

            if (operationPerformed.equals("added")) {

                HBoxLayout hBoxToAdd = createOrderLineHBox(lineToProcess);
                modifierOrderLinesScrollBox.add(hBoxToAdd);

                if (selectedLine != null) lineToDeselect = selectedLine;

                selectedLine = lineToProcess;

                if (lineToDeselect != null) setOrderLineStyle(lineToDeselect, modifierOrderLinesScrollBox);

                setOrderLineStyle(lineToProcess, modifierOrderLinesScrollBox);

                ((Button) modifierOrderLinesScrollBox.getComponent("itemName".concat(lineToProcess.getId().toString()))).focus();

                return;

            }

            if (operationPerformed.equals("removed")) {

                HBoxLayout hBoxToRemove = (HBoxLayout) modifierOrderLinesScrollBox.getOwnComponent("hBoxLayout".concat(lineToProcess.getId().toString()));

                int index = modifierOrderLinesScrollBox.indexOf(hBoxToRemove);

                modifierOrderLinesScrollBox.remove(hBoxToRemove);

                if (modifierOrderLinesScrollBox.getOwnComponents().size() == 0) {

                    selectedLine = null;

                    return;

                }

                if (index == modifierOrderLinesScrollBox.getOwnComponents().size()) index--;

                HBoxLayout hBoxToSelect = (HBoxLayout) modifierOrderLinesScrollBox.getComponent(index);

                for (OrderLine orderLine: ticketDc.getItem().getOrderLines()) if (orderLine.getId().equals(UUID.fromString(hBoxToSelect.getId().substring(10)))) {

                    selectedLine = orderLine;

                    setOrderLineStyle(orderLine, modifierOrderLinesScrollBox);

                    ((Button) modifierOrderLinesScrollBox.getComponent("itemName".concat(orderLine.getId().toString()))).focus();

                    return;

                }

            }

        } else {

            modifierOrderLinesScrollBox.removeAll();

            for (OrderLine orderLine: ticketDc.getItem().getOrderLines()) {

                if (orderLine.getIsModifier() && orderLine.getItemToModifyId().equals(lineToModify.getId())) {

                    modifierOrderLinesScrollBox.add(createOrderLineHBox(orderLine));
                    setOrderLineStyle(orderLine, modifierOrderLinesScrollBox);

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

        itemName.setAction(new ItemModifier.SelectCurrentLineAction());

        if (!orderLine.getIsModifier()) quantity.setValue(orderLine.getQuantity());

        if (orderLine.getItemName().length() < 50) itemName.setCaption(orderLine.getItemName());
        else
            itemName.setCaption(orderLine.getItemName().substring(0, 27).concat("...").concat(orderLine.getItemName().substring(orderLine.getItemName().length() - 20)));

        if (!orderLine.getPrice().equals(BigDecimal.ZERO.setScale(2))) {

            if (orderLine.getPrice().toString().charAt(0) == '-') price.setValue("(".concat(orderLine.getPrice().toString()).concat(")"));
            else price.setValue("(+".concat(orderLine.getPrice().toString()).concat(")"));

        }

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

            OrderLine newLineToSelect = dataContext.getParent().getParent().find(OrderLine.class, UUID.fromString(itemNameBtn.getId().substring(8)));

            if (newLineToSelect == selectedLine) {

                selectedLine = null;
                setOrderLineStyle(newLineToSelect, modifierOrderLinesScrollBox);
                return;

            } else {

                OrderLine newLineToDeselect = selectedLine;

                selectedLine = newLineToSelect;

                setOrderLineStyle(newLineToSelect, modifierOrderLinesScrollBox);
                if (newLineToDeselect != null) setOrderLineStyle(newLineToDeselect, modifierOrderLinesScrollBox);

            }

        }

    }

}