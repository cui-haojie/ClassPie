package org.example.classpiserver.dto.attendance;

public class CheckInRequest {
    private Long session_id;
    private String account;
    private String code;

    public Long getSession_id() { return session_id; }
    public void setSession_id(Long session_id) { this.session_id = session_id; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
