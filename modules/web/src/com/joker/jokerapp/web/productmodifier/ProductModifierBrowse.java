package com.joker.jokerapp.web.productmodifier;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.web.gui.components.WebLookupPickerField;
import com.joker.jokerapp.entity.ProductModifier;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class ProductModifierBrowse extends EntityCombinedScreen {

    @Inject
    private GroupDatasource<ProductModifier, UUID> productModifiersDs;

    @Named ("fieldGroup.addPrice")
    private TextField addPrice;

    @Named ("fieldGroup.subtractPrice")
    private TextField subtractPrice;

    @Named("table")
    private Table<ProductModifier> table;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        addPrice.addValueChangeListener(e -> {
            if ((subtractPrice.getValue() != null ) && (subtractPrice.getValue()==BigDecimal.valueOf(0))) subtractPrice.setValue(addPrice.getValue());
        });
        subtractPrice.addValueChangeListener(e -> {
            if ((subtractPrice.getValue() != null ) && (addPrice.getValue()==BigDecimal.valueOf(0))) addPrice.setValue(subtractPrice.getValue());
        });
    }

    @Override
    protected void initNewItem(Entity item) {
        item.setValue("addPrice", BigDecimal.valueOf(0));
        item.setValue("subtractPrice", BigDecimal.valueOf(0));
        super.initNewItem(item);
    }

    public void onDuplicateBtnClick() {
    
        if (table.getSelected().size() > 1 || table.getSingleSelected() == null) {
            return;
        }

        ProductModifier itemToDuplicate = table.getSingleSelected();

        Action createAction = table.getAction("create");
        createAction.actionPerform(this);
        int max=0;

        for (ProductModifier item: productModifiersDs.getItems()) {
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
        TextField addPrice = (TextField) getComponent("fieldGroup.addPrice");
        addPrice.setValue(itemToDuplicate.getAddPrice());
        TextField subtractPrice = (TextField) getComponent("fieldGroup.subtractPrice");
        subtractPrice.setValue(itemToDuplicate.getSubtractPrice());

    }
}