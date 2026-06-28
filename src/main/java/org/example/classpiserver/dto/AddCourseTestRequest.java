package org.example.classpiserver.dto;

import java.util.List;

public class AddCourseTestRequest {
    private Integer class_id;
    private String title;
    private String content;
    private String start_time;
    private String deadline;
    private String creator_account;
    private List<TestQuestionInput> questions;
    private Long activity_id;

    public Long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Long activity_id) {
        this.activity_id = activity_id;
    }

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getCreator_account() {
        return creator_account;
    }

    public void setCreator_account(String creator_account) {
        this.creator_account = creator_account;
    }

    public List<TestQuestionInput> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TestQuestionInput> questions) {
        this.questions = questions;
    }
}
