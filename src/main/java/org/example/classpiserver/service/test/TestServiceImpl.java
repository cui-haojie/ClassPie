package org.example.classpiserver.service.test;

import org.example.classpiserver.dto.test.AddCourseTestRequest;
import org.example.classpiserver.dto.test.GradeTestAnswerRequest;
import org.example.classpiserver.dto.test.SaveTestDraftResult;
import org.example.classpiserver.dto.test.SubmitTestRequest;
import org.example.classpiserver.dto.test.TestAnswerInput;
import org.example.classpiserver.dto.test.TestAnswerResultDTO;
import org.example.classpiserver.dto.test.TestDetailDTO;
import org.example.classpiserver.dto.test.TestQuestionInput;
import org.example.classpiserver.dto.test.TestSubmissionSummaryDTO;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.TestAnswer;
import org.example.classpiserver.entity.TestQuestion;
import org.example.classpiserver.entity.TestSubmission;
import org.example.classpiserver.mapper.account.AccountMapper;
import org.example.classpiserver.mapper.activity.ActivityMapper;
import org.example.classpiserver.mapper.test.TestMapper;
import org.example.classpiserver.service.notification.NotificationService;
import org.example.classpiserver.util.HomeworkDeadlineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class TestServiceImpl implements TestService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private NotificationService notificationService;

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
            CourseActivity existing = activityMapper.getCourseActivityById(activityId);
            if (existing == null || !"test".equals(existing.getType()) || !"draft".equals(existing.getPublish_status())) {
                return new SaveTestDraftResult(null, false);
            }
            if (!request.getCreator_account().equals(existing.getCreator_account())) {
                return new SaveTestDraftResult(null, false);
            }
            if (!activityMapper.updateCourseTest(activityId, request.getTitle().trim(),
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
            if (!activityMapper.addCourseActivity(activity)) {
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
            CourseActivity existing = activityMapper.getCourseActivityById(activityId);
            if (existing == null || !"test".equals(existing.getType()) || !"draft".equals(existing.getPublish_status())) {
                return false;
            }
            if (!request.getCreator_account().equals(existing.getCreator_account())) {
                return false;
            }
            if (!activityMapper.updateCourseTest(activityId, request.getTitle().trim(),
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
            if (!activityMapper.addCourseActivity(activity)) {
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
        notificationService.notifyTestPublished(request.getClass_id(), activityId, request.getTitle().trim());
        return true;
    }

    @Override
    public TestDetailDTO getTestDetail(Long activityId, String account) {
        if (activityId == null) {
            return null;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(activityId);
        if (activity == null || !"test".equals(activity.getType())) {
            return null;
        }
        String status = accountMapper.getAccountStatus(account);
        boolean isTeacher = "老师".equals(status);
        if ("draft".equals(activity.getPublish_status()) && !isTeacher) {
            return null;
        }
        normalizeActivityTimes(activity);
        List<TestQuestion> questions = testMapper.getTestQuestionsByActivityId(activityId);
        int choiceCount = 0;
        int shortCount = 0;
        for (TestQuestion q : questions) {
            if ("choice".equals(q.getQuestion_type())) {
                choiceCount++;
            } else if ("short".equals(q.getQuestion_type())) {
                shortCount++;
            }
        }
        TestSubmission existingSubmission = (account != null && !account.isBlank())
                ? testMapper.getTestSubmissionByAccount(activityId, account) : null;
        boolean studentSubmitted = existingSubmission != null;
        if (!isTeacher && !studentSubmitted) {
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
        int maxScore = 0;
        for (TestQuestion q : questions) {
            maxScore += q.getScore() == null ? 0 : q.getScore();
        }
        dto.setMax_score(maxScore);
        if (account != null && !account.isBlank()) {
            TestSubmission submission = existingSubmission;
            if (submission != null) {
                dto.setSubmitted(true);
                dto.setTotal_score(submission.getTotal_score());
                dto.setMax_score(submission.getMax_score());
                dto.setAuto_score(submission.getAuto_score());
                dto.setManual_score(submission.getManual_score());
                dto.setIs_fully_graded(submission.getIs_fully_graded());
                if (submission.getSubmit_time() != null) {
                    submission.setSubmit_time(HomeworkDeadlineUtil.formatDisplay(submission.getSubmit_time()));
                }
                Map<Long, String> answers = new HashMap<>();
                Map<Long, TestAnswerResultDTO> answerResults = new HashMap<>();
                Map<Long, TestQuestion> questionMap = new HashMap<>();
                for (TestQuestion q : questions) {
                    questionMap.put(q.getId(), q);
                }
                List<TestAnswer> answerRows = testMapper.getTestAnswersFullBySubmissionId(submission.getId());
                for (TestAnswer row : answerRows) {
                    answers.put(row.getQuestion_id(), row.getAnswer() == null ? "" : row.getAnswer());
                    TestAnswerResultDTO result = new TestAnswerResultDTO();
                    result.setAnswer(row.getAnswer());
                    result.setScore(row.getScore());
                    TestQuestion q = questionMap.get(row.getQuestion_id());
                    result.setMax_score(q == null ? 0 : q.getScore());
                    result.setIs_correct(row.getIs_correct());
                    result.setPending("short".equals(q == null ? null : q.getQuestion_type()) && row.getScore() == null);
                    answerResults.put(row.getQuestion_id(), result);
                }
                dto.setMy_answers(answers);
                dto.setAnswer_results(answerResults);
            }
        }
        if (isTeacher) {
            dto.setSubmissions(buildTestSubmissionSummaries(activityId));
        }
        return dto;
    }

    @Override
    public boolean submitTest(SubmitTestRequest request) {
        if (request == null || request.getActivity_id() == null || request.getAccount() == null
                || request.getAccount().isBlank()) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || !"test".equals(activity.getType())) {
            return false;
        }
        if (!"published".equals(activity.getPublish_status())) {
            return false;
        }
        if (!HomeworkDeadlineUtil.isWithinWindow(activity.getStart_time(), activity.getDeadline())) {
            return false;
        }
        TestSubmission existing = testMapper.getTestSubmissionByAccount(request.getActivity_id(), request.getAccount());
        if (existing != null) {
            return false;
        }
        List<TestQuestion> questions = testMapper.getTestQuestionsByActivityId(request.getActivity_id());
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
        int autoScore = 0;
        int maxScore = 0;
        int shortCount = 0;
        for (TestQuestion question : questions) {
            maxScore += question.getScore() == null ? 0 : question.getScore();
            if ("short".equals(question.getQuestion_type())) {
                shortCount++;
            }
        }
        TestSubmission submission = new TestSubmission();
        submission.setActivity_id(request.getActivity_id());
        submission.setAccount(request.getAccount());
        submission.setAuto_score(0);
        submission.setManual_score(shortCount > 0 ? null : 0);
        submission.setTotal_score(0);
        submission.setMax_score(maxScore);
        submission.setIs_fully_graded(shortCount == 0);
        if (!testMapper.addTestSubmission(submission)) {
            return false;
        }
        for (TestQuestion question : questions) {
            String answer = answerMap.getOrDefault(question.getId(), "");
            if ("choice".equals(question.getQuestion_type())) {
                boolean correct = question.getCorrect_option() != null
                        && question.getCorrect_option().equalsIgnoreCase(answer);
                int qScore = correct ? (question.getScore() == null ? 0 : question.getScore()) : 0;
                autoScore += qScore;
                testMapper.addTestAnswerScored(submission.getId(), question.getId(), answer, qScore,
                        correct ? 1 : 0, 1);
            } else {
                testMapper.addTestAnswerScored(submission.getId(), question.getId(), answer, null, null, 0);
            }
        }
        submission.setAuto_score(autoScore);
        submission.setTotal_score(autoScore);
        testMapper.updateTestSubmissionScores(submission.getId(),
                shortCount > 0 ? null : 0,
                autoScore,
                shortCount == 0 ? 1 : 0);
        return true;
    }

    @Override
    public boolean gradeTestAnswer(GradeTestAnswerRequest request) {
        if (request == null || request.getActivity_id() == null || request.getQuestion_id() == null
                || request.getStudent_account() == null || request.getStudent_account().isBlank()) {
            return false;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(request.getTeacher_account()))) {
            return false;
        }
        int score = request.getScore() == null ? 0 : request.getScore();
        if (score < 0) {
            return false;
        }
        TestQuestion question = null;
        for (TestQuestion q : testMapper.getTestQuestionsByActivityId(request.getActivity_id())) {
            if (q.getId().equals(request.getQuestion_id())) {
                question = q;
                break;
            }
        }
        if (question == null || !"short".equals(question.getQuestion_type())) {
            return false;
        }
        int max = question.getScore() == null ? 100 : question.getScore();
        if (score > max) {
            return false;
        }
        TestSubmission submission = testMapper.getTestSubmissionByAccount(request.getActivity_id(), request.getStudent_account());
        if (submission == null) {
            return false;
        }
        if (!testMapper.updateTestAnswerScore(submission.getId(), request.getQuestion_id(), score)) {
            return false;
        }
        recalculateSubmissionScores(submission.getId());
        return true;
    }

    private void replaceTestQuestions(Long activityId, List<TestQuestionInput> inputs, boolean strict) {
        testMapper.deleteTestQuestionsByActivityId(activityId);
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
            String imageUrl = input.getStem_image_url() == null ? null : input.getStem_image_url().trim();
            if (stem.isEmpty() && (imageUrl == null || imageUrl.isEmpty())) {
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
            question.setStem_image_url(imageUrl == null || imageUrl.isEmpty() ? null : imageUrl);
            question.setSort_order(order++);
            int defaultScore = "short".equals(qType) ? 10 : 5;
            int score = input.getScore() == null || input.getScore() <= 0 ? defaultScore : input.getScore();
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
            if (!testMapper.addTestQuestion(question)) {
                throw new IllegalArgumentException("保存题目失败");
            }
        }
        if (strict && order == 0) {
            throw new IllegalArgumentException("题目不能为空");
        }
    }

    private List<TestSubmissionSummaryDTO> buildTestSubmissionSummaries(Long activityId) {
        List<TestSubmission> rows = testMapper.getTestSubmissionsByActivityId(activityId);
        List<TestSubmissionSummaryDTO> list = new ArrayList<>();
        for (TestSubmission row : rows) {
            TestSubmissionSummaryDTO item = new TestSubmissionSummaryDTO();
            item.setSubmission_id(row.getId());
            item.setAccount(row.getAccount());
            item.setAccount_name(row.getAccount_name());
            item.setAuto_score(row.getAuto_score());
            item.setManual_score(row.getManual_score());
            item.setTotal_score(row.getTotal_score());
            item.setMax_score(row.getMax_score());
            item.setIs_fully_graded(row.getIs_fully_graded());
            if (row.getSubmit_time() != null) {
                item.setSubmit_time(HomeworkDeadlineUtil.formatDisplay(row.getSubmit_time()));
            }
            list.add(item);
        }
        return list;
    }

    private void recalculateSubmissionScores(Long submissionId) {
        TestSubmission submission = testMapper.getTestSubmissionById(submissionId);
        if (submission == null) {
            return;
        }
        List<TestAnswer> answers = testMapper.getTestAnswersFullBySubmissionId(submissionId);
        int manual = 0;
        boolean allShortGraded = true;
        for (TestAnswer answer : answers) {
            if (Boolean.TRUE.equals(answer.getIs_auto_graded())) {
                continue;
            }
            if (answer.getScore() == null) {
                allShortGraded = false;
            } else {
                manual += answer.getScore();
            }
        }
        int auto = submission.getAuto_score() == null ? 0 : submission.getAuto_score();
        int total = auto + manual;
        testMapper.updateTestSubmissionScores(submissionId, manual, total, allShortGraded ? 1 : 0);
    }

    @Override
    public boolean deleteTest(Long activityId, String teacherAccount) {
        if (activityId == null || teacherAccount == null) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(activityId);
        if (activity == null || !"test".equals(activity.getType())) {
            return false;
        }
        if (!teacherAccount.equals(activity.getCreator_account())) {
            return false;
        }
        testMapper.deleteTestQuestionsByActivityId(activityId);
        return activityMapper.deleteActivityById(activityId);
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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
