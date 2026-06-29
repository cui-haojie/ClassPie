package org.example.classpiserver.dto.course;

import java.util.List;

public class CourseRequest {
    private String teacher_account;
    private String class_name;
    private String class_time;
    private String selected_classes;
    private String semester;
    private Integer school_class_id;
    private List<Integer> school_class_ids;
    private String code;

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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getTeacher_account() {
        return teacher_account;
    }

    public void setTeacher_account(String teacher_account) {
        this.teacher_account = teacher_account;
    }

    public Integer getSchool_class_id() {
        return school_class_id;
    }

    public void setSchool_class_id(Integer school_class_id) {
        this.school_class_id = school_class_id;
    }

    public List<Integer> getSchool_class_ids() {
        return school_class_ids;
    }

    public void setSchool_class_ids(List<Integer> school_class_ids) {
        this.school_class_ids = school_class_ids;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
