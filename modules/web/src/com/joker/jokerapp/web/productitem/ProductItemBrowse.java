package com.joker.jokerapp.web.productitem;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.gui.components.WebLookupPickerField;
import com.joker.jokerapp.entity.ProductItem;


import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

public class ProductItemBrowse extends EntityCombinedScreen {

    @Inject
    private CollectionDatasource<ProductItem, UUID> productItemsDs;

    @Named("table")
    private Table<ProductItem> table;

    public void onDuplicateBtnClick() {

        if (table.getSelected().size() > 1 || table.getSingleSelected() == null) {
            return;
        }

        ProductItem itemToDuplicate = table.getSingleSelected();


        Action createAction = table.getAction("create");
        createAction.actionPerform(this);
        int max=0;

        for (ProductItem item: productItemsDs.getItems()) {
            if (item.getCategory().equals(itemToDuplicate.getCategory()) && item.getSortOrder() > max) {
                max = item.getSortOrder();
            }

        }

        max += 10;

        TextField name = (TextField) getComponent("fieldGroup.name");
        name.setValue(itemToDuplicate.getName());
        TextField sortOrder = (TextField) getComponent("fieldGroup.sortOrder");
        sortOrder.setValue(max);
        WebLookupPickerField category = (WebLookupPickerField) getComponent("fieldGroup.category");
        category.setValue(itemToDuplicate.getCategory());
        TextField price = (TextField) getComponent("fieldGroup.price");
        price.setValue(itemToDuplicate.getPrice());
        CheckBox visible = (CheckBox) getComponent("fieldGroup.visible");
        visible.setValue(itemToDuplicate.getVisible());

        OptionsGroup optionsGroup = (OptionsGroup)getComponent("modifierCategories");
        optionsGroup.setValue(itemToDuplicate.getModifierCategories());

    }
}