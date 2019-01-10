package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum PrinterGroup implements EnumClass<Integer> {

    Bar(10),
    Fryer(20),
    Grill(30);

    private Integer id;

    PrinterGroup(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static PrinterGroup fromId(Integer id) {
        for (PrinterGroup at : PrinterGroup.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}