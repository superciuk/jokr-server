package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Map;

public class ItemModifierDialog extends AbstractWindow {



    @Named("modifierText")
    private TextField modifierText;

    @Named("modifierPrice")
    private TextField modifierPrice;

    public interface CloseHandler {

        void onClose(String modifier,BigDecimal itemModifierPrice);

    }

    private ItemModifierDialog.CloseHandler handler;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        if (params.containsKey("handler")) {

            handler = (ItemModifierDialog.CloseHandler) params.get("handler");

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