package org.example.classpiserver.entity;

import lombok.Data;

@Data
public class Content {
    private Long content_id;
    private String details;
    private int score;
    private String account;
    private String attachment_url;
    private String attachment_name;
    private Boolean is_graded;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getContent_id() {
        return content_id;
    }

    public void setContent_id(Long content_id) {
        this.content_id = content_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

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

    public Boolean getIs_graded() {
        return is_graded;
    }

    public void setIs_graded(Boolean is_graded) {
        this.is_graded = is_graded;
    }
}
