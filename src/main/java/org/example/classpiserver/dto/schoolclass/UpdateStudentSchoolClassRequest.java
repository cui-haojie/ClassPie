package org.example.classpiserver.dto.schoolclass;

import java.util.List;

public class UpdateStudentSchoolClassRequest {
    private String account;
    private List<Integer> school_class_ids;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<Integer> getSchool_class_ids() {
        return school_class_ids;
    }

    public void setSchool_class_ids(List<Integer> school_class_ids) {
        this.school_class_ids = school_class_ids;
    }
}
