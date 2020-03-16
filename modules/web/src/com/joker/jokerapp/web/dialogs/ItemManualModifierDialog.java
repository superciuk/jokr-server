package com.joker.jokerapp.web.dialogs;

import com.haulmont.cuba.gui.components.*;

import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Map;

public class ItemManualModifierDialog extends AbstractWindow {

    @Named("modifierText")
    private TextField modifierText;

    @Named("modifierPrice")
    private TextField modifierPrice;

    @Named("keybBtnQ")
    private Button keybBtnQ;

    @Named("keybBtnW")
    private Button keybBtnW;

    @Named("keybBtnE")
    private Button keybBtnE;

    @Named("keybBtnR")
    private Button keybBtnR;

    @Named("keybBtnT")
    private Button keybBtnT;

    @Named("keybBtnY")
    private Button keybBtnY;

    @Named("keybBtnU")
    private Button keybBtnU;

    @Named("keybBtnI")
    private Button keybBtnI;

    @Named("keybBtnO")
    private Button keybBtnO;

    @Named("keybBtnP")
    private Button keybBtnP;

    @Named("keybBtnA")
    private Button keybBtnA;

    @Named("keybBtnS")
    private Button keybBtnS;

    @Named("keybBtnD")
    private Button keybBtnD;

    @Named("keybBtnF")
    private Button keybBtnF;

    @Named("keybBtnG")
    private Button keybBtnG;

    @Named("keybBtnH")
    private Button keybBtnH;

    @Named("keybBtnJ")
    private Button keybBtnJ;

    @Named("keybBtnK")
    private Button keybBtnK;

    @Named("keybBtnL")
    private Button keybBtnL;

    @Named("keybBtnZ")
    private Button keybBtnZ;

    @Named("keybBtnX")
    private Button keybBtnX;

    @Named("keybBtnC")
    private Button keybBtnC;

    @Named("keybBtnV")
    private Button keybBtnV;

    @Named("keybBtnB")
    private Button keybBtnB;

    @Named("keybBtnN")
    private Button keybBtnN;

    @Named("keybBtnM")
    private Button keybBtnM;

    public interface CloseHandler {

        void onClose(String modifier,BigDecimal itemModifierPrice);

    }

    private ItemManualModifierDialog.CloseHandler handler;

    private Boolean pressed = false;
    private Boolean numPadPressed = false;
    private Boolean capsPressed = false;


    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        modifierPrice.setValue("0");

