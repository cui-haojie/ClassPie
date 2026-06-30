package org.example.classpiserver.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Homework {
    private int homework_id;
    private String submitter;
    private int content_id;
    private String name;
    private String deadline;
    private boolean isEnd;
    private String type;
    private boolean isCorrect;
    private int score;
    private String details;

    private int graded_count;
    private int ungraded_count;
    private int unsubmitted_count;
    private String attachment_url;
    private String attachment_name;

    /** 当前学生是否已提交（列表接口传入 account 时填充） */
    @JsonProperty("my_submitted")
    private Boolean my_submitted;
    /** 当前学生是否已被批阅 */
    @JsonProperty("my_graded")
    private Boolean my_graded;
    /** 当前学生得分 */
    @JsonProperty("my_score")
    private Integer my_score;

    public String getAttachment_url() {
        return attachment_url;
    }

    public void setAttachment_url(String attachment_url) {
        this.attachment_url = attachment_url;
    }

    public String getAttachment_name() {
        return attachment_name;
    }

    public void setAttachment_name(String attachment_name) {
        this.attachment_name = attachment_name;
    }

    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getHomework_id() {
        return homework_id;
    }

    public void setHomework_id(int homework_id) {
        this.homework_id = homework_id;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGraded_count() {
        return graded_count;
    }

    public void setGraded_count(int graded_count) {
        this.graded_count = graded_count;
    }

    public int getUngraded_count() {
        return ungraded_count;
    }

    public void setUngraded_count(int ungraded_count) {
        this.ungraded_count = ungraded_count;
    }

    public int getUnsubmitted_count() {
        return unsubmitted_count;
    }

    public void setUnsubmitted_count(int unsubmitted_count) {
        this.unsubmitted_count = unsubmitted_count;
    }

    public Boolean getMy_submitted() {
        return my_submitted;
    }

    public void setMy_submitted(Boolean my_submitted) {
        this.my_submitted = my_submitted;
    }

    public Boolean getMy_graded() {
        return my_graded;
    }

    public void setMy_graded(Boolean my_graded) {
        this.my_graded = my_graded;
    }

    public Integer getMy_score() {
        return my_score;
    }

    public void setMy_score(Integer my_score) {
        this.my_score = my_score;
    }
}
