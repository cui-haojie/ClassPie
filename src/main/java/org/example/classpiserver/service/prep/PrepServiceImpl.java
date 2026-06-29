package org.example.classpiserver.service.prep;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.classpiserver.dto.prep.ListPrepRequest;
import org.example.classpiserver.dto.prep.PrepIdRequest;
import org.example.classpiserver.dto.prep.PrepItemDetailDTO;
import org.example.classpiserver.dto.prep.PublishPrepRequest;
import org.example.classpiserver.dto.test.AddCourseTestRequest;
import org.example.classpiserver.dto.test.TestQuestionInput;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.Homework;
import org.example.classpiserver.entity.TeacherPrepItem;
import org.example.classpiserver.entity.TeacherPrepTestQuestion;
import org.example.classpiserver.mapper.account.AccountMapper;
import org.example.classpiserver.mapper.course.CourseMapper;
import org.example.classpiserver.mapper.prep.PrepMapper;
import org.example.classpiserver.service.activity.ActivityService;
import org.example.classpiserver.service.homework.HomeworkService;
import org.example.classpiserver.service.test.TestService;
import org.example.classpiserver.util.FileStorageService;
import org.example.classpiserver.util.HomeworkDeadlineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class PrepServiceImpl implements PrepService {

    private static final Set<String> PREP_KINDS = Set.of("homework", "topic", "material", "announcement", "test");
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private PrepMapper prepMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TestService testService;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<TeacherPrepItem> listPrepItems(ListPrepRequest request) {
        if (request == null || request.getTeacher_account() == null || request.getTeacher_account().isBlank()) {
            return List.of();
        }
        if (!"老师".equals(accountMapper.getAccountStatus(request.getTeacher_account()))) {
            return List.of();
        }
        String kind = request.getKind();
        if (kind != null && !kind.isBlank() && !PREP_KINDS.contains(kind)) {
            return List.of();
        }
        return prepMapper.listByTeacher(request.getTeacher_account(), kind);
    }

    @Override
    @Transactional(readOnly = true)
    public PrepItemDetailDTO getPrepItem(PrepIdRequest request) {
        if (request == null || request.getId() == null) {
            return null;
        }
        TeacherPrepItem item = prepMapper.getById(request.getId());
        if (item == null) {
            return null;
        }
        if (request.getTeacher_account() != null && !request.getTeacher_account().equals(item.getTeacher_account())) {
            return null;
        }
        return toDetail(item);
    }

    @Override
    public PrepItemDetailDTO savePrepItem(Long id, String teacherAccount, String kind, String title, String content,
                                          String homeworkType, String deadline, String startTime,
                                          String attachmentUrl, String attachmentName,
                                          String questionsJson, MultipartFile file) {
        if (teacherAccount == null || teacherAccount.isBlank() || kind == null || !PREP_KINDS.contains(kind)) {
            return null;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(teacherAccount))) {
            return null;
        }
        if (title == null || title.isBlank()) {
            return null;
        }
        TeacherPrepItem item = id == null ? new TeacherPrepItem() : prepMapper.getById(id);
        if (id != null && (item == null || !teacherAccount.equals(item.getTeacher_account()))) {
            return null;
        }
        if (item == null) {
            item = new TeacherPrepItem();
        }
        item.setTeacher_account(teacherAccount);
        item.setKind(kind);
        item.setTitle(title.trim());
        item.setContent(trimOrNull(content));
        if (file != null && !file.isEmpty()) {
            try {
                FileStorageService.StoredFile stored = fileStorageService.saveHomeworkAttachment(file);
                item.setAttachment_url(stored.url());
                item.setAttachment_name(stored.originalName());
            } catch (IOException | IllegalArgumentException ex) {
                return null;
            }
        } else if (attachmentUrl != null && !attachmentUrl.isBlank()) {
            item.setAttachment_url(attachmentUrl.trim());
            item.setAttachment_name(attachmentName == null || attachmentName.isBlank() ? "附件" : attachmentName.trim());
        } else if (id == null) {
            item.setAttachment_url(null);
            item.setAttachment_name(null);
        }
        item.setMeta_json(buildMetaJson(kind, homeworkType, deadline, startTime));
        boolean ok;
        if (id == null) {
            ok = prepMapper.insertItem(item);
        } else {
            ok = prepMapper.updateItem(item);
        }
        if (!ok || item.getId() == null) {
            return null;
        }
        if ("test".equals(kind)) {
            saveTestQuestions(item.getId(), questionsJson);
        }
        return toDetail(prepMapper.getById(item.getId()));
    }

    @Override
    public boolean deletePrepItem(PrepIdRequest request) {
        if (request == null || request.getId() == null || request.getTeacher_account() == null) {
            return false;
        }
        TeacherPrepItem item = prepMapper.getById(request.getId());
        if (item == null || !request.getTeacher_account().equals(item.getTeacher_account())) {
            return false;
        }
        prepMapper.deleteQuestions(request.getId());
        return prepMapper.deleteItem(request.getId());
    }

    @Override
    public boolean publishPrepToCourse(PublishPrepRequest request) {
        if (request == null || request.getPrep_id() == null || request.getClass_id() == null
                || request.getTeacher_account() == null) {
            return false;
        }
        if (!canTeachCourse(request.getTeacher_account(), request.getClass_id().longValue())) {
            return false;
        }
        TeacherPrepItem item = prepMapper.getById(request.getPrep_id());
        if (item == null || !request.getTeacher_account().equals(item.getTeacher_account())) {
            return false;
        }
        return switch (item.getKind()) {
            case "homework" -> publishHomework(item, request);
            case "topic", "material", "announcement" -> publishActivity(item, request);
            case "test" -> publishTest(item, request);
            default -> false;
        };
    }

    private boolean publishHomework(TeacherPrepItem item, PublishPrepRequest request) {
        Map<String, String> meta = parseMeta(item.getMeta_json());
        String hwType = firstNonBlank(request.getHomework_type(), meta.get("homework_type"), "个人作业");
        String deadline = firstNonBlank(request.getDeadline(), meta.get("deadline"));
        if (deadline == null || deadline.isBlank()) {
            return false;
        }
        Homework homework = new Homework();
        homework.setName(item.getTitle());
        homework.setType(hwType);
        homework.setDeadline(HomeworkDeadlineUtil.normalizeInput(deadline));
        homework.setDetails(item.getContent());
        homework.setAttachment_url(item.getAttachment_url());
        homework.setAttachment_name(item.getAttachment_name());
        return homeworkService.addHomework(homework, request.getClass_id(), null);
    }

    private boolean publishActivity(TeacherPrepItem item, PublishPrepRequest request) {
        CourseActivity activity = new CourseActivity();
        activity.setType(item.getKind());
        activity.setTitle(item.getTitle());
        activity.setContent(item.getContent());
        activity.setCreator_account(request.getTeacher_account());
        activity.setAttachment_url(item.getAttachment_url());
        activity.setAttachment_name(item.getAttachment_name());
        return activityService.addCourseActivity(activity, request.getClass_id(), null);
    }

    private boolean publishTest(TeacherPrepItem item, PublishPrepRequest request) {
        Map<String, String> meta = parseMeta(item.getMeta_json());
        String startTime = firstNonBlank(request.getStart_time(), meta.get("start_time"));
        String deadline = firstNonBlank(request.getDeadline(), meta.get("deadline"));
        if (startTime == null || startTime.isBlank() || deadline == null || deadline.isBlank()) {
            return false;
        }
        List<TeacherPrepTestQuestion> prepQuestions = prepMapper.listQuestions(item.getId());
        if (prepQuestions == null || prepQuestions.isEmpty()) {
            return false;
        }
        AddCourseTestRequest testRequest = new AddCourseTestRequest();
        testRequest.setClass_id(request.getClass_id());
        testRequest.setTitle(item.getTitle());
        testRequest.setContent(item.getContent());
        testRequest.setStart_time(startTime);
        testRequest.setDeadline(deadline);
        testRequest.setCreator_account(request.getTeacher_account());
        List<TestQuestionInput> questions = new ArrayList<>();
        for (TeacherPrepTestQuestion q : prepQuestions) {
            TestQuestionInput input = new TestQuestionInput();
            input.setQuestion_type(q.getQuestion_type());
            input.setStem(q.getStem());
            input.setOption_a(q.getOption_a());
            input.setOption_b(q.getOption_b());
            input.setOption_c(q.getOption_c());
            input.setOption_d(q.getOption_d());
            input.setCorrect_option(q.getCorrect_option());
            input.setScore(q.getScore());
            input.setStem_image_url(q.getStem_image_url());
            questions.add(input);
        }
        testRequest.setQuestions(questions);
        return testService.addCourseTest(testRequest);
    }

    private void saveTestQuestions(Long prepItemId, String questionsJson) {
        prepMapper.deleteQuestions(prepItemId);
        if (questionsJson == null || questionsJson.isBlank()) {
            return;
        }
        try {
            List<TestQuestionInput> inputs = JSON.readValue(questionsJson, new TypeReference<>() {});
            if (inputs == null) {
                return;
            }
            int order = 0;
            for (TestQuestionInput input : inputs) {
                if (input == null) {
                    continue;
                }
                String stem = input.getStem() == null ? "" : input.getStem().trim();
                String imageUrl = trimOrNull(input.getStem_image_url());
                if (stem.isEmpty() && imageUrl == null) {
                    continue;
                }
                String qType = input.getQuestion_type() == null ? "choice" : input.getQuestion_type().trim();
                if (!"choice".equals(qType) && !"short".equals(qType)) {
                    continue;
                }
                TeacherPrepTestQuestion q = new TeacherPrepTestQuestion();
                q.setPrep_item_id(prepItemId);
                q.setQuestion_type(qType);
                q.setStem(stem);
                q.setStem_image_url(imageUrl);
                q.setOption_a(input.getOption_a());
                q.setOption_b(input.getOption_b());
                q.setOption_c(input.getOption_c());
                q.setOption_d(input.getOption_d());
                q.setCorrect_option(input.getCorrect_option());
                int defaultScore = "short".equals(qType) ? 10 : 5;
                q.setScore(input.getScore() == null || input.getScore() <= 0 ? defaultScore : input.getScore());
                q.setSort_order(order++);
                prepMapper.insertQuestion(q);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("invalid questions json");
        }
    }

    private PrepItemDetailDTO toDetail(TeacherPrepItem item) {
        PrepItemDetailDTO dto = new PrepItemDetailDTO();
        dto.setId(item.getId());
        dto.setTeacher_account(item.getTeacher_account());
        dto.setKind(item.getKind());
        dto.setTitle(item.getTitle());
        dto.setContent(item.getContent());
        dto.setAttachment_url(item.getAttachment_url());
        dto.setAttachment_name(item.getAttachment_name());
        dto.setMeta_json(item.getMeta_json());
        dto.setCreate_time(item.getCreate_time());
        dto.setUpdate_time(item.getUpdate_time());
        Map<String, String> meta = parseMeta(item.getMeta_json());
        dto.setHomework_type(meta.get("homework_type"));
        dto.setDeadline(meta.get("deadline"));
        dto.setStart_time(meta.get("start_time"));
        if ("test".equals(item.getKind())) {
            dto.setQuestions(prepMapper.listQuestions(item.getId()));
        }
        return dto;
    }

    private String buildMetaJson(String kind, String homeworkType, String deadline, String startTime) {
        Map<String, String> meta = new LinkedHashMap<>();
        if ("homework".equals(kind)) {
            if (homeworkType != null && !homeworkType.isBlank()) {
                meta.put("homework_type", homeworkType.trim());
            }
            if (deadline != null && !deadline.isBlank()) {
                meta.put("deadline", HomeworkDeadlineUtil.normalizeInput(deadline));
            }
        }
        if ("test".equals(kind)) {
            if (startTime != null && !startTime.isBlank()) {
                meta.put("start_time", HomeworkDeadlineUtil.normalizeInput(startTime));
            }
            if (deadline != null && !deadline.isBlank()) {
                meta.put("deadline", HomeworkDeadlineUtil.normalizeInput(deadline));
            }
        }
        if (meta.isEmpty()) {
            return null;
        }
        try {
            return JSON.writeValueAsString(meta);
        } catch (Exception ex) {
            return null;
        }
    }

    private Map<String, String> parseMeta(String metaJson) {
        if (metaJson == null || metaJson.isBlank()) {
            return Map.of();
        }
        try {
            return JSON.readValue(metaJson, new TypeReference<>() {});
        } catch (Exception ex) {
            return Map.of();
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private String trimOrNull(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean canTeachCourse(String teacherAccount, Long classId) {
        if (!"老师".equals(accountMapper.getAccountStatus(teacherAccount))) {
            return false;
        }
        Course course = courseMapper.getCourseByCourseId(classId);
        if (course == null) {
            return false;
        }
        if (teacherAccount.equals(course.getTeacher_account())) {
            return true;
        }
        Integer inCourse = courseMapper.countAccountInCourse(teacherAccount, classId);
        return inCourse != null && inCourse > 0;
    }
}