        if (params.containsKey("handler")) {

            handler = (ItemManualModifierDialog.CloseHandler) params.get("handler");

        }

    }

    public void onCancelBtnClick() {

        close("cancel");

    }

    public void onOkBtnClick() {

        if (modifierText.getValue()!=null) if (handler != null) {

            String itemModifier = modifierText.getValue().toString().replaceAll("_", " ");
            BigDecimal itemModifierPrice = BigDecimal.valueOf(Double.parseDouble(modifierPrice.getRawValue()));
            handler.onClose(itemModifier, itemModifierPrice);

            close("ok");

        }

    }

    public void onKeybBtn1Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("1");
        else modifierText.setValue(modifierText.getValue().toString().concat("1"));
        pressed = true;

    }

    public void onKeybBtn2Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("2");
        else modifierText.setValue(modifierText.getValue().toString().concat("2"));
        pressed = true;

    }

    public void onKeybBtn3Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("3");
        else modifierText.setValue(modifierText.getValue().toString().concat("3"));
        pressed = true;

    }

    public void onKeybBtn4Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("4");
        else modifierText.setValue(modifierText.getValue().toString().concat("4"));
        pressed = true;

    }

    public void onKeybBtn5Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("5");
        else modifierText.setValue(modifierText.getValue().toString().concat("5"));
        pressed = true;

    }

    public void onKeybBtn6Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("6");
        else modifierText.setValue(modifierText.getValue().toString().concat("6"));
        pressed = true;

    }

    public void onKeybBtn7Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("7");
        else modifierText.setValue(modifierText.getValue().toString().concat("7"));
        pressed = true;

    }

    public void onKeybBtn8Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("8");
        else modifierText.setValue(modifierText.getValue().toString().concat("8"));
        pressed = true;

    }

    public void onKeybBtn9Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("9");
        else modifierText.setValue(modifierText.getValue().toString().concat("9"));
        pressed = true;

    }

    public void onKeybBtn0Click() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("0");
        else modifierText.setValue(modifierText.getValue().toString().concat("0"));
        pressed = true;

    }

    public void onKeybBtnBackspaceClick() {

        if (modifierText.getValue()!=null)
            modifierText.setValue(modifierText.getValue().toString().substring(0, modifierText.getValue().toString().length()-1));
        if (modifierText.getValue()==null) pressed = false;

    }

    public void onKeybBtnQClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("Q");
            else modifierText.setValue(modifierText.getValue().toString().concat("Q"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("q");
            else modifierText.setValue(modifierText.getValue().toString().concat("q"));

        }

        pressed = true;

    }

    public void onKeybBtnWClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("W");
            else modifierText.setValue(modifierText.getValue().toString().concat("w"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("w");
            else modifierText.setValue(modifierText.getValue().toString().concat("w"));

        }

        pressed = true;

    }

    public void onKeybBtnEClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("E");
            else modifierText.setValue(modifierText.getValue().toString().concat("E"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("e");
            else modifierText.setValue(modifierText.getValue().toString().concat("e"));

        }

        pressed = true;

    }

    public void onKeybBtnRClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("R");
            else modifierText.setValue(modifierText.getValue().toString().concat("R"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("r");
            else modifierText.setValue(modifierText.getValue().toString().concat("r"));

        }

        pressed = true;

    }

    public void onKeybBtnTClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("T");
            else modifierText.setValue(modifierText.getValue().toString().concat("T"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("t");
            else modifierText.setValue(modifierText.getValue().toString().concat("t"));

        }

        pressed = true;

    }

    public void onKeybBtnYClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("Y");
            else modifierText.setValue(modifierText.getValue().toString().concat("Y"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("y");
            else modifierText.setValue(modifierText.getValue().toString().concat("y"));

        }

        pressed = true;

    }

    public void onKeybBtnUClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("U");
            else modifierText.setValue(modifierText.getValue().toString().concat("U"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("u");
            else modifierText.setValue(modifierText.getValue().toString().concat("u"));

        }

        pressed = true;

    }

    public void onKeybBtnIClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("I");
            else modifierText.setValue(modifierText.getValue().toString().concat("I"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("i");
            else modifierText.setValue(modifierText.getValue().toString().concat("i"));

        }

        pressed = true;

    }

    public void onKeybBtnOClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("O");
            else modifierText.setValue(modifierText.getValue().toString().concat("O"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("o");
            else modifierText.setValue(modifierText.getValue().toString().concat("o"));

        }

        pressed = true;

    }

    public void onKeybBtnPClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("P");
            else modifierText.setValue(modifierText.getValue().toString().concat("P"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("p");
            else modifierText.setValue(modifierText.getValue().toString().concat("p"));

        }

        pressed = true;

    }

    public void onKeybBtnPlusClick() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("+");
        else modifierText.setValue(modifierText.getValue().toString().concat("+"));
        pressed = true;

    }

    public void onKeybBtnCapsClick() {

        if (!capsPressed) {

            keybBtnQ.setCaption("Q");
            keybBtnW.setCaption("W");
            keybBtnE.setCaption("E");
            keybBtnR.setCaption("R");
            keybBtnT.setCaption("T");
            keybBtnY.setCaption("Y");
            keybBtnU.setCaption("U");
            keybBtnI.setCaption("I");
            keybBtnO.setCaption("O");
            keybBtnP.setCaption("P");
            keybBtnA.setCaption("A");
            keybBtnS.setCaption("S");
            keybBtnD.setCaption("D");
            keybBtnF.setCaption("F");
            keybBtnG.setCaption("G");
            keybBtnH.setCaption("H");
            keybBtnJ.setCaption("J");
            keybBtnK.setCaption("K");
            keybBtnL.setCaption("L");
            keybBtnZ.setCaption("Z");
            keybBtnX.setCaption("X");
            keybBtnC.setCaption("C");
            keybBtnV.setCaption("V");
            keybBtnB.setCaption("B");
            keybBtnN.setCaption("N");
            keybBtnM.setCaption("M");

            capsPressed = true;

        } else {

            keybBtnQ.setCaption("q");
            keybBtnW.setCaption("w");
            keybBtnE.setCaption("e");
            keybBtnR.setCaption("r");
            keybBtnT.setCaption("t");
            keybBtnY.setCaption("y");
            keybBtnU.setCaption("u");
            keybBtnI.setCaption("i");
            keybBtnO.setCaption("o");
            keybBtnP.setCaption("p");
            keybBtnA.setCaption("a");
            keybBtnS.setCaption("s");
            keybBtnD.setCaption("d");
            keybBtnF.setCaption("f");
            keybBtnG.setCaption("g");
            keybBtnH.setCaption("h");
            keybBtnJ.setCaption("j");
            keybBtnK.setCaption("k");
            keybBtnL.setCaption("l");
            keybBtnZ.setCaption("z");
            keybBtnX.setCaption("x");
            keybBtnC.setCaption("c");
            keybBtnV.setCaption("v");
            keybBtnB.setCaption("b");
            keybBtnN.setCaption("n");
            keybBtnM.setCaption("m");

            capsPressed = false;

        }

    }

    public void onKeybBtnAClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("A");
            else modifierText.setValue(modifierText.getValue().toString().concat("A"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("a");
            else modifierText.setValue(modifierText.getValue().toString().concat("a"));

        }

        pressed = true;

    }

    public void onKeybBtnSClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("S");
            else modifierText.setValue(modifierText.getValue().toString().concat("S"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("s");
            else modifierText.setValue(modifierText.getValue().toString().concat("s"));

        }

        pressed = true;

    }

    public void onKeybBtnDClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("D");
            else modifierText.setValue(modifierText.getValue().toString().concat("D"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("d");
            else modifierText.setValue(modifierText.getValue().toString().concat("d"));

        }

        pressed = true;

    }

    public void onKeybBtnFClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("F");
            else modifierText.setValue(modifierText.getValue().toString().concat("F"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("f");
            else modifierText.setValue(modifierText.getValue().toString().concat("f"));

        }

        pressed = true;

    }

    public void onKeybBtnGClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("G");
            else modifierText.setValue(modifierText.getValue().toString().concat("G"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("g");
            else modifierText.setValue(modifierText.getValue().toString().concat("g"));

        }

        pressed = true;

    }

    public void onKeybBtnHClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("H");
            else modifierText.setValue(modifierText.getValue().toString().concat("H"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("h");
            else modifierText.setValue(modifierText.getValue().toString().concat("h"));

        }

        pressed = true;

    }

    public void onKeybBtnJClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("J");
            else modifierText.setValue(modifierText.getValue().toString().concat("J"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("j");
            else modifierText.setValue(modifierText.getValue().toString().concat("j"));

        }

        pressed = true;

    }

    public void onKeybBtnKClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("K");
            else modifierText.setValue(modifierText.getValue().toString().concat("K"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("k");
            else modifierText.setValue(modifierText.getValue().toString().concat("k"));

        }

        pressed = true;

    }

    public void onKeybBtnLClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("L");
            else modifierText.setValue(modifierText.getValue().toString().concat("L"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("l");
            else modifierText.setValue(modifierText.getValue().toString().concat("l"));

        }

        pressed = true;

    }

    public void onKeybBtnMinusClick() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("-");
        else modifierText.setValue(modifierText.getValue().toString().concat("-"));
        pressed = true;

    }
    
    public void onKeybBtnZClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("Z");
            else modifierText.setValue(modifierText.getValue().toString().concat("Z"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("z");
            else modifierText.setValue(modifierText.getValue().toString().concat("z"));

        }

        pressed = true;

    }

    public void onKeybBtnXClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("X");
            else modifierText.setValue(modifierText.getValue().toString().concat("X"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("x");
            else modifierText.setValue(modifierText.getValue().toString().concat("x"));

        }

        pressed = true;

    }

    public void onKeybBtnCClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("C");
            else modifierText.setValue(modifierText.getValue().toString().concat("C"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("c");
            else modifierText.setValue(modifierText.getValue().toString().concat("c"));

        }

        pressed = true;

    }

    public void onKeybBtnVClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("V");
            else modifierText.setValue(modifierText.getValue().toString().concat("V"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("v");
            else modifierText.setValue(modifierText.getValue().toString().concat("v"));

        }

        pressed = true;

    }

    public void onKeybBtnBClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("B");
            else modifierText.setValue(modifierText.getValue().toString().concat("B"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("b");
            else modifierText.setValue(modifierText.getValue().toString().concat("b"));

        }

        pressed = true;

    }

    public void onKeybBtnNClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("N");
            else modifierText.setValue(modifierText.getValue().toString().concat("N"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("n");
            else modifierText.setValue(modifierText.getValue().toString().concat("n"));

        }

        pressed = true;

    }

    public void onKeybBtnMClick() {

        if (capsPressed) {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("M");
            else modifierText.setValue(modifierText.getValue().toString().concat("M"));


        } else {

            if (modifierText.getValue()==null || !pressed) modifierText.setValue("m");
            else modifierText.setValue(modifierText.getValue().toString().concat("m"));

        }

        pressed = true;

    }

    public void onKeybBtnCommaClick() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue(",");
        else modifierText.setValue(modifierText.getValue().toString().concat(","));
        pressed = true;

    }

    public void onKeybBtnDotClick() {


        if (modifierText.getValue()==null || !pressed) modifierText.setValue(".");
        else modifierText.setValue(modifierText.getValue().toString().concat("."));
        pressed = true;

    }

    public void onKeybBtnAccentClick() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("'");
        else modifierText.setValue(modifierText.getValue().toString().concat("'"));
        pressed = true;

    }

    public void onKeybBtnSpaceClick() {

        if (modifierText.getValue()==null || !pressed) modifierText.setValue("_");
        else modifierText.setValue(modifierText.getValue().toString().concat("_"));
        pressed = true;

    }

    public void onNumPadBtnMinusClick() {

        if (modifierPrice.getValue()==null || !numPadPressed) {

            modifierPrice.setValue("-");
            numPadPressed = true;

        }

    }

    public void onNumPadBtn7Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("7");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("7"));
        numPadPressed = true;

    }

    public void onNumPadBtn8Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("8");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("8"));
        numPadPressed = true;

    }

    public void onNumPadBtn9Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("9");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("9"));
        numPadPressed = true;

    }

    public void onNumPadBtn4Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("4");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("4"));
        numPadPressed = true;

    }

    public void onNumPadBtn5Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("5");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("5"));
        numPadPressed = true;

    }

    public void onNumPadBtn6Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("6");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("6"));
        numPadPressed = true;

    }

    public void onNumPadBtn1Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("1");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("1"));
        numPadPressed = true;

    }

    public void onNumPadBtn2Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("2");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("2"));
        numPadPressed = true;

    }

    public void onNumPadBtn3Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("3");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("3"));
        numPadPressed = true;

    }

    public void onNumPadBtnCClick() {

        if (modifierPrice.getValue()!=null)
            modifierPrice.setValue(modifierPrice.getValue().toString().substring(0, modifierPrice.getValue().toString().length()-1));
        if (modifierPrice.getValue()==null) numPadPressed = false;

    }

    public void onNumPadBtn0Click() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue("0");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("0"));
        numPadPressed = true;

    }

    public void onNumPadBtnDotClick() {

        if (modifierPrice.getValue()==null || !numPadPressed) modifierPrice.setValue(".");
        else if (!modifierPrice.getValue().toString().contains(".")) modifierPrice.setValue(modifierPrice.getValue().toString().concat("."));
        numPadPressed = true;

    }

}