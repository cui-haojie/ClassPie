package org.example.classpiserver.dto.homework;

import org.example.classpiserver.entity.Homework;

public class HomeworkRequest {
    private Homework homework;
    private int class_id;

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public Homework getHomework() {
        return homework;
    }

    public void setHomework(Homework homework) {
        this.homework = homework;
    }
}
