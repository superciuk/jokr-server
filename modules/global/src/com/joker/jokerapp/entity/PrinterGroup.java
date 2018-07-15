package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_PRINTER_GROUP")
@Entity(name = "jokerapp$PrinterGroup")
public class PrinterGroup extends StandardEntity {
    private static final long serialVersionUID = -2719994457085713347L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRINTER_ID")
    protected Printer printer;

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public Printer getPrinter() {
        return printer;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}