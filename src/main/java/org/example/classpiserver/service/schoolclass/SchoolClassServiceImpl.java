package org.example.classpiserver.service.schoolclass;

import org.example.classpiserver.dto.schoolclass.ImportSchoolClassResult;
import org.example.classpiserver.dto.schoolclass.ImportStudentResult;
import org.example.classpiserver.dto.schoolclass.JoinStudentClassRequest;
import org.example.classpiserver.dto.account.RegisterRequest;
import org.example.classpiserver.dto.schoolclass.SchoolClassRequest;
import org.example.classpiserver.dto.schoolclass.UpdateStudentSchoolClassRequest;
import org.example.classpiserver.entity.Accounts;
import org.example.classpiserver.entity.SchoolClass;
import org.example.classpiserver.mapper.account.AccountMapper;
import org.example.classpiserver.mapper.schoolclass.SchoolClassMapper;
import org.example.classpiserver.service.account.AccountService;
import org.example.classpiserver.support.EnrollmentSupport;
import org.example.classpiserver.util.SchoolClassExcelUtil;
import org.example.classpiserver.util.StudentExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class SchoolClassServiceImpl implements SchoolClassService {

    @Autowired
    private SchoolClassMapper schoolClassMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private EnrollmentSupport enrollmentSupport;

    @Autowired
    private AccountService accountService;

    @Override
    public List<SchoolClass> listSchoolClasses() {
        return schoolClassMapper.listSchoolClasses();
    }

    @Override
    public SchoolClass createSchoolClass(SchoolClassRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            return null;
        }
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(request.getName());
        schoolClass.setMechanism(request.getMechanism());
        schoolClass.setTeacher_account(request.getTeacher_account());
        if (!schoolClassMapper.insertSchoolClass(schoolClass)) {
            return null;
        }
        return schoolClass;
    }

    @Override
    public boolean joinStudentClass(JoinStudentClassRequest request) {
        if (request == null || request.getAccount() == null || request.getSchool_class_id() == null) {
            return false;
        }
        Integer existing = schoolClassMapper.countStudentInClass(request.getAccount(), request.getSchool_class_id());
        if (existing != null && existing > 0) {
            return true;
        }
        if (!schoolClassMapper.insertStudentClass(request.getAccount(), request.getSchool_class_id())) {
            return false;
        }
        enrollmentSupport.enrollStudentInExistingCourses(request.getAccount(), request.getSchool_class_id());
        return true;
    }

    @Override
    public boolean updateStudentSchoolClasses(UpdateStudentSchoolClassRequest request) {
        if (request == null || request.getAccount() == null || request.getAccount().isBlank()) {
            return false;
        }
        List<Integer> classIds = enrollmentSupport.resolveSchoolClassIds(null, request.getSchool_class_ids());
        if (classIds.isEmpty()) {
            return false;
        }
        for (Integer classId : classIds) {
            if (schoolClassMapper.getSchoolClassById(classId) == null) {
                return false;
            }
        }
        schoolClassMapper.deleteStudentClassesByAccount(request.getAccount());
        for (Integer classId : classIds) {
            schoolClassMapper.insertStudentClass(request.getAccount(), classId);
            enrollmentSupport.enrollStudentInExistingCourses(request.getAccount(), classId);
        }
        return true;
    }

    @Override
    public List<SchoolClass> getStudentSchoolClasses(String account) {
        if (account == null || account.isBlank()) {
            return List.of();
        }
        return schoolClassMapper.getSchoolClassesByStudentAccount(account);
    }

    @Override
    public ImportStudentResult importSchoolClassStudents(MultipartFile file, Integer schoolClassId) {
        ImportStudentResult result = new ImportStudentResult();
        if (schoolClassId == null) {
            result.setFailed(1);
            result.addMessage("缺少班级 ID");
            return result;
        }
        SchoolClass schoolClass = schoolClassMapper.getSchoolClassById(schoolClassId);
        if (schoolClass == null) {
            result.setFailed(1);
            result.addMessage("班级不存在");
            return result;
        }

        List<StudentExcelUtil.StudentRow> rows;
        try {
            rows = StudentExcelUtil.parseImportFile(file);
        } catch (IOException e) {
            result.setFailed(1);
            result.addMessage("读取 Excel 失败：" + e.getMessage());
            return result;
        } catch (IllegalArgumentException e) {
            result.setFailed(1);
            result.addMessage(e.getMessage());
            return result;
        }

        String mechanism = schoolClass.getMechanism() == null ? "" : schoolClass.getMechanism();
        for (StudentExcelUtil.StudentRow row : rows) {
            try {
                importOneStudentRow(row, schoolClassId, mechanism, result);
            } catch (Exception e) {
                result.setFailed(result.getFailed() + 1);
                result.addMessage("第" + row.getRowNum() + "行：" + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public byte[] buildStudentImportTemplate() {
        try {
            return StudentExcelUtil.buildTemplateBytes();
        } catch (IOException e) {
            throw new IllegalStateException("生成模板失败", e);
        }
    }

    @Override
    public byte[] buildSchoolClassImportTemplate() {
        try {
            return SchoolClassExcelUtil.buildTemplateBytes();
        } catch (IOException e) {
            throw new IllegalStateException("生成班级模板失败", e);
        }
    }

    @Override
    public ImportSchoolClassResult importSchoolClasses(MultipartFile file, String teacherAccount, String defaultMechanism) {
        ImportSchoolClassResult result = new ImportSchoolClassResult();
        if (teacherAccount == null || teacherAccount.isBlank()) {
            result.setFailed(1);
            result.addMessage("缺少教师账号");
            return result;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(teacherAccount))) {
            result.setFailed(1);
            result.addMessage("仅教师可批量创建班级");
            return result;
        }
        String fallbackMechanism = defaultMechanism == null ? "" : defaultMechanism.trim();
        if (fallbackMechanism.isBlank()) {
            Accounts teacher = accountMapper.getAccount(teacherAccount);
            if (teacher != null && teacher.getMechanism() != null && !teacher.getMechanism().isBlank()) {
                fallbackMechanism = teacher.getMechanism().trim();
            }
        }
        List<SchoolClassExcelUtil.ClassRow> rows;
        try {
            rows = SchoolClassExcelUtil.parseImportFile(file);
        } catch (Exception ex) {
            result.setFailed(1);
            result.addMessage(ex.getMessage() == null ? "文件解析失败" : ex.getMessage());
            return result;
        }
        Set<String> seenInFile = new LinkedHashSet<>();
        for (SchoolClassExcelUtil.ClassRow row : rows) {
            importOneSchoolClassRow(row, teacherAccount, fallbackMechanism, seenInFile, result);
        }
        return result;
    }

    private void importOneSchoolClassRow(SchoolClassExcelUtil.ClassRow row, String teacherAccount,
                                         String fallbackMechanism, Set<String> seenInFile,
                                         ImportSchoolClassResult result) {
        String name = row.getName() == null ? "" : row.getName().trim();
        if (name.isBlank()) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：班级名称不能为空");
            return;
        }
        if (name.length() > 128) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：班级名称过长");
            return;
        }
        String mechanism = row.getMechanism() == null || row.getMechanism().isBlank()
                ? fallbackMechanism
                : row.getMechanism().trim();
        if (mechanism.isBlank()) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：缺少所属学校，请在 Excel 填写或选择默认学校");
            return;
        }
        String dedupeKey = normalizeClassKey(name, mechanism);
        if (seenInFile.contains(dedupeKey)) {
            result.setSkipped(result.getSkipped() + 1);
            result.addMessage("第" + row.getRowNum() + "行：与文件中其他行重复，已跳过");
            return;
        }
        seenInFile.add(dedupeKey);
        if (schoolClassExists(name, mechanism)) {
            result.setSkipped(result.getSkipped() + 1);
            result.addMessage("第" + row.getRowNum() + "行：班级已存在（" + name + "），已跳过");
            return;
        }
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(name);
        schoolClass.setMechanism(mechanism);
        schoolClass.setTeacher_account(teacherAccount);
        if (!schoolClassMapper.insertSchoolClass(schoolClass)) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：创建失败");
            return;
        }
        result.setCreated(result.getCreated() + 1);
        result.addCreatedId(schoolClass.getId());
    }

    private boolean schoolClassExists(String name, String mechanism) {
        String key = normalizeClassKey(name, mechanism);
        for (SchoolClass existing : schoolClassMapper.listSchoolClasses()) {
            if (normalizeClassKey(existing.getName(), existing.getMechanism()).equals(key)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeClassKey(String name, String mechanism) {
        String n = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        String m = mechanism == null ? "" : mechanism.trim().toLowerCase(Locale.ROOT);
        return n + "@@" + m;
    }

    private void importOneStudentRow(StudentExcelUtil.StudentRow row, Integer schoolClassId, String mechanism, ImportStudentResult result) {
        String accountName = row.getAccount().trim();
        if (accountName.length() < 4 || accountName.length() > 32) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：账号长度须为 4~32 位");
            return;
        }
        String password = row.resolvedPassword();
        if (password.length() < 8 || password.length() > 16) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：密码长度须为 8~16 位");
            return;
        }
        String statusNumber = row.getStatusNumber() == null ? "" : row.getStatusNumber().trim();
        if (!statusNumber.isEmpty() && !enrollmentSupport.isValidStudentId(statusNumber)) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：学号须为 6~20 位数字");
            return;
        }

        Accounts existing = accountMapper.getAccount(accountName);
        if (existing != null) {
            if (!"学生".equals(existing.getStatus())) {
                result.setFailed(result.getFailed() + 1);
                result.addMessage("第" + row.getRowNum() + "行：账号已存在且非学生身份");
                return;
            }
            Integer count = schoolClassMapper.countStudentInClass(accountName, schoolClassId);
            if (count != null && count > 0) {
                result.setSkipped(result.getSkipped() + 1);
                return;
            }
            schoolClassMapper.insertStudentClass(accountName, schoolClassId);
            enrollmentSupport.enrollStudentInExistingCourses(accountName, schoolClassId);
            result.setLinked(result.getLinked() + 1);
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAccount(accountName);
        registerRequest.setPassword(password);
        registerRequest.setName(row.getName() == null || row.getName().isBlank() ? accountName : row.getName().trim());
        registerRequest.setStatus("学生");
        registerRequest.setMechanism(mechanism);
        registerRequest.setStatus_number(statusNumber);
        registerRequest.setSchool_class_id(schoolClassId);
        if (!accountService.register(registerRequest)) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：创建学生失败");
            return;
        }
        result.setCreated(result.getCreated() + 1);
    }
}
