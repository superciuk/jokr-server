package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|number")
@Table(name = "JOKERAPP_TABLE_ITEM")
@Entity(name = "jokerapp$TableItem")
public class TableItem extends StandardEntity {
    private static final long serialVersionUID = -4769521418786074068L;

    @NotNull
    @Column(name = "NUMBER_", nullable = false)
    protected Integer number;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}