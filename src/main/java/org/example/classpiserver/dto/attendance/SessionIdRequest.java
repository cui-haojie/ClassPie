package org.example.classpiserver.dto.attendance;

public class SessionIdRequest {
    private Long session_id;
    private String teacher_account;
    private String account;

    public Long getSession_id() { return session_id; }
    public void setSession_id(Long session_id) { this.session_id = session_id; }
    public String getTeacher_account() { return teacher_account; }
    public void setTeacher_account(String teacher_account) { this.teacher_account = teacher_account; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
}
