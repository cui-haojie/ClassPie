package org.example.classpiserver.entity;

public class CourseActivity {
    private Long id;
    private Long class_id;
    private String type;
    private String title;
    private String content;
    private String attachment_url;
    private String attachment_name;
    private String start_time;
    private String deadline;
    private String creator_account;
    private String creator_name;
    private String create_time;
    private int reply_count;
    private int choice_count;
    private int short_count;
    private String publish_status;
    private String interaction_kind;
    private String interaction_options;

    public String getInteraction_kind() {
        return interaction_kind;
    }

    public void setInteraction_kind(String interaction_kind) {
        this.interaction_kind = interaction_kind;
    }

    public String getInteraction_options() {
        return interaction_options;
    }

    public void setInteraction_options(String interaction_options) {
        this.interaction_options = interaction_options;
    }

    public String getPublish_status() {
        return publish_status;
    }

    public void setPublish_status(String publish_status) {
        this.publish_status = publish_status;
    }

    public int getChoice_count() {
        return choice_count;
    }

    public void setChoice_count(int choice_count) {
        this.choice_count = choice_count;
    }

    public int getShort_count() {
        return short_count;
    }

    public void setShort_count(int short_count) {
        this.short_count = short_count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClass_id() {
        return class_id;
    }

    public void setClass_id(Long class_id) {
        this.class_id = class_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getCreator_account() {
        return creator_account;
    }

    public void setCreator_account(String creator_account) {
        this.creator_account = creator_account;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getReply_count() {
        return reply_count;
    }

    public void setReply_count(int reply_count) {
        this.reply_count = reply_count;
    }
}
