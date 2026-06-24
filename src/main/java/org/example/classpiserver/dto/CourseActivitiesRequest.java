package org.example.classpiserver.dto;

public class CourseActivitiesRequest {
    private Integer class_id;
    private String type;

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
