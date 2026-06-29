package org.example.classpiserver.entity;

public class AttendanceRecord {
    private Long id;
    private Long session_id;
    private String account;
    private String status;
    private String check_time;
    private String account_name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSession_id() { return session_id; }
    public void setSession_id(Long session_id) { this.session_id = session_id; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCheck_time() { return check_time; }
    public void setCheck_time(String check_time) { this.check_time = check_time; }
    public String getAccount_name() { return account_name; }
    public void setAccount_name(String account_name) { this.account_name = account_name; }
}
