package org.example.classpiserver.service.homework;

import org.example.classpiserver.dto.homework.RemindHomeworkRequest;
import org.example.classpiserver.entity.Content;
import org.example.classpiserver.entity.Homework;
import org.example.classpiserver.mapper.course.CourseMapper;
import org.example.classpiserver.mapper.homework.HomeworkMapper;
import org.example.classpiserver.service.notification.NotificationService;
import org.example.classpiserver.util.FileStorageService;
import org.example.classpiserver.util.HomeworkDeadlineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean addHomework(Homework homework, Integer classId, MultipartFile attachment) {
        try {
            if (homework.getDeadline() != null) {
                homework.setDeadline(HomeworkDeadlineUtil.normalizeInput(homework.getDeadline()));
            }
            if (attachment != null && !attachment.isEmpty()) {
                FileStorageService.StoredFile stored = fileStorageService.saveHomeworkAttachment(attachment);
                homework.setAttachment_url(stored.url());
                homework.setAttachment_name(stored.originalName());
            }
            homeworkMapper.addHomework(homework);
            Integer homeworkId = homeworkMapper.getLastInsertId();
            homeworkMapper.setHomeworkContentId(homeworkId, homeworkId);
            homeworkMapper.addCourses_homework(classId, homeworkId);
            notificationService.notifyHomeworkPublished(classId, homeworkId, homework.getName());
            return true;
        } catch (Exception e) {
            System.err.println(homeworkMapper.getLastInsertId());
            System.err.println("添加作业失败");
            System.err.println(e);
            return false;
        }
    }

    @Override
    public Integer getCountByClassId(Integer classId) {
        System.err.println(classId);
        System.err.println(homeworkMapper.getCountByClassId(classId));
        return homeworkMapper.getCountByClassId(classId);
    }

    @Override
    public List<Homework> getHomeworkByClassId(Integer classId, String account) {
        List<Homework> homeworkList = new ArrayList<>();
        for (Integer homeworkId : homeworkMapper.getHomeworkIdsByClassId(classId)) {
            Homework homework = homeworkMapper.getHomework(homeworkId);
            normalizeHomeworkDeadline(homework);
            fillHomeworkSubmissionStats(homework, classId.longValue());
            fillStudentSubmissionStatus(homework, account);
            homeworkList.add(homework);
        }
        return homeworkList;
    }

    @Override
    public Homework getHomeworkById(Integer homeworkId, String account) {
        Homework homework = homeworkMapper.getHomework(homeworkId);
        normalizeHomeworkDeadline(homework);
        fillStudentSubmissionStatus(homework, account);
        return homework;
    }

    @Override
    public List<Content> getContentById(Long contentId) {
        return homeworkMapper.getContentByContentId(contentId);
    }

    @Override
    public boolean setContentScore(int newScore, Long contentId, String account) {
        return homeworkMapper.setContentScore(newScore, contentId, account);
    }

    @Override
    public boolean addContent(Content content) {
        return homeworkMapper.addContent(content);
    }

    @Override
    public boolean submitHomework(Long contentId, String account, String details, MultipartFile file) {
        if (contentId == null || account == null || account.isBlank()) {
            return false;
        }
        String normalizedAccount = account.trim();
        Homework homework = homeworkMapper.getHomeworkByContentOrId(contentId);
        if (homework == null) {
            return false;
        }
        long submissionContentId = resolveSubmissionContentId(homework);
        Integer existing = homeworkMapper.countContentSubmission(submissionContentId, normalizedAccount);
        if (existing != null && existing > 0) {
            return false;
        }
        if (HomeworkDeadlineUtil.isDeadlinePassed(homework.getDeadline())) {
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
        content.setContent_id(submissionContentId);
        content.setAccount(normalizedAccount);
        content.setScore(0);
        content.setDetails(text.isEmpty() ? "（附件提交）" : text);
        content.setAttachment_url(attachmentUrl);
        content.setAttachment_name(attachmentName);
        content.setIs_graded(false);
        return homeworkMapper.addContent(content);
    }

    @Override
    public boolean remindHomework(RemindHomeworkRequest request) {
        if (request == null || request.getHomework_id() == null || request.getClass_id() == null) {
            return false;
        }
        Homework homework = homeworkMapper.getHomework(request.getHomework_id());
        if (homework == null) {
            return false;
        }
        long submissionContentId = homework.getHomework_id();
        List<String> accounts = homeworkMapper.getUnsubmittedAccounts(request.getClass_id(), submissionContentId);
        if (accounts.isEmpty()) {
            return false;
        }
        for (String studentAccount : accounts) {
            notificationService.notifyHomeworkRemind(
                    studentAccount,
                    request.getClass_id(),
                    request.getHomework_id(),
                    "老师催交作业：" + homework.getName()
            );
        }
        return true;
    }

    @Override
    public boolean deleteHomework(Integer classId, Integer homeworkId, String teacherAccount) {
        if (classId == null || homeworkId == null || teacherAccount == null) {
            return false;
        }
        var course = courseMapper.getCourseByCourseId(classId.longValue());
        if (course == null || !teacherAccount.equals(course.getTeacher_account())) {
            return false;
        }
        homeworkMapper.deleteCourseHomeworkLink(classId, homeworkId);
        return homeworkMapper.deleteHomeworkById(homeworkId);
    }

    private long resolveSubmissionContentId(Homework homework) {
        if (homework == null) {
            return 0L;
        }
        // content 表 FK 指向 homework.homework_id；与 homework.content_id 字段无关
        return homework.getHomework_id();
    }

    private void fillHomeworkSubmissionStats(Homework homework, Long classId) {
        if (homework == null) {
            return;
        }
        long contentId = resolveSubmissionContentId(homework);
        Integer graded = homeworkMapper.countGradedSubmissions(contentId);
        Integer ungraded = homeworkMapper.countUngradedSubmissions(contentId);
        Integer unsubmitted = homeworkMapper.countUnsubmittedStudents(classId, contentId);
        homework.setGraded_count(graded == null ? 0 : graded);
        homework.setUngraded_count(ungraded == null ? 0 : ungraded);
        homework.setUnsubmitted_count(unsubmitted == null ? 0 : unsubmitted);
    }

    private void fillStudentSubmissionStatus(Homework homework, String account) {
        if (homework == null || account == null || account.isBlank()) {
            return;
        }
        String normalizedAccount = account.trim();
        long contentId = resolveSubmissionContentId(homework);
        Content submission = homeworkMapper.getContentByAccount(contentId, normalizedAccount);
        if (submission == null && homework.getContent_id() > 0 && homework.getContent_id() != homework.getHomework_id()) {
            submission = homeworkMapper.getContentByAccount((long) homework.getContent_id(), normalizedAccount);
        }
        if (submission == null) {
            homework.setMy_submitted(false);
            homework.setMy_graded(false);
            homework.setMy_score(null);
            return;
        }
        homework.setMy_submitted(true);
        homework.setMy_graded(Boolean.TRUE.equals(submission.getIs_graded()));
        homework.setMy_score(submission.getScore());
    }

    private void normalizeHomeworkDeadline(Homework homework) {
        if (homework != null && homework.getDeadline() != null) {
            homework.setDeadline(HomeworkDeadlineUtil.formatDisplay(homework.getDeadline()));
        }
    }
}
