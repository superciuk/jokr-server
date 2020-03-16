package com.joker.jokerapp.web.popups;

import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.*;
import javax.inject.Named;

@UiController("jokerapp_ActualSeats")
@UiDescriptor("actual-seats.xml")
public class ActualSeats extends Screen {

    @Named("seatsTextField")
    private TextField seatsTextField;

    private Boolean pressed = false;

    public String getSeats () {

        return seatsTextField.getValue().toString();

    }

    @Subscribe("okBtn")
    protected void onOkBtnClick(Button.ClickEvent event) {

        if (seatsTextField.getValue() != null) { close(WINDOW_COMMIT_AND_CLOSE_ACTION); }

    }

    @Subscribe("cancelBtn")
    protected void onCancelBtnClick(Button.ClickEvent event) {

        closeWithDefaultAction();

    }

    @Subscribe("numPadBtn1")
    protected void onNumPadBtn1Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("1");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("1"));
        pressed = true;

    }

    @Subscribe("numPadBtn2")
    protected void onNumPadBtn2Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("2");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("2"));
        pressed = true;

    }

    @Subscribe("numPadBtn3")
    protected void onNumPadBtn3Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("3");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("3"));
        pressed = true;

    }

    @Subscribe("numPadBtn4")
    protected void onNumPadBtn4Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("4");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("4"));
        pressed = true;

    }

    @Subscribe("numPadBtn5")
    protected void onNumPadBtn5Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("5");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("5"));
        pressed = true;

    }

    @Subscribe("numPadBtn6")
    protected void onNumPadBtn6Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("6");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("6"));
        pressed = true;

    }

    @Subscribe("numPadBtn7")
    protected void onNumPadBtn7Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("7");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("7"));
        pressed = true;

    }

    @Subscribe("numPadBtn8")
    protected void onNumPadBtn8Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("8");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("8"));
        pressed = true;

    }

    @Subscribe("numPadBtn9")
    protected void onNumPadBtn9Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("9");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("9"));
        pressed = true;

    }

    @Subscribe("numPadBtn0")
    protected void onNumPadBtn0Click(Button.ClickEvent event) {

        if (seatsTextField.getValue() == null || !pressed) seatsTextField.setValue("0");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("0"));
        pressed = true;

    }

    @Subscribe("numPadBtnC")
    protected void onNumPadBtnCClick(Button.ClickEvent event) {

        if (seatsTextField.getValue() != null && !seatsTextField.getValue().equals(""))
            seatsTextField.setValue(seatsTextField.getValue().toString().substring(0, seatsTextField.getValue().toString().length()-1));

    }

    public void setSeatsCapacity(String tableSeatsCapacity) {

        seatsTextField.setValue(tableSeatsCapacity);

    }

}