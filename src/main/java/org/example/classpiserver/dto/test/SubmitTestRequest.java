package org.example.classpiserver.dto.test;

import java.util.List;

public class SubmitTestRequest {
    private Long activity_id;
    private String account;
    private List<TestAnswerInput> answers;

    public Long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Long activity_id) {
        this.activity_id = activity_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<TestAnswerInput> getAnswers() {
        return answers;
    }

    public void setAnswers(List<TestAnswerInput> answers) {
        this.answers = answers;
    }
}
