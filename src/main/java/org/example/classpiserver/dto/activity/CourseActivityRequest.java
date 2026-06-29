package org.example.classpiserver.dto.activity;

import org.example.classpiserver.entity.CourseActivity;

public class CourseActivityRequest {
    private Integer class_id;
    private CourseActivity activity;

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    public CourseActivity getActivity() {
        return activity;
    }

    public void setActivity(CourseActivity activity) {
        this.activity = activity;
    }
}
