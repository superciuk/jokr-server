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

    private Boolean pressed = false;

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

            BigDecimal newPrice = BigDecimal.valueOf(Double.parseDouble(priceModifier.getRawValue()));
            handler.onClose(newPrice);

        }

        close("ok");

    }

    public void onNumPadBtn1Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("1");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("1"));
        pressed = true;

    }

    public void onNumPadBtn2Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("2");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("2"));
        pressed = true;

    }

    public void onNumPadBtn3Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("3");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("3"));
        pressed = true;

    }

    public void onNumPadBtn4Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("4");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("4"));
        pressed = true;

    }

    public void onNumPadBtn5Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("5");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("5"));
        pressed = true;

    }

    public void onNumPadBtn6Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("6");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("6"));
        pressed = true;

    }

    public void onNumPadBtn7Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("7");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("7"));
        pressed = true;

    }

    public void onNumPadBtn8Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("8");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("8"));
        pressed = true;

    }

    public void onNumPadBtn9Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("9");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("9"));
        pressed = true;

    }

    public void onNumPadBtn0Click() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue("0");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("0"));
        pressed = true;

    }

    public void onNumPadBtnDotClick() {

        if (priceModifier.getValue()==null || !pressed) priceModifier.setValue(".");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("."));
        pressed = true;

    }

    public void onNumPadBtnCClick() {

        if (priceModifier.getValue()!=null)
            priceModifier.setValue(priceModifier.getValue().toString().substring(0, priceModifier.getValue().toString().length()-1));

    }

}