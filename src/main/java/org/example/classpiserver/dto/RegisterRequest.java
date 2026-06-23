package org.example.classpiserver.dto;

public class RegisterRequest {
    private String account;
    private String password;
    private String name;
    private String status;
    private String mechanism;
    private String status_number;
    private Integer school_class_id;
    private java.util.List<Integer> school_class_ids;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getStatus_number() {
        return status_number;
    }

    public void setStatus_number(String status_number) {
        this.status_number = status_number;
    }

    public Integer getSchool_class_id() {
        return school_class_id;
    }

    public void setSchool_class_id(Integer school_class_id) {
        this.school_class_id = school_class_id;
    }

    public java.util.List<Integer> getSchool_class_ids() {
        return school_class_ids;
    }

    public void setSchool_class_ids(java.util.List<Integer> school_class_ids) {
        this.school_class_ids = school_class_ids;
    }
}
