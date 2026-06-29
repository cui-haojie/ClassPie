package org.example.classpiserver.dto.attendance;

public class ClassIdAccountRequest {
    private Long class_id;
    private String account;

    public Long getClass_id() { return class_id; }
    public void setClass_id(Long class_id) { this.class_id = class_id; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
}
