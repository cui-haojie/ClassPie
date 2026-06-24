package org.example.classpiserver.entity;

import lombok.Data;

@Data
public class CourseMember {
    private String account;
    private String name;
    private String status;
    private String status_number;
    private String avatar_url;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
