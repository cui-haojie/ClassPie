package org.example.classpiserver.dto.course;

public class LeaveCourseRequest {
    private String account;
    private Long id;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
