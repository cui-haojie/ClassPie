package org.example.classpiserver.dto.course;

import java.util.List;

public class CourseOrderRequest {
    private String account;
    private List<Long> course_ids;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<Long> getCourse_ids() {
        return course_ids;
    }

    public void setCourse_ids(List<Long> course_ids) {
        this.course_ids = course_ids;
    }
}
