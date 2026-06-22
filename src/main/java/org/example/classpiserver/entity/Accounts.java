package org.example.classpiserver.entity;

import lombok.Data;

@Data
public class Accounts {
    private String account;
    private String password;
    private String name;
    private String status;
    private String mechanism;
    private String email_or_phone = null;
    private String status_number = null;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail_or_phone() {
        return email_or_phone;
    }

    public void setEmail_or_phone(String email_or_phone) {
        this.email_or_phone = email_or_phone;
    }

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_number() {
        return status_number;
    }

    public void setStatus_number(String status_number) {
        this.status_number = status_number;
    }
    public Accounts() {}
}
