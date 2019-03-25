package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextField;
import com.joker.jokerapp.entity.TableItem;

import javax.inject.Named;
import java.util.Map;

public class ActualSeatsDialog extends AbstractWindow {


    @Named("seatsTextField")
    private TextField seatsTextField;

    private TableItem table;

    public interface CloseHandler {
        void onClose(int seats);
    }

    private CloseHandler handler;

    private Boolean pressed = false;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        if (params.containsKey("table")) {
            table = (TableItem) params.get("table");
            seatsTextField.setValue(table.getSeatsCapacity());

        }

        if (params.containsKey("handler")) {
            handler = (CloseHandler) params.get("handler");
        }

    }

    public void onCancelBtnClick() {
        close("cancel");
    }

    public void onOkBtnClick() {

        if (seatsTextField.getValue() == null && table != null) seatsTextField.setValue(table.getSeatsCapacity());

        if (handler != null) {

            int seatsNum = seatsTextField.getValue();
            handler.onClose(seatsNum);

        }

        close("ok");

    }

    public void onNumPadBtn1Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("1");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("1"));
        pressed = true;

    }

    public void onNumPadBtn2Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("2");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("2"));
        pressed = true;

    }

    public void onNumPadBtn3Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("3");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("3"));
        pressed = true;

    }

    public void onNumPadBtn4Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("4");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("4"));
        pressed = true;

    }

    public void onNumPadBtn5Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("5");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("5"));
        pressed = true;

    }

    public void onNumPadBtn6Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("6");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("6"));
        pressed = true;

    }

    public void onNumPadBtn7Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("7");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("7"));
        pressed = true;

    }

    public void onNumPadBtn8Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("8");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("8"));
        pressed = true;

    }

    public void onNumPadBtn9Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("9");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("9"));
        pressed = true;

    }

    public void onNumPadBtn0Click() {

        if (seatsTextField.getValue()==null || !pressed) seatsTextField.setValue("0");
        else seatsTextField.setValue(seatsTextField.getValue().toString().concat("0"));
        pressed = true;

    }

    public void onNumPadBtnCClick() {

        if (seatsTextField.getValue()!=null)
            seatsTextField.setValue(seatsTextField.getValue().toString().substring(0, seatsTextField.getValue().toString().length()-1));

    }

}
