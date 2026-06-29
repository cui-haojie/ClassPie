package org.example.classpiserver.dto.schoolclass;

public class JoinStudentClassRequest {
    private String account;
    private Integer school_class_id;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getSchool_class_id() {
        return school_class_id;
    }

    public void setSchool_class_id(Integer school_class_id) {
        this.school_class_id = school_class_id;
    }
}
