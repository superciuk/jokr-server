package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.joker.jokerapp.entity.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

public class ItemModifierDialog extends AbstractWindow {

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private CollectionDatasource<OrderLine, UUID> modifierOrderLinesDs;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Named("modifierOrderLinesDataGrid")
    private DataGrid modifierOrderLinesDataGrid;

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

    private ItemModifierDialog.CloseHandler handler;
    private Order currentOrder;
    private OrderLine selectedLine;
    private List <ProductModifierCategory> modifierCategories;
    private List <OrderLine> newModifierOrderLines = new ArrayList<>();

    private ArrayList<Integer> spaceToConvert = new ArrayList<>();

    Boolean addModifierBtnPushed = true;

    public interface CloseHandler {

        void onClose(List<OrderLine> newModifierOrderLines);

    }

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        modifierOrderLinesDs.refresh();
        drawOrderLinesGrid();

        if (params.containsKey("handler")) {

            handler = (ItemModifierDialog.CloseHandler) params.get("handler");

            selectedLine = (OrderLine) params.get("selectedLine");
            currentOrder = selectedLine.getOrder();

            addModifierBtn.setStyleName("modifierButtonPushed");
            removeModifierBtn.setStyleName("modifierButtonNotPushed");

            String itemToModify = selectedLine.getItemName();

            ProductItem item = dataManager.load(ProductItem.class)
                    .query("select e from jokerapp$ProductItem e where e.name = :itemToModify")
                    .parameter("itemToModify", itemToModify)
                    .view("productItem-view")
                    .one();

            modifierCategories = item.getModifierCategories();
            modifierCategoriesGrid.setColumns(1);

            for (ProductModifierCategory productModifierCategory : modifierCategories) {

                WebButton btn = componentsFactory.createComponent(WebButton.class);

                btn.setWidth(categoryBtnWidth);
                btn.setHeight(categoryBtnHeight);
                btn.setAlignment(Alignment.TOP_CENTER);
                btn.setCaptionAsHtml(Boolean.TRUE);

                int numberOfRow;

                int maxLineLength = 0;

                String categoryName = productModifierCategory.getName();

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

                btn.setCaption(categoryName);

                if (maxLineLength <= 10 && spaceToConvert.size()<3) btn.setStyleName("v-button-fontSize30");
                else btn.setStyleName("v-button-fontSize20");

                btn.setAction(new BaseAction("showItem".concat(productModifierCategory.getName())).withHandler(e -> showItemModifiers(productModifierCategory)));

                modifierCategoriesGrid.add(btn);

            }

        }

    }

    private void showItemModifiers(ProductModifierCategory productModifierCategory) {

        List <ProductModifier> productModifiers = dataManager.load(ProductModifier.class)
                .query("select e from jokerapp$ProductModifier e where e.category.id = :productModifierCategory")
                .parameter("productModifierCategory", productModifierCategory.getId())
                .view("productModifier-view")
                .list();

        modifierItemGrid.removeAll();

        for (ProductModifier modifier : productModifiers) {

            WebButton btn = componentsFactory.createComponent(WebButton.class);
            btn.setWidth(itemBtnWidth);
            btn.setHeight(itemBtnHeight);
            btn.setAlignment(Alignment.TOP_LEFT);
            btn.setCaptionAsHtml(Boolean.TRUE);

            int numberOfRow;

            int maxLineLength = 0;

            String productName = modifier.getName();

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

            btn.setCaption(productName);

            if (maxLineLength <= 14 && spaceToConvert.size()<4) btn.setStyleName("v-button-fontSize20");
            else btn.setStyleName("v-button-fontSize16");

            btn.setAction(new BaseAction("addModifierToOrder".concat(modifier.getName())).withHandler(e -> addModifierToOrder(modifier)));
            modifierItemGrid.add(btn);

        }

    }

    private void addModifierToOrder(ProductModifier productModifierToAdd) {

        for (OrderLine line : modifierOrderLinesDs.getItems()) {

            if (line.getItemName().equals(" + ".concat(productModifierToAdd.getName())) || line.getItemName().equals(" - ".concat(productModifierToAdd.getName()))) return;

        }

        OrderLine newLine = metadata.create(OrderLine.class);

        newLine.setQuantity(1);

        if (addModifierBtnPushed) {

            newLine.setItemName(" + ".concat(productModifierToAdd.getName()));
            newLine.setUnitPrice(productModifierToAdd.getAddPrice());
            newLine.setPrice(productModifierToAdd.getAddPrice());

        } else {

            newLine.setItemName(" - ".concat(productModifierToAdd.getName()));
            newLine.setUnitPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())));
            newLine.setPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())));

        }

        newLine.setItemId(productModifierToAdd.getId());
        newLine.setTaxes(BigDecimal.ZERO);
        newLine.setOrder(currentOrder);
        newLine.setPosition(selectedLine.getNextModifierPosition());
        selectedLine.setNextModifierPosition(selectedLine.getNextModifierPosition()+1);
        newLine.setHasModifier(Boolean.FALSE);
        newLine.setIsModifier(Boolean.TRUE);
        newLine.setItemToModifyId(selectedLine.getId());
        newLine.setPrinterGroup(selectedLine.getPrinterGroup());
        newLine.setIsSended(Boolean.FALSE);
        newLine.setIsSelected(Boolean.FALSE);

        if (selectedLine.getHasModifier().equals(Boolean.FALSE)) selectedLine.setHasModifier(Boolean.TRUE);

        modifierOrderLinesDs.addItem(newLine);

        drawOrderLinesGrid();

    }

    public void onModifierOrderLinesRemoveBtnClick() {

        ArrayList<OrderLine> toRemove = new ArrayList<>();

        for (OrderLine lineToRemove: modifierOrderLinesDs.getItems()) if (lineToRemove.getIsSelected()) toRemove.add(lineToRemove);

        Iterator<OrderLine> iterator = toRemove.iterator();

        while (iterator.hasNext()) modifierOrderLinesDs.removeItem(iterator.next());

        drawOrderLinesGrid();

    }


    public void onCancelBtnClick() {

        close("cancel");

    }

    public void onOkBtnClick() {

        if (handler != null) {

            newModifierOrderLines.addAll(modifierOrderLinesDs.getItems());
            for (OrderLine line : modifierOrderLinesDs.getItems()) line.setIsSelected(false);
            handler.onClose(newModifierOrderLines);

        }

        close("ok");

    }

    public void onAddModifierBtnClick() {

        addModifierBtn.setStyleName("modifierButtonPushed");
        removeModifierBtn.setStyleName("modifierButtonNotPushed");
        addModifierBtnPushed = true;

    }

    public void onRemoveModifierBtnClick() {

        addModifierBtn.setStyleName("modifierButtonNotPushed");
        removeModifierBtn.setStyleName("modifierButtonPushed");
        addModifierBtnPushed = false;

    }

    private void drawOrderLinesGrid () {

        modifierOrderLinesScrollBox.removeAll();

        for (OrderLine orderLine : modifierOrderLinesDs.getItems()) {

            HBoxLayout hBoxLayout= componentsFactory.createComponent(HBoxLayout.class);

            Button itemName = componentsFactory.createComponent(Button.class);
            Label price = componentsFactory.createComponent(Label.class);

            itemName.setWidth("430px");
            price.setWidth("55px");

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

                                itemName.setStyleName("button-itemName-selected-isModifier-isSended");
                                price.setStyleName("label-price-selected-isModifier-isSended");

                            } else {

                                itemName.setStyleName("button-itemName-selected-isSended");
                                price.setStyleName("label-price-selected-isSended");

                            }

                        } else {

                            if (orderLine.getIsModifier()) {

                                itemName.setStyleName("button-itemName-selected-isModifier");
                                price.setStyleName("label-price-selected-isModifier");

                            } else {

                                itemName.setStyleName("button-itemName-selected");
                                price.setStyleName("label-price-selected");

                            }

                        }

                    } else {

                        orderLine.setIsSelected(Boolean.FALSE);

                        if (orderLine.getIsSended()) {

                            if (orderLine.getIsModifier()) {

                                itemName.setStyleName("button-itemName-isModifier-isSended");
                                price.setStyleName("label-price-isModifier-isSended");

                            } else {

                                itemName.setStyleName("button-itemName-isSended");
                                price.setStyleName("label-price-isSended");

                            }

                        } else {

                            if (orderLine.getIsModifier()) {

                                itemName.setStyleName("button-itemName-isModifier");
                                price.setStyleName("label-price-isModifier");

                            } else {

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

                        itemName.setStyleName("button-itemName-selected-isModifier-isSended");
                        price.setStyleName("label-price-selected-isModifier-isSended");

                    } else {

                        itemName.setStyleName("button-itemName-selected-isSended");
                        price.setStyleName("label-price-selected-isSended");

                    }

                } else {

                    if (orderLine.getIsModifier()) {

                        itemName.setStyleName("button-itemName-selected-isModifier");
                        price.setStyleName("label-price-selected-isModifier");

                    } else {

                        itemName.setStyleName("button-itemName-selected");
                        price.setStyleName("label-price-selected");

                    }

                }

            } else {

                if (orderLine.getIsSended()) {

                    if (orderLine.getIsModifier()) {

                        itemName.setStyleName("button-itemName-isModifier-isSended");
                        price.setStyleName("label-price-isModifier-isSended");

                    } else {

                        itemName.setStyleName("button-itemName-isSended");
                        price.setStyleName("label-price-isSended");

                    }

                } else {

                    if (orderLine.getIsModifier()) {

                        itemName.setStyleName("button-itemName-isModifier");
                        price.setStyleName("label-price-isModifier");

                    } else {

                        itemName.setStyleName("button-itemName");
                        price.setStyleName("label-price");

                    }

                }

            }

            itemName.setCaption(orderLine.getItemName());
            price.setValue(orderLine.getPrice().toString());

            hBoxLayout.add(itemName);
            hBoxLayout.add(price);

            modifierOrderLinesScrollBox.add(hBoxLayout);

        }

    }

}