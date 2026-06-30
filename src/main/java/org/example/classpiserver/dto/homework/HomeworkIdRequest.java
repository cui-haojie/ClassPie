package org.example.classpiserver.dto.homework;

public class HomeworkIdRequest {
    private Integer homework_id;
    private String account;

    public Integer getHomework_id() {
        return homework_id;
    }

    public void setHomework_id(Integer homework_id) {
        this.homework_id = homework_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
