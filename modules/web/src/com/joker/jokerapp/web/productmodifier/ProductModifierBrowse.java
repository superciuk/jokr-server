package com.joker.jokerapp.web.productmodifier;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Named;
import java.util.Map;

public class ProductModifierBrowse extends EntityCombinedScreen {

    @Named ("fieldGroup.addPrice")
    private TextField addPrice;

    @Named ("fieldGroup.subtractPrice")
    private TextField subtractPrice;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        addPrice.addValueChangeListener(e -> {
            if ((subtractPrice.getValue() != null ) && ((double)subtractPrice.getValue()==0)) subtractPrice.setValue(addPrice.getValue());
        });
        subtractPrice.addValueChangeListener(e -> {
            if ((subtractPrice.getValue() != null ) && ((double)addPrice.getValue()==0)) addPrice.setValue(subtractPrice.getValue());
        });
    }

    @Override
    protected void initNewItem(Entity item) {
        item.setValue("addPrice" , 0.0);
        item.setValue("subtractPrice" , 0.0);
        super.initNewItem(item);
    }
}