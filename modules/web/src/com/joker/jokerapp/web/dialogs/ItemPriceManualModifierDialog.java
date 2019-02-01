package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Map;

public class ItemPriceManualModifierDialog extends AbstractWindow {

    @Named("priceModifier")
    private TextField priceModifier;

    public interface CloseHandler {

        void onClose(BigDecimal newPrice);

    }

    private ItemPriceManualModifierDialog.CloseHandler handler;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        if (params.containsKey("handler")) {

            handler = (ItemPriceManualModifierDialog.CloseHandler) params.get("handler");

        }

    }

    public void onCancelBtnClick() {
        close("cancel");
    }

    public void onOkBtnClick() {

        if (handler != null) {

            BigDecimal newPrice = priceModifier.getValue();
            handler.onClose(newPrice);

        }

        close("ok");

    }

}