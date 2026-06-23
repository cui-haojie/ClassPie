package org.example.classpiserver.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Course {
private Long id;
private String teacher_account;
private String class_name;
private String class_time;
private String selected_classes;
private String code;
private Boolean is_pinned;
private Integer school_class_id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSelected_classes() {
        return selected_classes;
    }

    public void setSelected_classes(String selected_classes) {
        this.selected_classes = selected_classes;
    }

    public String getTeacher_account() {
        return teacher_account;
    }

    public void setTeacher_account(String teacher_account) {
        this.teacher_account = teacher_account;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIs_pinned() {
        return is_pinned;
    }

    public void setIs_pinned(Boolean is_pinned) {
        this.is_pinned = is_pinned;
    }

    public Integer getSchool_class_id() {
        return school_class_id;
    }

    public void setSchool_class_id(Integer school_class_id) {
        this.school_class_id = school_class_id;
    }
}
