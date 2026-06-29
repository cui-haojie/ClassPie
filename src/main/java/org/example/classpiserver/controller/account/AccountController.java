package org.example.classpiserver.controller.account;

import org.example.classpiserver.dto.account.AccountRequest;
import org.example.classpiserver.dto.account.LoginRequest;
import org.example.classpiserver.dto.account.RegisterRequest;
import org.example.classpiserver.entity.Accounts;
import org.example.classpiserver.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/editor")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/account")
    public ResponseEntity<Accounts> getAccount(@RequestBody AccountRequest request) {
        Accounts result = accountService.getAccount(request.getAccount());
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Accounts());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public boolean addAccount(@RequestBody RegisterRequest request) {
        return accountService.register(request);
    }

    @PutMapping("/change")
    public boolean changePassword(@RequestBody LoginRequest request) {
        return accountService.changePassword(request.getPassword(), request.getAccount());
    }

    @PostMapping("/login")
    public Accounts login(@RequestBody LoginRequest request) {
        return accountService.login(request.getAccount(), request.getPassword());
    }

    @PostMapping("/check")
    public boolean selectAccountByAccount(@RequestBody String account) {
        return accountService.selectAccountByAccount(account);
    }

    @PostMapping("/selectTeacherName")
    public String getAccountName(@RequestBody AccountRequest request) {
        return accountService.getAccountName(request.getAccount());
    }

    @PostMapping("/getAccountStatus")
    public String getAccountStatus(@RequestBody AccountRequest request) {
        return accountService.getAccountStatus(request.getAccount());
    }

    @PostMapping(value = "/uploadAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<java.util.Map<String, String>> uploadAvatar(
            @RequestParam("account") String account,
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = accountService.uploadAvatar(account, file);
        if (avatarUrl == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("message", "头像上传失败"));
        }
        return ResponseEntity.ok(java.util.Map.of("avatar_url", avatarUrl));
    }

    @PutMapping("/updateAccount")
    public boolean updateAccount(@RequestBody Accounts account) {
        return accountService.updateAccount(account);
    }
}
