package org.example.classpiserver.dto.ai;

public class AiGenerateTestRequest {
    private String teacher_account;
    private String topic;
    private String course_name;
    private Integer choice_count;
    private Integer short_count;

    public String getTeacher_account() {
        return teacher_account;
    }

    public void setTeacher_account(String teacher_account) {
        this.teacher_account = teacher_account;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public Integer getChoice_count() {
        return choice_count;
    }

    public void setChoice_count(Integer choice_count) {
        this.choice_count = choice_count;
    }

    public Integer getShort_count() {
        return short_count;
    }

    public void setShort_count(Integer short_count) {
        this.short_count = short_count;
    }
}
