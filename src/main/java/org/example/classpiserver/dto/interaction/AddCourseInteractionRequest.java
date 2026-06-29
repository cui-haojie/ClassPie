package org.example.classpiserver.dto.interaction;

import java.util.List;

public class AddCourseInteractionRequest {
    private Integer class_id;
    private String title;
    private String content;
    private String interaction_kind;
    private List<String> options;
    private String creator_account;
    private String deadline;

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

    public String getInteraction_kind() {
        return interaction_kind;
    }

    public void setInteraction_kind(String interaction_kind) {
        this.interaction_kind = interaction_kind;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCreator_account() {
        return creator_account;
    }

    public void setCreator_account(String creator_account) {
        this.creator_account = creator_account;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
