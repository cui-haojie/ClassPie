package org.example.classpiserver.service;

import org.example.classpiserver.dto.*;
import org.example.classpiserver.entity.*;
import org.example.classpiserver.mapper.UserMapper;
import org.example.classpiserver.util.FileStorageService;
import org.example.classpiserver.util.HomeworkDeadlineUtil;
import org.example.classpiserver.util.StudentExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileStorageService fileStorageService;

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
        return userMapper.getArchivedCourseIdByAccount(account);
    }

    @Override
    public Accounts getAccount(String account) {
       return userMapper.getAccount(account);
    }

    @Override
    public Course getCourseByCode(String account, String code) {
        Course course = userMapper.getCourseByCode(code);
        if (course != null) {
            enrollAccountIfAbsent(account, course.getId());
        }
        return course;
    }
    @Override
    public void addCourse(String account,Long class_id) {
        enrollAccountIfAbsent(account, class_id);
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
            Integer maxOrder = userMapper.getMaxSortOrder(account);
            int nextOrder = (maxOrder == null ? -1 : maxOrder) + 1;
            userMapper.createCourseWithOrder(account, courseId, nextOrder);
        }
    }

    @Override
    public boolean updateCourseOrder(CourseOrderRequest request) {
        if (request == null || request.getAccount() == null || request.getAccount().isBlank()) {
            return false;
        }
        List<Long> courseIds = request.getCourse_ids();
        if (courseIds == null || courseIds.isEmpty()) {
            return false;
        }
        for (int i = 0; i < courseIds.size(); i++) {
            Long courseId = courseIds.get(i);
            if (courseId == null) {
                continue;
            }
            Integer linked = userMapper.countAccountInCourse(request.getAccount(), courseId);
            if (linked == null || linked == 0) {
                continue;
            }
            userMapper.updateCourseSortOrder(request.getAccount(), courseId, i);
        }
        return true;
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
        if (request == null || request.getClass_id() == null || request.getAccount() == null || request.getAccount().isBlank()) {
            return false;
        }
        Course course = userMapper.getCourseByCourseId(request.getClass_id());
        if (course == null || !request.getAccount().equals(course.getTeacher_account())) {
            return false;
        }
        return userMapper.setCourseArchivedForClass(
                request.getClass_id(),
                request.isArchived() ? 1 : 0
        );
    }

    @Override
    public List<CourseMember> getCourseMembers(Long classId) {
        return userMapper.getCourseMembers(classId);
    }

    @Override
    public boolean addHomework(Homework homework, Integer class_id, MultipartFile attachment) {
        try {
            if (homework.getDeadline() != null) {
                homework.setDeadline(HomeworkDeadlineUtil.normalizeInput(homework.getDeadline()));
            }
            if (attachment != null && !attachment.isEmpty()) {
                FileStorageService.StoredFile stored = fileStorageService.saveHomeworkAttachment(attachment);
                homework.setAttachment_url(stored.url());
                homework.setAttachment_name(stored.originalName());
            }
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
            Homework homework = userMapper.getHomework(homework_id);
            normalizeHomeworkDeadline(homework);
            fillHomeworkSubmissionStats(homework, class_id.longValue());
            homeworkList.add(homework);
        }
        return homeworkList;
    }

    private void fillHomeworkSubmissionStats(Homework homework, Long classId) {
        if (homework == null) {
            return;
        }
        long contentId = homework.getContent_id() > 0 ? homework.getContent_id() : homework.getHomework_id();
        Integer graded = userMapper.countGradedSubmissions(contentId);
        Integer ungraded = userMapper.countUngradedSubmissions(contentId);
        Integer unsubmitted = userMapper.countUnsubmittedStudents(classId, contentId);
        homework.setGraded_count(graded == null ? 0 : graded);
        homework.setUngraded_count(ungraded == null ? 0 : ungraded);
        homework.setUnsubmitted_count(unsubmitted == null ? 0 : unsubmitted);
    }

    @Override
    public Homework getHomeworkById(Integer homework_id) {
        Homework homework = userMapper.getHomework(homework_id);
        normalizeHomeworkDeadline(homework);
        return homework;
    }

    private void normalizeHomeworkDeadline(Homework homework) {
        if (homework != null && homework.getDeadline() != null) {
            homework.setDeadline(HomeworkDeadlineUtil.formatDisplay(homework.getDeadline()));
        }
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
    public boolean submitHomework(Long contentId, String account, String details, MultipartFile file) {
        if (contentId == null || account == null || account.isBlank()) {
            return false;
        }
        Integer existing = userMapper.countContentSubmission(contentId, account);
        if (existing != null && existing > 0) {
            return false;
        }
        Homework homework = userMapper.getHomeworkByContentOrId(contentId);
        if (homework != null && HomeworkDeadlineUtil.isDeadlinePassed(homework.getDeadline())) {
            return false;
        }
        String text = details == null ? "" : details.trim();
        String attachmentUrl = null;
        String attachmentName = null;
        if (file != null && !file.isEmpty()) {
            try {
                FileStorageService.StoredFile stored = fileStorageService.saveHomeworkAttachment(file);
                attachmentUrl = stored.url();
                attachmentName = stored.originalName();
            } catch (IOException | IllegalArgumentException ex) {
                return false;
            }
        }
        if (text.isEmpty() && attachmentUrl == null) {
            return false;
        }
        Content content = new Content();
        content.setContent_id(contentId);
        content.setAccount(account);
        content.setScore(0);
        content.setDetails(text.isEmpty() ? "（附件提交）" : text);
        content.setAttachment_url(attachmentUrl);
        content.setAttachment_name(attachmentName);
        content.setIs_graded(false);
        return userMapper.addContent(content);
    }

    @Override
    public String uploadAvatar(String account, MultipartFile file) {
        if (account == null || account.isBlank() || file == null || file.isEmpty()) {
            return null;
        }
        Accounts existing = userMapper.getAccount(account);
        if (existing == null) {
            return null;
        }
        try {
            String avatarUrl = fileStorageService.saveAvatar(file, account);
            userMapper.updateAvatarUrl(account, avatarUrl);
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
        if (id == null || account == null || account.isBlank()) {
            return false;
        }
        return userMapper.markNotificationRead(id, account);
    }

    @Override
    public boolean markAllNotificationsRead(String account) {
        if (account == null || account.isBlank()) {
            return false;
        }
        return userMapper.markAllNotificationsRead(account);
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

    private static final Set<String> COURSE_ACTIVITY_TYPES = Set.of(
            "interaction", "topic", "material", "test", "announcement"
    );

    @Override
    public List<CourseActivity> getCourseActivities(Integer classId, String type, String account) {
        if (classId == null || type == null || !COURSE_ACTIVITY_TYPES.contains(type)) {
            return List.of();
        }
        List<CourseActivity> activities;
        if ("test".equals(type)) {
            boolean isTeacher = "老师".equals(userMapper.getAccountStatus(account));
            activities = isTeacher
                    ? userMapper.getCourseTestsByClassId(classId)
                    : userMapper.getPublishedCourseTestsByClassId(classId);
        } else {
            activities = userMapper.getCourseActivitiesByType(classId, type);
        }
        for (CourseActivity activity : activities) {
            if (activity.getDeadline() != null) {
                activity.setDeadline(HomeworkDeadlineUtil.formatDisplay(activity.getDeadline()));
            }
            if (activity.getStart_time() != null) {
                activity.setStart_time(HomeworkDeadlineUtil.formatDisplay(activity.getStart_time()));
            }
            if (activity.getCreate_time() != null) {
                activity.setCreate_time(HomeworkDeadlineUtil.formatDisplay(activity.getCreate_time()));
            }
        }
        return activities;
    }

    @Override
    public Integer countCourseActivities(Integer classId, String type) {
        if (classId == null || type == null || !COURSE_ACTIVITY_TYPES.contains(type)) {
            return 0;
        }
        Integer count = userMapper.countCourseActivitiesByType(classId, type);
        return count == null ? 0 : count;
    }

    @Override
    public boolean addCourseActivity(CourseActivity activity, Integer classId, MultipartFile attachment) {
        if (activity == null || classId == null || activity.getTitle() == null || activity.getTitle().isBlank()) {
            return false;
        }
        if (activity.getType() == null || !COURSE_ACTIVITY_TYPES.contains(activity.getType())) {
            return false;
        }
        if (activity.getCreator_account() == null || activity.getCreator_account().isBlank()) {
            return false;
        }
        if ("test".equals(activity.getType())) {
            return false;
        }
        activity.setClass_id(classId.longValue());
        activity.setPublish_status("published");
        if (activity.getDeadline() != null && !activity.getDeadline().isBlank()) {
            activity.setDeadline(HomeworkDeadlineUtil.normalizeInput(activity.getDeadline()));
        } else {
            activity.setDeadline(null);
            activity.setStart_time(null);
        }
        if (attachment != null && !attachment.isEmpty()) {
            try {
                FileStorageService.StoredFile stored = fileStorageService.saveHomeworkAttachment(attachment);
                activity.setAttachment_url(stored.url());
                activity.setAttachment_name(stored.originalName());
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (IOException ex) {
                System.err.println("资料附件上传失败: " + ex.getMessage());
                return false;
            }
        }
        boolean ok = userMapper.addCourseActivity(activity);
        if (ok && "announcement".equals(activity.getType())) {
            notifyAnnouncementPublished(classId, activity.getTitle());
        }
        return ok;
    }

    @Override
    public SaveTestDraftResult saveCourseTestDraft(AddCourseTestRequest request) {
        if (request == null || request.getClass_id() == null || request.getTitle() == null || request.getTitle().isBlank()) {
            return new SaveTestDraftResult(null, false);
        }
        if (request.getCreator_account() == null || request.getCreator_account().isBlank()) {
            return new SaveTestDraftResult(null, false);
        }
        String startTime = normalizeOptionalTime(request.getStart_time());
        String endTime = normalizeOptionalTime(request.getDeadline());
        if (startTime != null && endTime != null) {
            var start = HomeworkDeadlineUtil.parseDeadlineEnd(startTime);
            var end = HomeworkDeadlineUtil.parseDeadlineEnd(endTime);
            if (start == null || end == null || !start.isBefore(end)) {
                return new SaveTestDraftResult(null, false);
            }
        }
        Long activityId = request.getActivity_id();
        if (activityId != null) {
            CourseActivity existing = userMapper.getCourseActivityById(activityId);
            if (existing == null || !"test".equals(existing.getType()) || !"draft".equals(existing.getPublish_status())) {
                return new SaveTestDraftResult(null, false);
            }
            if (!request.getCreator_account().equals(existing.getCreator_account())) {
                return new SaveTestDraftResult(null, false);
            }
            if (!userMapper.updateCourseTest(activityId, request.getTitle().trim(),
                    trimOrNull(request.getContent()), startTime, endTime, "draft")) {
                return new SaveTestDraftResult(null, false);
            }
        } else {
            CourseActivity activity = new CourseActivity();
            activity.setClass_id(request.getClass_id().longValue());
            activity.setType("test");
            activity.setTitle(request.getTitle().trim());
            activity.setContent(trimOrNull(request.getContent()));
            activity.setStart_time(startTime);
            activity.setDeadline(endTime);
            activity.setCreator_account(request.getCreator_account());
            activity.setPublish_status("draft");
            if (!userMapper.addCourseActivity(activity)) {
                return new SaveTestDraftResult(null, false);
            }
            activityId = activity.getId();
            if (activityId == null) {
                return new SaveTestDraftResult(null, false);
            }
        }
        replaceTestQuestions(activityId, request.getQuestions(), false);
        return new SaveTestDraftResult(activityId, true);
    }

    @Override
    public boolean addCourseTest(AddCourseTestRequest request) {
        if (request == null || request.getClass_id() == null || request.getTitle() == null || request.getTitle().isBlank()) {
            return false;
        }
        if (request.getCreator_account() == null || request.getCreator_account().isBlank()) {
            return false;
        }
        if (request.getStart_time() == null || request.getStart_time().isBlank()
                || request.getDeadline() == null || request.getDeadline().isBlank()) {
            return false;
        }
        List<TestQuestionInput> inputs = request.getQuestions();
        if (inputs == null || inputs.isEmpty()) {
            return false;
        }
        String startTime = HomeworkDeadlineUtil.normalizeInput(request.getStart_time());
        String endTime = HomeworkDeadlineUtil.normalizeInput(request.getDeadline());
        var start = HomeworkDeadlineUtil.parseDeadlineEnd(startTime);
        var end = HomeworkDeadlineUtil.parseDeadlineEnd(endTime);
        if (start == null || end == null || !start.isBefore(end)) {
            return false;
        }
        Long activityId = request.getActivity_id();
        if (activityId != null) {
            CourseActivity existing = userMapper.getCourseActivityById(activityId);
            if (existing == null || !"test".equals(existing.getType()) || !"draft".equals(existing.getPublish_status())) {
                return false;
            }
            if (!request.getCreator_account().equals(existing.getCreator_account())) {
                return false;
            }
            if (!userMapper.updateCourseTest(activityId, request.getTitle().trim(),
                    trimOrNull(request.getContent()), startTime, endTime, "published")) {
                return false;
            }
        } else {
            CourseActivity activity = new CourseActivity();
            activity.setClass_id(request.getClass_id().longValue());
            activity.setType("test");
            activity.setTitle(request.getTitle().trim());
            activity.setContent(trimOrNull(request.getContent()));
            activity.setStart_time(startTime);
            activity.setDeadline(endTime);
            activity.setCreator_account(request.getCreator_account());
            activity.setPublish_status("published");
            if (!userMapper.addCourseActivity(activity)) {
                return false;
            }
            activityId = activity.getId();
            if (activityId == null) {
                return false;
            }
        }
        try {
            replaceTestQuestions(activityId, inputs, true);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        notifyTestPublished(request.getClass_id(), request.getTitle().trim());
        return true;
    }

    private static String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeOptionalTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return HomeworkDeadlineUtil.normalizeInput(value);
    }

    private void replaceTestQuestions(Long activityId, List<TestQuestionInput> inputs, boolean strict) {
        userMapper.deleteTestQuestionsByActivityId(activityId);
        if (inputs == null || inputs.isEmpty()) {
            if (strict) {
                throw new IllegalArgumentException("题目不能为空");
            }
            return;
        }
        int order = 0;
        for (TestQuestionInput input : inputs) {
            if (input == null || input.getQuestion_type() == null) {
                if (strict) {
                    throw new IllegalArgumentException("题目信息不完整");
                }
                continue;
            }
            String stem = input.getStem() == null ? "" : input.getStem().trim();
            if (stem.isEmpty()) {
                if (strict) {
                    throw new IllegalArgumentException("题目信息不完整");
                }
                continue;
            }
            String qType = input.getQuestion_type().trim();
            if (!"choice".equals(qType) && !"short".equals(qType)) {
                if (strict) {
                    throw new IllegalArgumentException("题目类型无效");
                }
                continue;
            }
            TestQuestion question = new TestQuestion();
            question.setActivity_id(activityId);
            question.setQuestion_type(qType);
            question.setStem(stem);
            question.setSort_order(order++);
            int score = input.getScore() == null || input.getScore() <= 0 ? 5 : input.getScore();
            question.setScore(score);
            if ("choice".equals(qType)) {
                if (isBlank(input.getOption_a()) || isBlank(input.getOption_b())
                        || isBlank(input.getOption_c()) || isBlank(input.getOption_d())) {
                    if (strict) {
                        throw new IllegalArgumentException("选择题选项不完整");
                    }
                    continue;
                }
                String correct = input.getCorrect_option() == null ? "" : input.getCorrect_option().trim().toUpperCase();
                if (!Set.of("A", "B", "C", "D").contains(correct)) {
                    if (strict) {
                        throw new IllegalArgumentException("请选择正确答案");
                    }
                    continue;
                }
                question.setOption_a(input.getOption_a().trim());
                question.setOption_b(input.getOption_b().trim());
                question.setOption_c(input.getOption_c().trim());
                question.setOption_d(input.getOption_d().trim());
                question.setCorrect_option(correct);
            }
            if (!userMapper.addTestQuestion(question)) {
                throw new IllegalArgumentException("保存题目失败");
            }
        }
        if (strict && order == 0) {
            throw new IllegalArgumentException("题目不能为空");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public TestDetailDTO getTestDetail(Long activityId, String account) {
        if (activityId == null) {
            return null;
        }
        CourseActivity activity = userMapper.getCourseActivityById(activityId);
        if (activity == null || !"test".equals(activity.getType())) {
            return null;
        }
        String status = userMapper.getAccountStatus(account);
        boolean isTeacher = "老师".equals(status);
        if ("draft".equals(activity.getPublish_status()) && !isTeacher) {
            return null;
        }
        normalizeActivityTimes(activity);
        List<TestQuestion> questions = userMapper.getTestQuestionsByActivityId(activityId);
        int choiceCount = 0;
        int shortCount = 0;
        for (TestQuestion q : questions) {
            if ("choice".equals(q.getQuestion_type())) {
                choiceCount++;
            } else if ("short".equals(q.getQuestion_type())) {
                shortCount++;
            }
        }
        if (!isTeacher) {
            for (TestQuestion q : questions) {
                q.setCorrect_option(null);
            }
        }
        TestDetailDTO dto = new TestDetailDTO();
        dto.setActivity(activity);
        dto.setQuestions(questions);
        dto.setChoice_count(choiceCount);
        dto.setShort_count(shortCount);
        dto.setIs_teacher(isTeacher);
        dto.setSubmitted(false);
        dto.setMy_answers(Map.of());
        if (account != null && !account.isBlank()) {
            TestSubmission submission = userMapper.getTestSubmissionByAccount(activityId, account);
            if (submission != null) {
                dto.setSubmitted(true);
                if (submission.getSubmit_time() != null) {
                    submission.setSubmit_time(HomeworkDeadlineUtil.formatDisplay(submission.getSubmit_time()));
                }
                Map<Long, String> answers = new HashMap<>();
                List<Map<String, Object>> rows = userMapper.getTestAnswersBySubmissionId(submission.getId());
                for (Map<String, Object> row : rows) {
                    Object qid = row.get("question_id");
                    Object ans = row.get("answer");
                    if (qid != null) {
                        answers.put(((Number) qid).longValue(), ans == null ? "" : String.valueOf(ans));
                    }
                }
                dto.setMy_answers(answers);
            }
        }
        return dto;
    }

    @Override
    public boolean submitTest(SubmitTestRequest request) {
        if (request == null || request.getActivity_id() == null || request.getAccount() == null
                || request.getAccount().isBlank()) {
            return false;
        }
        CourseActivity activity = userMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || !"test".equals(activity.getType())) {
            return false;
        }
        if (!"published".equals(activity.getPublish_status())) {
            return false;
        }
        if (!HomeworkDeadlineUtil.isWithinWindow(activity.getStart_time(), activity.getDeadline())) {
            return false;
        }
        TestSubmission existing = userMapper.getTestSubmissionByAccount(request.getActivity_id(), request.getAccount());
        if (existing != null) {
            return false;
        }
        List<TestQuestion> questions = userMapper.getTestQuestionsByActivityId(request.getActivity_id());
        if (questions.isEmpty()) {
            return false;
        }
        Map<Long, String> answerMap = new HashMap<>();
        if (request.getAnswers() != null) {
            for (TestAnswerInput input : request.getAnswers()) {
                if (input != null && input.getQuestion_id() != null) {
                    answerMap.put(input.getQuestion_id(), input.getAnswer() == null ? "" : input.getAnswer().trim());
                }
            }
        }
        for (TestQuestion question : questions) {
            String answer = answerMap.getOrDefault(question.getId(), "");
            if (answer.isBlank()) {
                return false;
            }
        }
        TestSubmission submission = new TestSubmission();
        submission.setActivity_id(request.getActivity_id());
        submission.setAccount(request.getAccount());
        if (!userMapper.addTestSubmission(submission)) {
            return false;
        }
        for (TestQuestion question : questions) {
            String answer = answerMap.getOrDefault(question.getId(), "");
            if (!userMapper.addTestAnswer(submission.getId(), question.getId(), answer)) {
                return false;
            }
        }
        return true;
    }

    private void normalizeActivityTimes(CourseActivity activity) {
        if (activity == null) {
            return;
        }
        if (activity.getDeadline() != null) {
            activity.setDeadline(HomeworkDeadlineUtil.formatDisplay(activity.getDeadline()));
        }
        if (activity.getStart_time() != null) {
            activity.setStart_time(HomeworkDeadlineUtil.formatDisplay(activity.getStart_time()));
        }
        if (activity.getCreate_time() != null) {
            activity.setCreate_time(HomeworkDeadlineUtil.formatDisplay(activity.getCreate_time()));
        }
    }

    @Override
    public CourseActivity getCourseActivityById(Long activityId) {
        if (activityId == null) {
            return null;
        }
        CourseActivity activity = userMapper.getCourseActivityById(activityId);
        normalizeActivityTimes(activity);
        return activity;
    }

    @Override
    public List<CourseActivityReply> getActivityReplies(Long activityId) {
        if (activityId == null) {
            return List.of();
        }
        List<CourseActivityReply> replies = userMapper.getActivityReplies(activityId);
        for (CourseActivityReply reply : replies) {
            if (reply.getCreate_time() != null) {
                reply.setCreate_time(HomeworkDeadlineUtil.formatDisplay(reply.getCreate_time()));
            }
        }
        return replies;
    }

    @Override
    public boolean addActivityReply(AddActivityReplyRequest request, MultipartFile image) {
        if (request == null || request.getActivity_id() == null || request.getAccount() == null
                || request.getAccount().isBlank()) {
            return false;
        }
        String text = request.getContent() == null ? "" : request.getContent().trim();
        boolean hasImage = image != null && !image.isEmpty();
        if (text.isEmpty() && !hasImage) {
            return false;
        }
        CourseActivity activity = userMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || "test".equals(activity.getType())) {
            return false;
        }
        CourseActivityReply reply = new CourseActivityReply();
        reply.setActivity_id(request.getActivity_id());
        reply.setAccount(request.getAccount());
        reply.setContent(text);
        if (hasImage) {
            try {
                FileStorageService.StoredFile stored = fileStorageService.saveTopicImage(image);
                reply.setAttachment_url(stored.url());
                reply.setAttachment_name(stored.originalName());
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (IOException ex) {
                System.err.println("话题图片上传失败: " + ex.getMessage());
                return false;
            }
        }
        return userMapper.addActivityReply(reply);
    }

    private void notifyAnnouncementPublished(Integer classId, String title) {
        List<CourseMember> members = userMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setType("announcement");
            notification.setMessage("新公告：" + title);
            userMapper.addNotification(notification);
        }
    }

    private void notifyTestPublished(Integer classId, String title) {
        List<CourseMember> members = userMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setType("test");
            notification.setMessage("新测试发布：" + title);
            userMapper.addNotification(notification);
        }
    }
}
