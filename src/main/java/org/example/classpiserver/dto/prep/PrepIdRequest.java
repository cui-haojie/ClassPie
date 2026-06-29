package org.example.classpiserver.dto.prep;

public class PrepIdRequest {
    private Long id;
    private String teacher_account;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeacher_account() { return teacher_account; }
    public void setTeacher_account(String teacher_account) { this.teacher_account = teacher_account; }
}
