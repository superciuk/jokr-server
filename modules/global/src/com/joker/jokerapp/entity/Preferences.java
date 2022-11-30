package com.joker.jokerapp.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.security.entity.User;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "JOKERAPP_PREFERENCES")
@Entity(name = "jokerapp_Preferences")
public class Preferences extends StandardEntity {
    private static final long serialVersionUID = 3144834897172622052L;

    @JoinColumn(name = "USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(DeletePolicy.UNLINK)
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @Lookup(type = LookupType.DROPDOWN, actions = "lookup")
    private User user;

    @Column(name = "SCREEN_ORIENTATION")
    private String screenOrientation;

    @Column(name = "TASK")
    private String task;

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(String screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}