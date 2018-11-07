package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextField;
import com.joker.jokerapp.entity.TableItem;

import javax.inject.Named;
import java.util.Map;

public class ActualSeatsDialog extends AbstractWindow {


    @Named("seatsText")
    private TextField seatsText;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("table")) {
            TableItem table = (TableItem) params.get("table");
            seatsText.setValue(table.getSeatsCapacity().toString());
        }

    }

    public void onCancelBtnClick() {
        close("cancel");
    }

    public void onOkBtnClick() {

        int seatsNum = Integer.parseInt(seatsText.getValue());


    }
}
