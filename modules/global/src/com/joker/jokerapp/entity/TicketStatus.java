package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TicketStatus implements EnumClass<String> {

    notSended("notSended"),
    sended("sended"),
    cancelled("cancelled");

    private String id;

    TicketStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TicketStatus fromId(String id) {
        for (TicketStatus at : TicketStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}