package org.example.classpiserver.dto.homework;

public class ClassIdRequest {
    public Integer class_id;
    private String account;

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
