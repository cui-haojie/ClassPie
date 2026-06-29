package org.example.classpiserver.dto.account;

import lombok.Data;

@Data
public class AccountRequest {
    String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
