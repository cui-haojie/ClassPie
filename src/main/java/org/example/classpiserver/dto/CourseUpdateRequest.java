package org.example.classpiserver.dto;

public class CourseUpdateRequest {
    private Long id;
    private String teacher_account;
    private String class_name;
    private String class_time;
    private String selected_classes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeacher_account() {
        return teacher_account;
    }

    public void setTeacher_account(String teacher_account) {
        this.teacher_account = teacher_account;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getClass_time() {
        return class_time;
    }

    public void setClass_time(String class_time) {
        this.class_time = class_time;
    }

    public String getSelected_classes() {
        return selected_classes;
    }

    public void setSelected_classes(String selected_classes) {
        this.selected_classes = selected_classes;
    }
}
