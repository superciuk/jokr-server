package com.joker.jokerapp.web.popups;

import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Named;
import java.math.BigDecimal;

@UiController("jokerapp_ManualModifier")
@UiDescriptor("item-manual-modifier.xml")
public class ItemManualModifier extends Screen {

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

    private Boolean pressed = false;
    private Boolean numPadPressed = false;
    private Boolean capsPressed = false;

    String itemModifierName = "";
    BigDecimal itemModifierPrice = BigDecimal.ZERO;

    @Subscribe
    protected void onInit(InitEvent event) {

        modifierPrice.setValue("0");

    }

    public String getItemModifierName () {

        return itemModifierName;

    }

    public BigDecimal getItemModifierPrice () {

        return itemModifierPrice;

    }

    @Subscribe("okBtn")
    protected void onOkBtnClick(Button.ClickEvent event) {

        if (modifierText.getValue()!=null) {

            itemModifierName = modifierText.getValue().toString().replaceAll("_", " ");
            itemModifierPrice = BigDecimal.valueOf(Double.parseDouble(modifierPrice.getRawValue()));

            close(WINDOW_COMMIT_AND_CLOSE_ACTION);

        }

    }

    @Subscribe("cancelBtn")
    protected void onCancelBtnClick(Button.ClickEvent event) {

        closeWithDefaultAction();

    }
    
