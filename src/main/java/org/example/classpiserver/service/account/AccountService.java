package org.example.classpiserver.service.account;

import org.example.classpiserver.dto.account.LoginResponse;
import org.example.classpiserver.dto.account.RegisterRequest;
import org.example.classpiserver.entity.Accounts;
import org.springframework.web.multipart.MultipartFile;

public interface AccountService {
    boolean addAccount(Accounts account);
    boolean register(RegisterRequest request);
    boolean changePassword(String password, String account);
    LoginResponse login(String account, String password);
    boolean selectAccountByAccount(String account);
    Accounts getAccount(String account);
    String getAccountName(String account);
    String getAccountStatus(String account);
    String uploadAvatar(String account, MultipartFile file);
    boolean updateAccount(Accounts account);
}
