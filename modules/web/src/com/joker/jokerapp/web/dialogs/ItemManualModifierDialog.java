package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Map;

public class ItemManualModifierDialog extends AbstractWindow {



    @Named("modifierText")
    private TextField modifierText;

    @Named("modifierPrice")
    private TextField modifierPrice;

    public interface CloseHandler {

        void onClose(String modifier,BigDecimal itemModifierPrice);

    }

    private ItemManualModifierDialog.CloseHandler handler;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        modifierPrice.setValue(BigDecimal.valueOf(0));

        if (params.containsKey("handler")) {

            handler = (ItemManualModifierDialog.CloseHandler) params.get("handler");

        }

    }

    public void onCancelBtnClick() {
        close("cancel");
    }

    public void onOkBtnClick() {

        if (handler != null) {
            String itemModifier = modifierText.getValue();
            BigDecimal itemModifierPrice = modifierPrice.getValue();
            handler.onClose(itemModifier,itemModifierPrice);
        }

        close("ok");

    }

}