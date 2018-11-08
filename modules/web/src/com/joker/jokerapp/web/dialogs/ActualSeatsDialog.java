package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextField;
import com.joker.jokerapp.entity.TableItem;

import javax.inject.Named;
import java.util.Map;

public class ActualSeatsDialog extends AbstractWindow {


    @Named("seatsText")
    private TextField seatsText;

    public interface CloseHandler {
        void onClose(int seats);
    }

    private CloseHandler handler;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("table")) {
            TableItem table = (TableItem) params.get("table");
            seatsText.setValue(table.getSeatsCapacity().toString());
        }

        if (params.containsKey("handler")) {
            handler = (CloseHandler) params.get("handler");
        }

    }

    public void onCancelBtnClick() {
        close("cancel");
    }

    public void onOkBtnClick() {

        if (handler != null) {
            int seatsNum = Integer.parseInt(seatsText.getValue());
            handler.onClose(seatsNum);
        }
        close("ok");
    }
}
