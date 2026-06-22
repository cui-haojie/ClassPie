package org.example.classpiserver.entity;

import lombok.Data;

@Data
public class Courses_homework {
    private int class_id;
    private int homework_id;

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public int getHomework_id() {
        return homework_id;
    }

    public void setHomework_id(int homework_id) {
        this.homework_id = homework_id;
    }
}
