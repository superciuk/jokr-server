package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum PrinterGroup implements EnumClass<String> {

    Bar("Bar"),
    UpBar("UpBar"),
    Fryer("Fryer"),
    Grill("Grill");

    private String id;

    PrinterGroup(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PrinterGroup fromId(String id) {
        for (PrinterGroup at : PrinterGroup.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}