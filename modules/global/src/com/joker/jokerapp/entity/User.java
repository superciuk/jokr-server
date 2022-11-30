package com.joker.jokerapp.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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

    @NotNull
    @Column(name = "USER_STATUS", nullable = false)
    protected String userStatus;

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

    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus == null ? null : userStatus.getId(); }

    public UserStatus getUserStatus() { return userStatus == null ? null : UserStatus.fromId(userStatus); }
}