package org.example.classpiserver.service.account;

import org.example.classpiserver.dto.account.RegisterRequest;
import org.example.classpiserver.entity.Accounts;
import org.example.classpiserver.mapper.account.AccountMapper;
import org.example.classpiserver.mapper.schoolclass.SchoolClassMapper;
import org.example.classpiserver.support.EnrollmentSupport;
import org.example.classpiserver.util.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private SchoolClassMapper schoolClassMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EnrollmentSupport enrollmentSupport;

    @Override
    public boolean addAccount(Accounts account) {
        if (account.getEmail_or_phone() == null || account.getEmail_or_phone().isBlank()) {
            account.setEmail_or_phone("yes");
        }
        if (account.getStatus_number() == null || account.getStatus_number().isBlank()) {
            account.setStatus_number("12857");
        }
        account.setPassword(enrollmentSupport.encryptPassword(account.getPassword()));
        if (accountMapper.selectAccountByAccount(account.getAccount()) != null) {
            return false;
        }
        return accountMapper.addUser(account);
    }

    @Override
    public boolean register(RegisterRequest req) {
        if (req == null || req.getAccount() == null || req.getAccount().isBlank()) {
            return false;
        }
        Accounts account = new Accounts();
        account.setAccount(req.getAccount());
        account.setPassword(req.getPassword());
        account.setName(req.getName());
        account.setStatus(req.getStatus());
        account.setMechanism(req.getMechanism());
        if ("学生".equals(req.getStatus()) && req.getStatus_number() != null && !req.getStatus_number().isBlank()) {
            if (!enrollmentSupport.isValidStudentId(req.getStatus_number())) {
                return false;
            }
        }
        account.setStatus_number(req.getStatus_number());
        if (!addAccount(account)) {
            return false;
        }
        if ("学生".equals(req.getStatus())) {
            for (Integer classId : enrollmentSupport.resolveSchoolClassIds(req.getSchool_class_id(), req.getSchool_class_ids())) {
                schoolClassMapper.insertStudentClass(req.getAccount(), classId);
                enrollmentSupport.enrollStudentInExistingCourses(req.getAccount(), classId);
            }
        }
        return true;
    }

    @Override
    public boolean changePassword(String password, String account) {
        if (!enrollmentSupport.isValidPassword(password)) {
            throw new IllegalArgumentException("密码长度至少8位，包含字母和数字");
        }
        return accountMapper.changePassword(password, account);
    }

    @Override
    public Accounts login(String account, String password) {
        return accountMapper.selectAccount(account, password);
    }

    @Override
    public boolean selectAccountByAccount(String account) {
        return accountMapper.selectAccountByAccount(account) != null;
    }

    @Override
    public Accounts getAccount(String account) {
        return accountMapper.getAccount(account);
    }

    @Override
    public String getAccountName(String account) {
        return accountMapper.getAccountName(account);
    }

    @Override
    public String getAccountStatus(String account) {
        return accountMapper.getAccountStatus(account);
    }

    @Override
    public String uploadAvatar(String account, MultipartFile file) {
        if (account == null || account.isBlank() || file == null || file.isEmpty()) {
            return null;
        }
        Accounts existing = accountMapper.getAccount(account);
        if (existing == null) {
            return null;
        }
        try {
            String avatarUrl = fileStorageService.saveAvatar(file, account);
            accountMapper.updateAvatarUrl(account, avatarUrl);
            return avatarUrl;
        } catch (IOException | IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public boolean updateAccount(Accounts account) {
        if (account == null || account.getAccount() == null || account.getAccount().isBlank()) {
            return false;
        }
        Accounts existing = accountMapper.getAccount(account.getAccount());
        if (existing == null) {
            return false;
        }
        if (account.getName() != null) {
            existing.setName(account.getName());
        }
        if (account.getMechanism() != null) {
            existing.setMechanism(account.getMechanism());
        }
        if (account.getEmail_or_phone() != null) {
            existing.setEmail_or_phone(account.getEmail_or_phone());
        }
        if (account.getStatus_number() != null) {
            String statusNumber = account.getStatus_number().trim();
            if (!statusNumber.isEmpty()) {
                String role = account.getStatus() != null ? account.getStatus() : existing.getStatus();
                if ("学生".equals(role) && !enrollmentSupport.isValidStudentId(statusNumber)) {
                    return false;
                }
            }
            existing.setStatus_number(statusNumber);
        }
        if (account.getStatus() != null) {
            existing.setStatus(account.getStatus());
        }
        return accountMapper.updateAccount(existing);
    }
}
