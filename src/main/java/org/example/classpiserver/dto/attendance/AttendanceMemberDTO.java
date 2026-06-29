package org.example.classpiserver.dto.attendance;

public class AttendanceMemberDTO {
    private String account;
    private String name;
    private String status_number;

    public AttendanceMemberDTO() {
    }

    public AttendanceMemberDTO(String account, String name, String status_number) {
        this.account = account;
        this.name = name;
        this.status_number = status_number;
    }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus_number() { return status_number; }
    public void setStatus_number(String status_number) { this.status_number = status_number; }
}