    @Subscribe("keybBtn1")
    public void onKeybBtn1Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("1");
        else modifierText.setValue(modifierText.getValue().toString().concat("1"));
        pressed = true;

    }

    @Subscribe("keybBtn2")
    public void onKeybBtn2Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("2");
        else modifierText.setValue(modifierText.getValue().toString().concat("2"));
        pressed = true;

    }

    @Subscribe("keybBtn3")
    public void onKeybBtn3Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("3");
        else modifierText.setValue(modifierText.getValue().toString().concat("3"));
        pressed = true;

    }

    @Subscribe("keybBtn4")
    public void onKeybBtn4Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("4");
        else modifierText.setValue(modifierText.getValue().toString().concat("4"));
        pressed = true;

    }

    @Subscribe("keybBtn5")
    public void onKeybBtn5Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("5");
        else modifierText.setValue(modifierText.getValue().toString().concat("5"));
        pressed = true;

    }

    @Subscribe("keybBtn6")
    public void onKeybBtn6Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("6");
        else modifierText.setValue(modifierText.getValue().toString().concat("6"));
        pressed = true;

    }

    @Subscribe("keybBtn7")
    public void onKeybBtn7Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("7");
        else modifierText.setValue(modifierText.getValue().toString().concat("7"));
        pressed = true;

    }

    @Subscribe("keybBtn8")
    public void onKeybBtn8Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("8");
        else modifierText.setValue(modifierText.getValue().toString().concat("8"));
        pressed = true;

    }

    @Subscribe("keybBtn9")
    public void onKeybBtn9Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("9");
        else modifierText.setValue(modifierText.getValue().toString().concat("9"));
        pressed = true;

    }

    @Subscribe("keybBtn0")
    public void onKeybBtn0Click(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("0");
        else modifierText.setValue(modifierText.getValue().toString().concat("0"));
        pressed = true;

    }

    @Subscribe("keybBtnBackspace")
    public void onKeybBtnBackspaceClick(Button.ClickEvent event) {

        if (modifierText.getValue()!=null)
            modifierText.setValue(modifierText.getValue().toString().substring(0, modifierText.getValue().toString().length()-1));
        if (modifierText.getValue() == null) pressed = false;

    }

    @Subscribe("keybBtnQ")
    public void onKeybBtnQClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("Q");
            else modifierText.setValue(modifierText.getValue().toString().concat("Q"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("q");
            else modifierText.setValue(modifierText.getValue().toString().concat("q"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnW")
    public void onKeybBtnWClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("W");
            else modifierText.setValue(modifierText.getValue().toString().concat("W"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("w");
            else modifierText.setValue(modifierText.getValue().toString().concat("w"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnE")
    public void onKeybBtnEClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("E");
            else modifierText.setValue(modifierText.getValue().toString().concat("E"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("e");
            else modifierText.setValue(modifierText.getValue().toString().concat("e"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnR")
    public void onKeybBtnRClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("R");
            else modifierText.setValue(modifierText.getValue().toString().concat("R"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("r");
            else modifierText.setValue(modifierText.getValue().toString().concat("r"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnT")
    public void onKeybBtnTClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("T");
            else modifierText.setValue(modifierText.getValue().toString().concat("T"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("t");
            else modifierText.setValue(modifierText.getValue().toString().concat("t"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnY")
    public void onKeybBtnYClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("Y");
            else modifierText.setValue(modifierText.getValue().toString().concat("Y"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("y");
            else modifierText.setValue(modifierText.getValue().toString().concat("y"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnU")
    public void onKeybBtnUClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("U");
            else modifierText.setValue(modifierText.getValue().toString().concat("U"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("u");
            else modifierText.setValue(modifierText.getValue().toString().concat("u"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnI")
    public void onKeybBtnIClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("I");
            else modifierText.setValue(modifierText.getValue().toString().concat("I"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("i");
            else modifierText.setValue(modifierText.getValue().toString().concat("i"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnO")
    public void onKeybBtnOClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("O");
            else modifierText.setValue(modifierText.getValue().toString().concat("O"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("o");
            else modifierText.setValue(modifierText.getValue().toString().concat("o"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnP")
    public void onKeybBtnPClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("P");
            else modifierText.setValue(modifierText.getValue().toString().concat("P"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("p");
            else modifierText.setValue(modifierText.getValue().toString().concat("p"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnPlus")
    public void onKeybBtnPlusClick(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("+");
        else modifierText.setValue(modifierText.getValue().toString().concat("+"));
        pressed = true;

    }

    @Subscribe("keybBtnCapsLock")
    public void onKeybBtnCapsLockClick(Button.ClickEvent event) {

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

    @Subscribe("keybBtnA")
    public void onKeybBtnAClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("A");
            else modifierText.setValue(modifierText.getValue().toString().concat("A"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("a");
            else modifierText.setValue(modifierText.getValue().toString().concat("a"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnS")
    public void onKeybBtnSClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("S");
            else modifierText.setValue(modifierText.getValue().toString().concat("S"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("s");
            else modifierText.setValue(modifierText.getValue().toString().concat("s"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnD")
    public void onKeybBtnDClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("D");
            else modifierText.setValue(modifierText.getValue().toString().concat("D"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("d");
            else modifierText.setValue(modifierText.getValue().toString().concat("d"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnF")
    public void onKeybBtnFClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("F");
            else modifierText.setValue(modifierText.getValue().toString().concat("F"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("f");
            else modifierText.setValue(modifierText.getValue().toString().concat("f"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnG")
    public void onKeybBtnGClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("G");
            else modifierText.setValue(modifierText.getValue().toString().concat("G"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("g");
            else modifierText.setValue(modifierText.getValue().toString().concat("g"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnH")
    public void onKeybBtnHClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("H");
            else modifierText.setValue(modifierText.getValue().toString().concat("H"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("h");
            else modifierText.setValue(modifierText.getValue().toString().concat("h"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnJ")
    public void onKeybBtnJClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("J");
            else modifierText.setValue(modifierText.getValue().toString().concat("J"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("j");
            else modifierText.setValue(modifierText.getValue().toString().concat("j"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnK")
    public void onKeybBtnKClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("K");
            else modifierText.setValue(modifierText.getValue().toString().concat("K"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("k");
            else modifierText.setValue(modifierText.getValue().toString().concat("k"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnL")
    public void onKeybBtnLClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("L");
            else modifierText.setValue(modifierText.getValue().toString().concat("L"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("l");
            else modifierText.setValue(modifierText.getValue().toString().concat("l"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnMinus")
    public void onKeybBtnMinusClick(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("-");
        else modifierText.setValue(modifierText.getValue().toString().concat("-"));
        pressed = true;

    }

    @Subscribe("keybBtnZ")
    public void onKeybBtnZClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("Z");
            else modifierText.setValue(modifierText.getValue().toString().concat("Z"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("z");
            else modifierText.setValue(modifierText.getValue().toString().concat("z"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnX")
    public void onKeybBtnXClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("X");
            else modifierText.setValue(modifierText.getValue().toString().concat("X"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("x");
            else modifierText.setValue(modifierText.getValue().toString().concat("x"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnC")
    public void onKeybBtnCClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("C");
            else modifierText.setValue(modifierText.getValue().toString().concat("C"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("c");
            else modifierText.setValue(modifierText.getValue().toString().concat("c"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnV")
    public void onKeybBtnVClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("V");
            else modifierText.setValue(modifierText.getValue().toString().concat("V"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("v");
            else modifierText.setValue(modifierText.getValue().toString().concat("v"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnB")
    public void onKeybBtnBClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("B");
            else modifierText.setValue(modifierText.getValue().toString().concat("B"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("b");
            else modifierText.setValue(modifierText.getValue().toString().concat("b"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnN")
    public void onKeybBtnNClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("N");
            else modifierText.setValue(modifierText.getValue().toString().concat("N"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("n");
            else modifierText.setValue(modifierText.getValue().toString().concat("n"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnM")
    public void onKeybBtnMClick(Button.ClickEvent event) {

        if (capsPressed) {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("M");
            else modifierText.setValue(modifierText.getValue().toString().concat("M"));


        } else {

            if (modifierText.getValue() == null || !pressed) modifierText.setValue("m");
            else modifierText.setValue(modifierText.getValue().toString().concat("m"));

        }

        pressed = true;

    }

    @Subscribe("keybBtnComma")
    public void onKeybBtnCommaClick(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue(",");
        else modifierText.setValue(modifierText.getValue().toString().concat(","));
        pressed = true;

    }

    @Subscribe("keybBtnDot")
    public void onKeybBtnDotClick(Button.ClickEvent event) {


        if (modifierText.getValue() == null || !pressed) modifierText.setValue(".");
        else modifierText.setValue(modifierText.getValue().toString().concat("."));
        pressed = true;

    }

    @Subscribe("keybBtnAccent")
    public void onKeybBtnAccentClick(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("'");
        else modifierText.setValue(modifierText.getValue().toString().concat("'"));
        pressed = true;

    }

    @Subscribe("keybBtnSpace")
    public void onKeybBtnSpaceClick(Button.ClickEvent event) {

        if (modifierText.getValue() == null || !pressed) modifierText.setValue("_");
        else modifierText.setValue(modifierText.getValue().toString().concat("_"));
        pressed = true;

    }

    @Subscribe("numPadBtnMinus")
    public void onNumPadBtnMinusClick(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) {

            modifierPrice.setValue("-");
            numPadPressed = true;

        }

    }

    @Subscribe("numPadBtn7")
    public void onNumPadBtn7Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("7");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("7"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtn8")
    public void onNumPadBtn8Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("8");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("8"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtn9")
    public void onNumPadBtn9Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("9");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("9"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtn4")
    public void onNumPadBtn4Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("4");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("4"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtn5")
    public void onNumPadBtn5Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("5");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("5"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtn6")
    public void onNumPadBtn6Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("6");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("6"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtn1")
    public void onNumPadBtn1Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("1");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("1"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtn2")
    public void onNumPadBtn2Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("2");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("2"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtn3")
    public void onNumPadBtn3Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("3");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("3"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtnC")
    public void onNumPadBtnCClick(Button.ClickEvent event) {

        if (modifierPrice.getValue()!=null)
            modifierPrice.setValue(modifierPrice.getValue().toString().substring(0, modifierPrice.getValue().toString().length()-1));
        if (modifierPrice.getValue() == null) numPadPressed = false;

    }

    @Subscribe("numPadBtn0")
    public void onNumPadBtn0Click(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue("0");
        else modifierPrice.setValue(modifierPrice.getValue().toString().concat("0"));
        numPadPressed = true;

    }

    @Subscribe("numPadBtnDot")
    public void onNumPadBtnDotClick(Button.ClickEvent event) {

        if (modifierPrice.getValue() == null || !numPadPressed) modifierPrice.setValue(".");
        else if (!modifierPrice.getValue().toString().contains(".")) modifierPrice.setValue(modifierPrice.getValue().toString().concat("."));
        numPadPressed = true;

    }

}