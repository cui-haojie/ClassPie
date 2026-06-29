package org.example.classpiserver.service.interaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.classpiserver.dto.interaction.AddCourseInteractionRequest;
import org.example.classpiserver.dto.interaction.AskInteractionQuestionRequest;
import org.example.classpiserver.dto.interaction.CloseInteractionRequest;
import org.example.classpiserver.dto.interaction.InteractionDetailDTO;
import org.example.classpiserver.dto.interaction.PickItemDTO;
import org.example.classpiserver.dto.interaction.PickRandomStudentRequest;
import org.example.classpiserver.dto.interaction.PickRandomStudentResult;
import org.example.classpiserver.dto.interaction.QaAnswerItemDTO;
import org.example.classpiserver.dto.interaction.SubmitInteractionRequest;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.entity.InteractionPick;
import org.example.classpiserver.entity.InteractionResponse;
import org.example.classpiserver.mapper.account.AccountMapper;
import org.example.classpiserver.mapper.activity.ActivityMapper;
import org.example.classpiserver.mapper.course.CourseMapper;
import org.example.classpiserver.mapper.interaction.InteractionMapper;
import org.example.classpiserver.service.notification.NotificationService;
import org.example.classpiserver.util.HomeworkDeadlineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class InteractionServiceImpl implements InteractionService {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private InteractionMapper interactionMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean addCourseInteraction(AddCourseInteractionRequest request) {
        if (request == null || request.getClass_id() == null || request.getTitle() == null || request.getTitle().isBlank()) {
            return false;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(request.getCreator_account()))) {
            return false;
        }
        CourseActivity activity = new CourseActivity();
        activity.setClass_id(request.getClass_id().longValue());
        activity.setType("interaction");
        activity.setTitle(request.getTitle().trim());
        activity.setContent(trimOrNull(request.getContent()));
        activity.setCreator_account(request.getCreator_account());
        activity.setPublish_status("published");
        activity.setInteraction_kind("qa");
        try {
            activity.setInteraction_options(JSON.writeValueAsString(Map.of("status", "active", "round", 1)));
        } catch (Exception ex) {
            return false;
        }
        if (!activityMapper.addCourseActivity(activity)) {
            return false;
        }
        notificationService.notifyInteractionPublished(request.getClass_id(), activity.getId(), activity.getTitle());
        return true;
    }

    @Override
    public InteractionDetailDTO getInteractionDetail(Long activityId, String account) {
        if (activityId == null) {
            return null;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(activityId);
        if (activity == null || !"interaction".equals(activity.getType())) {
            return null;
        }
        normalizeActivityTimes(activity);
        InteractionMeta meta = parseInteractionMeta(activity.getInteraction_options());
        boolean isTeacher = "老师".equals(accountMapper.getAccountStatus(account));
        int round = meta.round;
        InteractionDetailDTO dto = new InteractionDetailDTO();
        dto.setActivity(activity);
        dto.setIs_teacher(isTeacher);
        dto.setStatus(meta.status);
        dto.setCurrent_round(round);
        dto.setCurrent_question(activity.getContent());
        List<InteractionResponse> currentAnswers = interactionMapper.getInteractionResponsesByRound(activityId, round);
        dto.setAnswer_count(currentAnswers.size());
        Integer participantCount = interactionMapper.countDistinctInteractionParticipants(activityId);
        dto.setParticipant_count(participantCount == null ? 0 : participantCount);
        List<CourseMember> students = courseMapper.getCourseStudents(activity.getClass_id());
        dto.setStudent_count(students.size());
        dto.setParticipated(false);
        dto.setI_was_picked(false);
        if (account != null && !account.isBlank()) {
            InteractionResponse mine = interactionMapper.getInteractionResponseByAccountAndRound(activityId, account, round);
            if (mine != null) {
                dto.setParticipated(true);
                dto.setMy_content(mine.getContent());
            }
        }
        List<QaAnswerItemDTO> answers = new ArrayList<>();
        for (InteractionResponse row : currentAnswers) {
            QaAnswerItemDTO item = new QaAnswerItemDTO();
            item.setAccount(row.getAccount());
            item.setAccount_name(row.getAccount_name());
            item.setContent(row.getContent());
            if (row.getCreate_time() != null) {
                item.setCreate_time(HomeworkDeadlineUtil.formatDisplay(row.getCreate_time()));
            }
            answers.add(item);
        }
        dto.setAnswers(answers);
        List<InteractionPick> pickRows = interactionMapper.getInteractionPicksByActivityId(activityId);
        List<PickItemDTO> picks = new ArrayList<>();
        PickItemDTO latestPick = null;
        for (InteractionPick row : pickRows) {
            PickItemDTO item = toPickItem(row);
            picks.add(item);
            if (latestPick == null) {
                latestPick = item;
            }
            if (account != null && account.equals(row.getAccount())) {
                dto.setI_was_picked(true);
            }
        }
        dto.setPicks(picks);
        dto.setLatest_pick(latestPick);
        return dto;
    }

    @Override
    public boolean submitInteractionResponse(SubmitInteractionRequest request) {
        if (request == null || request.getActivity_id() == null || request.getAccount() == null
                || request.getAccount().isBlank()) {
            return false;
        }
        if ("老师".equals(accountMapper.getAccountStatus(request.getAccount()))) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || !"interaction".equals(activity.getType())) {
            return false;
        }
        InteractionMeta meta = parseInteractionMeta(activity.getInteraction_options());
        if (!"active".equals(meta.status)) {
            return false;
        }
        String text = request.getContent() == null ? "" : request.getContent().trim();
        if (text.isBlank()) {
            return false;
        }
        InteractionResponse existing = interactionMapper.getInteractionResponseByAccountAndRound(
                request.getActivity_id(), request.getAccount(), meta.round);
        if (existing != null) {
            return interactionMapper.updateInteractionResponse(request.getActivity_id(), request.getAccount(), meta.round, text);
        }
        InteractionResponse response = new InteractionResponse();
        response.setActivity_id(request.getActivity_id());
        response.setAccount(request.getAccount());
        response.setContent(text);
        response.setRound_num(meta.round);
        return interactionMapper.addInteractionResponse(response);
    }

    @Override
    public boolean askInteractionQuestion(AskInteractionQuestionRequest request) {
        if (request == null || request.getActivity_id() == null || request.getQuestion() == null
                || request.getQuestion().isBlank()) {
            return false;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(request.getTeacher_account()))) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || !"interaction".equals(activity.getType())) {
            return false;
        }
        InteractionMeta meta = parseInteractionMeta(activity.getInteraction_options());
        if (!"active".equals(meta.status)) {
            return false;
        }
        String question = request.getQuestion().trim();
        boolean hasCurrentQuestion = activity.getContent() != null && !activity.getContent().isBlank();
        int targetRound = hasCurrentQuestion ? meta.round + 1 : meta.round;
        meta.round = targetRound;
        try {
            String optionsJson = JSON.writeValueAsString(Map.of("status", meta.status, "round", targetRound));
            return activityMapper.updateCourseInteractionState(activity.getId(), question, optionsJson);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public PickRandomStudentResult pickRandomStudent(PickRandomStudentRequest request) {
        PickRandomStudentResult result = new PickRandomStudentResult();
        result.setNo_student_left(false);
        if (request == null || request.getActivity_id() == null) {
            result.setNo_student_left(true);
            return result;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(request.getTeacher_account()))) {
            result.setNo_student_left(true);
            return result;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || !"interaction".equals(activity.getType())) {
            result.setNo_student_left(true);
            return result;
        }
        InteractionMeta meta = parseInteractionMeta(activity.getInteraction_options());
        if (!"active".equals(meta.status)) {
            result.setNo_student_left(true);
            return result;
        }
        List<CourseMember> students = courseMapper.getCourseStudents(activity.getClass_id());
        if (students.isEmpty()) {
            result.setNo_student_left(true);
            return result;
        }
        Set<String> pickedAccounts = new LinkedHashSet<>(interactionMapper.getPickedAccountsByActivityId(request.getActivity_id()));
        List<CourseMember> candidates = new ArrayList<>();
        for (CourseMember student : students) {
            if (!pickedAccounts.contains(student.getAccount())) {
                candidates.add(student);
            }
        }
        if (candidates.isEmpty()) {
            candidates.addAll(students);
        }
        CourseMember chosen = candidates.get(new java.util.Random().nextInt(candidates.size()));
        interactionMapper.addInteractionPick(request.getActivity_id(), chosen.getAccount());
        PickItemDTO picked = new PickItemDTO();
        picked.setAccount(chosen.getAccount());
        picked.setAccount_name(chosen.getName());
        result.setPicked(picked);
        notificationService.notifyStudentPicked(activity, chosen.getAccount());
        return result;
    }

    @Override
    public boolean closeInteraction(CloseInteractionRequest request) {
        if (request == null || request.getActivity_id() == null) {
            return false;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(request.getTeacher_account()))) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || !"interaction".equals(activity.getType())) {
            return false;
        }
        InteractionMeta meta = parseInteractionMeta(activity.getInteraction_options());
        meta.status = "closed";
        try {
            String optionsJson = JSON.writeValueAsString(Map.of("status", meta.status, "round", meta.round));
            return activityMapper.updateCourseInteractionState(activity.getId(), activity.getContent(), optionsJson);
        } catch (Exception ex) {
            return false;
        }
    }

    private PickItemDTO toPickItem(InteractionPick row) {
        PickItemDTO item = new PickItemDTO();
        item.setAccount(row.getAccount());
        item.setAccount_name(row.getAccount_name());
        if (row.getCreate_time() != null) {
            item.setCreate_time(HomeworkDeadlineUtil.formatDisplay(row.getCreate_time()));
        }
        return item;
    }

    private InteractionMeta parseInteractionMeta(String json) {
        InteractionMeta meta = new InteractionMeta();
        meta.status = "active";
        meta.round = 1;
        if (json == null || json.isBlank()) {
            return meta;
        }
        try {
            Map<String, Object> map = JSON.readValue(json, new TypeReference<Map<String, Object>>() {});
            Object status = map.get("status");
            if (status != null) {
                meta.status = String.valueOf(status);
            }
            Object round = map.get("round");
            if (round instanceof Number number) {
                meta.round = number.intValue();
            } else if (round != null) {
                meta.round = Integer.parseInt(String.valueOf(round));
            }
        } catch (Exception ignored) {
        }
        return meta;
    }

    private static class InteractionMeta {
        private String status;
        private int round;
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
}
