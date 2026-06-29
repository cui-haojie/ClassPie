package org.example.classpiserver.dto.prep;

public class ListPrepRequest {
    private String teacher_account;
    private String kind;

    public String getTeacher_account() { return teacher_account; }
    public void setTeacher_account(String teacher_account) { this.teacher_account = teacher_account; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
}
