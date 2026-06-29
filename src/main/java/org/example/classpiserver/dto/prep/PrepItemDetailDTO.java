package org.example.classpiserver.dto.prep;

import org.example.classpiserver.entity.TeacherPrepTestQuestion;

import java.util.List;

public class PrepItemDetailDTO {
    private Long id;
    private String teacher_account;
    private String kind;
    private String title;
    private String content;
    private String attachment_url;
    private String attachment_name;
    private String meta_json;
    private String homework_type;
    private String deadline;
    private String start_time;
    private String create_time;
    private String update_time;
    private List<TeacherPrepTestQuestion> questions;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeacher_account() { return teacher_account; }
    public void setTeacher_account(String teacher_account) { this.teacher_account = teacher_account; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAttachment_url() { return attachment_url; }
    public void setAttachment_url(String attachment_url) { this.attachment_url = attachment_url; }
    public String getAttachment_name() { return attachment_name; }
    public void setAttachment_name(String attachment_name) { this.attachment_name = attachment_name; }
    public String getMeta_json() { return meta_json; }
    public void setMeta_json(String meta_json) { this.meta_json = meta_json; }
    public String getHomework_type() { return homework_type; }
    public void setHomework_type(String homework_type) { this.homework_type = homework_type; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }
    public String getCreate_time() { return create_time; }
    public void setCreate_time(String create_time) { this.create_time = create_time; }
    public String getUpdate_time() { return update_time; }
    public void setUpdate_time(String update_time) { this.update_time = update_time; }
    public List<TeacherPrepTestQuestion> getQuestions() { return questions; }
    public void setQuestions(List<TeacherPrepTestQuestion> questions) { this.questions = questions; }
}
