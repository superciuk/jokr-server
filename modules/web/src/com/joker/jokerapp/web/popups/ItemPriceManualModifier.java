package com.joker.jokerapp.web.popups;

import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Named;
import java.math.BigDecimal;

@UiController("jokerapp_ItemPriceManualModifier")
@UiDescriptor("item-price-manual-modifier.xml")
public class ItemPriceManualModifier extends Screen {

    @Named("priceModifier")
    private TextField priceModifier;

    BigDecimal newPrice = BigDecimal.ZERO;

    private Boolean pressed = false;

    @Subscribe("okBtn")
    protected void onOkBtnClick(Button.ClickEvent event) {

        if (priceModifier.getValue() != null) {

            newPrice = BigDecimal.valueOf(Double.parseDouble(priceModifier.getRawValue()));

            close(WINDOW_COMMIT_AND_CLOSE_ACTION);

        }

    }

    @Subscribe("cancelBtn")
    protected void onCancelBtnClick(Button.ClickEvent event) {

        closeWithDefaultAction();

    }

    public BigDecimal getNewItemPrice () {

        return newPrice;

    }

    @Subscribe("numPadBtn1")
    public void onNumPadBtn1Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("1");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("1"));
        pressed = true;

    }

    @Subscribe("numPadBtn2")
    public void onNumPadBtn2Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("2");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("2"));
        pressed = true;

    }

    @Subscribe("numPadBtn3")
    public void onNumPadBtn3Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("3");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("3"));
        pressed = true;

    }

    @Subscribe("numPadBtn4")
    public void onNumPadBtn4Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("4");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("4"));
        pressed = true;

    }

    @Subscribe("numPadBtn5")
    public void onNumPadBtn5Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("5");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("5"));
        pressed = true;

    }

    @Subscribe("numPadBtn6")
    public void onNumPadBtn6Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("6");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("6"));
        pressed = true;

    }

    @Subscribe("numPadBtn7")
    public void onNumPadBtn7Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("7");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("7"));
        pressed = true;

    }

    @Subscribe("numPadBtn8")
    public void onNumPadBtn8Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("8");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("8"));
        pressed = true;

    }

    @Subscribe("numPadBtn9")
    public void onNumPadBtn9Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("9");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("9"));
        pressed = true;

    }

    @Subscribe("numPadBtn0")
    public void onNumPadBtn0Click(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue("0");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("0"));
        pressed = true;

    }

    @Subscribe("numPadBtnDot")
    public void onNumPadBtnDotClick(Button.ClickEvent event) {

        if (priceModifier.getValue() == null || !pressed) priceModifier.setValue(".");
        else priceModifier.setValue(priceModifier.getValue().toString().concat("."));
        pressed = true;

    }

    @Subscribe("numPadBtnC")
    public void onNumPadBtnCClick(Button.ClickEvent event) {

        if (priceModifier.getValue() != null)
            priceModifier.setValue(priceModifier.getValue().toString().substring(0, priceModifier.getValue().toString().length()-1));

    }


}