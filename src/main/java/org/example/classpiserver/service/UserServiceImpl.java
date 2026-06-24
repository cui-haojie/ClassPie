package org.example.classpiserver.service;

import org.example.classpiserver.dto.*;
import org.example.classpiserver.entity.*;
import org.example.classpiserver.mapper.UserMapper;
import org.example.classpiserver.util.StudentExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean addAccount(Accounts account) {
        if (account.getEmail_or_phone() == null || account.getEmail_or_phone().isBlank()) {
            account.setEmail_or_phone("yes");
        }
        if (account.getStatus_number() == null || account.getStatus_number().isBlank()) {
            account.setStatus_number("12857");
        }
         account.setPassword(encryptPassword(account.getPassword()));
         if(userMapper.selectAccountByAccount(account.getAccount()) != null) {
             return false;
         }
        return userMapper.addUser(account);
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
            if (!isValidStudentId(req.getStatus_number())) {
                return false;
            }
        }
        account.setStatus_number(req.getStatus_number());
        if (!addAccount(account)) {
            return false;
        }
        if ("学生".equals(req.getStatus())) {
            for (Integer classId : resolveSchoolClassIds(req.getSchool_class_id(), req.getSchool_class_ids())) {
                userMapper.insertStudentClass(req.getAccount(), classId);
                enrollStudentInExistingCourses(req.getAccount(), classId);
            }
        }
        return true;
    }

    @Override
    public boolean changePassword(String password, String account) {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("密码长度至少8位，包含字母和数字");
        }
        return userMapper.changePassword(password, account);
    }

    @Override
    public Accounts login(String account,String password) {
        return userMapper.selectAccount(account, password);
    }

    @Override
    public boolean selectAccountByAccount(String account) {
        return userMapper.selectAccountByAccount(account) != null;
    }

    @Override
    public List<Long> getCourseIdByAccount(String account) {
        return userMapper.getCourseIdByAccount(account);
    }

    @Override
    public List<Long> getArchivedCourseIdByAccount(String account) {
        List<Long> allCourseIds = userMapper.getCourseIdByAccount(account);
        List<Long> archivedCourseIds = new ArrayList<>();
        for (Long courseId : allCourseIds) {
            Course course = userMapper.getCourseByCourseId(courseId);
            if (course != null && Boolean.TRUE.equals(course.getIs_archived())) {
                archivedCourseIds.add(courseId);
            }
        }
        return archivedCourseIds;
    }

    @Override
    public Accounts getAccount(String account) {
       return userMapper.getAccount(account);
    }

    @Override
    public Course getCourseByCode(String account, String code) {
        if (userMapper.getCourseByCode(code) != null){
            userMapper.createCourse(account,userMapper.getCourseByCode(code).getId());
        }
        return userMapper.getCourseByCode(code);
    }
    @Override
    public void addCourse(String account,Long class_id) {
        userMapper.createCourse(account,class_id);
    }

    @Override
    public List<Course> getCourseByCourseId(List<Long> courseId) {
        List<Course> courseList = new ArrayList<>();
        for (Long id : courseId) {
            Course course = userMapper.getCourseByCourseId(id);
            if (course != null) {
                courseList.add(course);
            }
        }
        return courseList;
    }

    @Override
    public List<Course> getCourseByCourseIdWithPinStatus(String account, List<Long> courseId) {
        List<Course> courseList = new ArrayList<>();
        for (Long id : courseId) {
            Course course = userMapper.getCourseByCourseId(id);
            if (course != null) {
                Integer pinnedStatus = userMapper.getCoursePinStatus(account, id);
                course.setIs_pinned(pinnedStatus != null && pinnedStatus == 1);
                courseList.add(course);
            }
        }
        return courseList;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    private boolean isValidStudentId(String statusNumber) {
        return statusNumber != null && statusNumber.matches("\\d{6,20}");
    }

    private String resolveDefaultSemester() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        if (month >= 9) {
            return year + "-" + (year + 1) + " 第一学期";
        }
        if (month >= 3) {
            return (year - 1) + "-" + year + " 第二学期";
        }
        return (year - 1) + "-" + year + " 第一学期";
    }

    private String encryptPassword(String password) {
        return password;
    }

    @Override
    public boolean togglePinCourse(String account, Long courseId, Boolean isPinned) {
        Course course = userMapper.getCourseByCourseId(courseId);
        if (course == null) return false;
        return userMapper.updateCoursePin(isPinned, account, courseId);
    }

    @Override
    public String getAccountName(String account){
        return userMapper.getAccountName(account);
    }

    @Override
    public Course addCourse(CourseRequest course) {
        if (course == null || course.getTeacher_account() == null) {
            return null;
        }
        List<Integer> schoolClassIds = resolveSchoolClassIds(course.getSchool_class_id(), course.getSchool_class_ids());
        if (!schoolClassIds.isEmpty()) {
            List<String> classNames = new ArrayList<>();
            for (Integer classId : schoolClassIds) {
                SchoolClass schoolClass = userMapper.getSchoolClassById(classId);
                if (schoolClass != null && schoolClass.getName() != null) {
                    classNames.add(schoolClass.getName());
                }
            }
            if ((course.getSelected_classes() == null || course.getSelected_classes().isBlank()) && !classNames.isEmpty()) {
                course.setSelected_classes(String.join("、", classNames));
            }
            course.setSchool_class_id(schoolClassIds.get(0));
        } else {
            course.setSchool_class_id(null);
        }
        if (course.getSemester() == null || course.getSemester().isBlank()) {
            course.setSemester(resolveDefaultSemester());
        }
        course.setCode(generateUniqueCourseCode());
        if (!userMapper.addCourse(course)) {
            return null;
        }
        Long courseId = userMapper.getLastInsertCourseId();
        if (courseId == null) {
            return null;
        }
        enrollAccountIfAbsent(course.getTeacher_account(), courseId);
        Set<String> enrolledStudents = new LinkedHashSet<>();
        for (Integer classId : schoolClassIds) {
            userMapper.insertCourseSchoolClass(courseId, classId);
            for (String studentAccount : userMapper.getStudentAccountsBySchoolClass(classId)) {
                if (enrolledStudents.add(studentAccount)) {
                    enrollAccountIfAbsent(studentAccount, courseId);
                }
            }
        }
        return userMapper.getCourseByCourseId(courseId);
    }

    @Override
    public List<SchoolClass> listSchoolClasses() {
        return userMapper.listSchoolClasses();
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
        if (!userMapper.insertSchoolClass(schoolClass)) {
            return null;
        }
        return schoolClass;
    }

    @Override
    public boolean joinStudentClass(JoinStudentClassRequest request) {
        if (request == null || request.getAccount() == null || request.getSchool_class_id() == null) {
            return false;
        }
        Integer existing = userMapper.countStudentInClass(request.getAccount(), request.getSchool_class_id());
        if (existing != null && existing > 0) {
            return true;
        }
        if (!userMapper.insertStudentClass(request.getAccount(), request.getSchool_class_id())) {
            return false;
        }
        enrollStudentInExistingCourses(request.getAccount(), request.getSchool_class_id());
        return true;
    }

    @Override
    public boolean updateStudentSchoolClasses(UpdateStudentSchoolClassRequest request) {
        if (request == null || request.getAccount() == null || request.getAccount().isBlank()) {
            return false;
        }
        List<Integer> classIds = resolveSchoolClassIds(null, request.getSchool_class_ids());
        if (classIds.isEmpty()) {
            return false;
        }
        for (Integer classId : classIds) {
            if (userMapper.getSchoolClassById(classId) == null) {
                return false;
            }
        }
        userMapper.deleteStudentClassesByAccount(request.getAccount());
        for (Integer classId : classIds) {
            userMapper.insertStudentClass(request.getAccount(), classId);
            enrollStudentInExistingCourses(request.getAccount(), classId);
        }
        return true;
    }

    @Override
    public List<SchoolClass> getStudentSchoolClasses(String account) {
        if (account == null || account.isBlank()) {
            return List.of();
        }
        return userMapper.getSchoolClassesByStudentAccount(account);
    }

    @Override
    public ImportStudentResult importSchoolClassStudents(MultipartFile file, Integer schoolClassId) {
        ImportStudentResult result = new ImportStudentResult();
        if (schoolClassId == null) {
            result.setFailed(1);
            result.addMessage("缺少班级 ID");
            return result;
        }
        SchoolClass schoolClass = userMapper.getSchoolClassById(schoolClassId);
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
        if (!statusNumber.isEmpty() && !isValidStudentId(statusNumber)) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：学号须为 6~20 位数字");
            return;
        }

        Accounts existing = userMapper.getAccount(accountName);
        if (existing != null) {
            if (!"学生".equals(existing.getStatus())) {
                result.setFailed(result.getFailed() + 1);
                result.addMessage("第" + row.getRowNum() + "行：账号已存在且非学生身份");
                return;
            }
            Integer count = userMapper.countStudentInClass(accountName, schoolClassId);
            if (count != null && count > 0) {
                result.setSkipped(result.getSkipped() + 1);
                return;
            }
            userMapper.insertStudentClass(accountName, schoolClassId);
            enrollStudentInExistingCourses(accountName, schoolClassId);
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
        if (!register(registerRequest)) {
            result.setFailed(result.getFailed() + 1);
            result.addMessage("第" + row.getRowNum() + "行：创建学生失败");
            return;
        }
        result.setCreated(result.getCreated() + 1);
    }

    private List<Integer> resolveSchoolClassIds(Integer singleId, List<Integer> multipleIds) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (multipleIds != null) {
            for (Integer id : multipleIds) {
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        if (singleId != null) {
            ids.add(singleId);
        }
        return new ArrayList<>(ids);
    }

    private void enrollStudentInExistingCourses(String account, Integer schoolClassId) {
        Set<Long> courseIds = new LinkedHashSet<>();
        List<Long> legacyIds = userMapper.getCourseIdsBySchoolClassLegacy(schoolClassId);
        if (legacyIds != null) {
            courseIds.addAll(legacyIds);
        }
        List<Long> linkedIds = userMapper.getCourseIdsBySchoolClassLink(schoolClassId);
        if (linkedIds != null) {
            courseIds.addAll(linkedIds);
        }
        for (Long courseId : courseIds) {
            enrollAccountIfAbsent(account, courseId);
        }
    }

    private void enrollAccountIfAbsent(String account, Long courseId) {
        Integer count = userMapper.countAccountInCourse(account, courseId);
        if (count == null || count == 0) {
            userMapper.createCourse(account, courseId);
        }
    }

    private String generateUniqueCourseCode() {
        for (int i = 0; i < 10; i++) {
            String code = generateCourseCode();
            if (userMapper.getCourseByCode(code) == null) {
                return code;
            }
        }
        return generateCourseCode() + (System.currentTimeMillis() % 1000);
    }

    private String generateCourseCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public boolean addTeacherCourse(String account){
        try {
            for (Long id : userMapper.getCourseIdByTeacherAccount(account)) {
                userMapper.createCourse(account, id);
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public Course getCourseById(Long courseId) {
        return userMapper.getCourseByCourseId(courseId);
    }

    @Override
    public Integer getCountByCourseId(Long id){
        return userMapper.getCountByCourseId(id);
    }

    @Override
    public boolean leaveCourse(String account, Long courseId) {
        return userMapper.leaveCourse(courseId, account);
    }

    @Override
    public boolean updateCourseInfo(CourseUpdateRequest request) {
        if (request == null || request.getId() == null) {
            return false;
        }
        return userMapper.updateCourseInfo(request);
    }

    @Override
    public boolean archiveCourse(ArchiveCourseRequest request) {
        if (request == null || request.getClass_id() == null) {
            return false;
        }
        return userMapper.setCourseArchived(
                request.getClass_id(),
                request.isArchived() ? 1 : 0
        );
    }

    @Override
    public List<CourseMember> getCourseMembers(Long classId) {
        return userMapper.getCourseMembers(classId);
    }

    @Override
    public boolean addHomework(Homework homework, Integer class_id) {
        try {
            userMapper.addHomework(homework);
            Integer homeworkId = userMapper.getLastInsertId();
            userMapper.setHomeworkContentId(homeworkId, homeworkId);
            userMapper.addCourses_homework(class_id, homeworkId);
            notifyHomeworkPublished(class_id, homeworkId, homework.getName());
            return true;
        } catch (Exception e) {
            System.err.println(userMapper.getLastInsertId());
            System.err.println("添加作业失败");
            System.err.println(e);
            return false;
        }
    }

    @Override
    public Integer getCountByClassId(Integer class_id){
        System.err.println(class_id);
        System.err.println(userMapper.getCountByClassId(class_id));
        return userMapper.getCountByClassId(class_id);
    }

    @Override
    public List<Homework> getHomeworkByClassId(Integer class_id) {
        List<Homework> homeworkList = new ArrayList<>();
        for (Integer homework_id : userMapper.getCourseIdByClassId(class_id)){
            homeworkList.add(userMapper.getHomework(homework_id));
        }
        return homeworkList;
    }

    @Override
    public Homework getHomeworkById(Integer homework_id) {
        return userMapper.getHomework(homework_id);
    }

    @Override
    public String getAccountStatus(String account) {
        return userMapper.getAccountStatus(account);
    }

    @Override
    public List<Content> getContentById(Long content_id) {
        return userMapper.getContentByContentId(content_id);
    }

    @Override
    public boolean setContentScore(int newScore,Long content_id,String account) {
        return userMapper.setContentScore(newScore,content_id,account);
    }

    @Override
    public boolean addContent(Content content) {
        return userMapper.addContent(content);
    }

    @Override
    public boolean updateAccount(Accounts account) {
        if (account == null || account.getAccount() == null || account.getAccount().isBlank()) {
            return false;
        }
        Accounts existing = userMapper.getAccount(account.getAccount());
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
                if ("学生".equals(role) && !isValidStudentId(statusNumber)) {
                    return false;
                }
            }
            existing.setStatus_number(statusNumber);
        }
        if (account.getStatus() != null) {
            existing.setStatus(account.getStatus());
        }
        return userMapper.updateAccount(existing);
    }

    @Override
    public List<Notification> getNotifications(String account) {
        return userMapper.getNotifications(account);
    }

    @Override
    public Integer getUnreadNotificationCount(String account) {
        return userMapper.getUnreadNotificationCount(account);
    }

    @Override
    public boolean markNotificationRead(Integer id, String account) {
        return userMapper.markNotificationRead(id, account);
    }

    @Override
    public boolean remindHomework(RemindHomeworkRequest request) {
        if (request == null || request.getHomework_id() == null || request.getClass_id() == null) {
            return false;
        }
        Homework homework = userMapper.getHomework(request.getHomework_id());
        if (homework == null) {
            return false;
        }
        Long contentId = homework.getContent_id() > 0 ? (long) homework.getContent_id() : request.getHomework_id().longValue();
        List<String> accounts = userMapper.getUnsubmittedAccounts(request.getClass_id(), contentId);
        if (accounts.isEmpty()) {
            return false;
        }
        for (String studentAccount : accounts) {
            Notification notification = new Notification();
            notification.setAccount(studentAccount);
            notification.setClass_id(request.getClass_id());
            notification.setHomework_id(request.getHomework_id());
            notification.setType("remind");
            notification.setMessage("老师催交作业：" + homework.getName());
            userMapper.addNotification(notification);
        }
        return true;
    }

    private void notifyHomeworkPublished(Integer classId, Integer homeworkId, String homeworkName) {
        List<CourseMember> members = userMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setHomework_id(homeworkId);
            notification.setType("homework");
            notification.setMessage("新作业发布：" + homeworkName);
            userMapper.addNotification(notification);
        }
    }
}
