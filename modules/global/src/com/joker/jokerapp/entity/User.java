package com.joker.jokerapp.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "JOKERAPP_USER")
@Entity(name = "jokerapp_User")
public class User extends StandardEntity {
    private static final long serialVersionUID = 8659211658710801775L;

    @NotNull
    @Column(name = "USERNAME", nullable = false, unique = true)
    protected String username;

    @NotNull
    @Column(name = "ENCRYPTED_USER_PASSWORD", nullable = false)
    protected String encryptedUserPassword;

    @NotNull
    @Column(name = "USER_TYPE", nullable = false)
    protected String userType;

    @Lookup(type = LookupType.DROPDOWN)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "WORKPLACE_ID")
    protected Workplace workplace;

    @JoinTable(name = "JOKERAPP_USER_WORKPLACE_LINK",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "WORKPLACE_ID"))
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToMany
    protected List<Workplace> workplacesAllowedToNotify;

    @NotNull
    @Column(name = "USER_STATUS", nullable = false)
    protected String userStatus;

    @Column(name = "NOTIFICATION_TOKEN")
    protected String notificationToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedUserPassword() {
        return encryptedUserPassword;
    }

    public void setEncryptedUserPassword(String encryptedUserPassword) {
        this.encryptedUserPassword = encryptedUserPassword;
    }

    public void setUserType(UserType userType) { this.userType = userType == null ? null : userType.getId(); }

    public UserType getUserType() { return userType == null ? null : UserType.fromId(userType); }

    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }

    public void setWorkplacesAllowedToNotify(List<Workplace> workplaces) { this.workplacesAllowedToNotify = workplaces; }

    public List<Workplace> getWorkplacesAllowedToNotify() {
        return workplacesAllowedToNotify;
    }

    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus == null ? null : userStatus.getId(); }

    public UserStatus getUserStatus() { return userStatus == null ? null : UserStatus.fromId(userStatus); }

    public String getNotificationToken() { return notificationToken; }

    public void setNotificationToken(String notificationToken) { this.notificationToken = notificationToken; }

}