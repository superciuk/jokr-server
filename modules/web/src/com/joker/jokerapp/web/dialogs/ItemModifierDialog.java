package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.joker.jokerapp.entity.*;
import com.joker.jokerapp.web.screens.OrderScreen;

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

    @Named("modifierCategoriesGrid")
    private GridLayout modifierCategoriesGrid;

    @Named("modifierItemGrid")
    private GridLayout modifierItemGrid;

    @Named("addCheckBox")
    private CheckBox addCheckBox;

    @Named("removeCheckBox")
    private CheckBox removeCheckBox;

    private String categoryBtnWidth = "180px";
    private String categoryBtnHeight = "120px";

    private String itemBtnWidth = "180px";
    private String itemBtnHeight = "120px";

    private ItemModifierDialog.CloseHandler handler;
    private Order currentOrder;
    private OrderLine selectedLine;
    private List <ProductModifierCategory> modifierCategories;
    private List <OrderLine> newModifierOrderLines = new ArrayList<>();

    public interface CloseHandler {

        void onClose(List<OrderLine> newModifierOrderLines);

    }

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        if (params.containsKey("handler")) {

            handler = (ItemModifierDialog.CloseHandler) params.get("handler");

            selectedLine = (OrderLine) params.get("selectedLine");
            currentOrder = selectedLine.getOrder();

            String itemToModify = selectedLine.getItemName();

            ProductItem item = dataManager.load(ProductItem.class)
                    .query("select e from jokerapp$ProductItem e where e.name = :itemToModify")
                    .parameter("itemToModify", itemToModify)
                    .view("productItem-view")
                    .one();

            modifierCategories = item.getModifierCategories();
            modifierCategoriesGrid.setColumns(1);

            Integer btnNumber = 1;

            for (ProductModifierCategory productModifierCategory : modifierCategories) {

                WebButton btn = componentsFactory.createComponent(WebButton.class);

                btn.setId("btn".concat(btnNumber.toString()));
//            html.setCssProperty(categoriesGrid, HtmlAttributes.CSS.BACKGROUND_COLOR , "red");
                btn.setWidth(categoryBtnWidth);
                btn.setHeight(categoryBtnHeight);
                btn.setCaptionAsHtml(Boolean.TRUE);

                Integer nameLength = productModifierCategory.getName().length();
                String categoryName = productModifierCategory.getName();
                if (nameLength>16 && categoryName.contains(" ")) {

                    categoryName = categoryName.replace(" ", "<br>");
                    btn.setCaption(categoryName);

                } else btn.setCaption(categoryName);

                btn.setAction(new BaseAction("showItem".concat(productModifierCategory.getName())).withHandler(e -> showItemModifiers(productModifierCategory)));

                modifierCategoriesGrid.add(btn);

                btnNumber ++;

            }

        }

        addCheckBox.setValue(Boolean.TRUE);
        removeCheckBox.setValue(Boolean.FALSE);

        addCheckBox.addValueChangeListener(event -> {

            if (Boolean.TRUE.equals(event.getValue())) {

                removeCheckBox.setValue(Boolean.FALSE);

            } else {

                removeCheckBox.setValue(Boolean.TRUE);

            }

        });

        removeCheckBox.addValueChangeListener(event -> {

            if (Boolean.TRUE.equals(event.getValue())) {

                addCheckBox.setValue(Boolean.FALSE);

            } else {

                addCheckBox.setValue(Boolean.TRUE);

            }

        });

    }

    private void showItemModifiers(ProductModifierCategory productModifierCategory) {

        List <ProductModifier> productModifiers = dataManager.load(ProductModifier.class)
                .query("select e from jokerapp$ProductModifier e where e.category.id = :productModifierCategory")
                .parameter("productModifierCategory", productModifierCategory.getId())
                .view("productModifier-view")
                .list();

        modifierItemGrid.removeAll();
        modifierItemGrid.setColumns(4);

        for (ProductModifier modifier : productModifiers) {

                WebButton btn = componentsFactory.createComponent(WebButton.class);
                btn.setWidth(itemBtnWidth);
                btn.setHeight(itemBtnHeight);
                btn.setCaption(modifier.getName());
                btn.setAction(new BaseAction("addModifierToOrder".concat(modifier.getName())).withHandler(e -> addModifierToOrder(modifier)));
                modifierItemGrid.add(btn);

        }

    }

    private void addModifierToOrder(ProductModifier productModifierToAdd) {

        for (OrderLine line : modifierOrderLinesDs.getItems()) {

            if (addCheckBox.isChecked()) {

                if (line.getItemName().equals("  + ".concat(productModifierToAdd.getName()))) {

                    line.setQuantity(line.getQuantity() + 1);
                    line.setPrice(line.getPrice().add(productModifierToAdd.getAddPrice()));

                    return;

                }

            } else if (removeCheckBox.isChecked()) {

                if (line.getItemName().equals("  - ".concat(productModifierToAdd.getName()))) {

                    line.setQuantity(line.getQuantity() + 1);
                    line.setPrice(line.getPrice().add(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue()))));

                    return;

                }

            }

        }

        OrderLine newLine = metadata.create(OrderLine.class);

        newLine.setQuantity(1);
        if (addCheckBox.isChecked()) {

            newLine.setItemName("  + ".concat(productModifierToAdd.getName()));
            newLine.setUnitPrice(productModifierToAdd.getAddPrice());
            newLine.setPrice(productModifierToAdd.getAddPrice());

        } else if (removeCheckBox.isChecked()) {

            newLine.setItemName("  - ".concat(productModifierToAdd.getName()));
            newLine.setUnitPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())));
            newLine.setPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())));

        }

        newLine.setTaxes(BigDecimal.ZERO);
        newLine.setOrder(currentOrder);
        newLine.setPosition(selectedLine.getNextModifierPosition());
        selectedLine.setNextModifierPosition(selectedLine.getNextModifierPosition()+1);
        newLine.setHasModifier(Boolean.FALSE);
        newLine.setIsModifier(Boolean.TRUE);
        newLine.setItemToModifyId(selectedLine.getId());
        newLine.setIsSended(Boolean.FALSE);

        if (selectedLine.getHasModifier().equals(Boolean.FALSE)) selectedLine.setHasModifier(Boolean.TRUE);

        modifierOrderLinesDs.addItem(newLine);

    }

    public void onModifierOrderLinesRemoveBtnClick() {

        if (modifierOrderLinesDataGrid.getSelected().size() > 0) {

            for (OrderLine lineToRemove: (Set<OrderLine>) modifierOrderLinesDataGrid.getSelected()) {

                modifierOrderLinesDs.removeItem(lineToRemove);

            }

        } else {

            showOptionDialog("warning", "Please select an item to remove",MessageType.WARNING,
                    new Action[] {
                            new DialogAction(DialogAction.Type.OK)});

        }

    }

    public void onCancelBtnClick() {

        close("cancel");

    }

    public void onOkBtnClick() {

        if (handler != null) {

            newModifierOrderLines.addAll(modifierOrderLinesDs.getItems());
            handler.onClose(newModifierOrderLines);

        }

        close("ok");

    }

}