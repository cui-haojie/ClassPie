package org.example.classpiserver.service.interaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.classpiserver.dto.activity.ActivityIdRequest;
import org.example.classpiserver.dto.interaction.AddCourseInteractionRequest;
import org.example.classpiserver.dto.interaction.AskInteractionQuestionRequest;
import org.example.classpiserver.dto.interaction.CloseInteractionRequest;
import org.example.classpiserver.dto.interaction.InteractionDetailDTO;
import org.example.classpiserver.dto.interaction.PickItemDTO;
import org.example.classpiserver.dto.interaction.PickRandomStudentRequest;
import org.example.classpiserver.dto.interaction.PickRandomStudentResult;
import org.example.classpiserver.dto.interaction.QaAnswerItemDTO;
import org.example.classpiserver.dto.interaction.StartRaceRequest;
import org.example.classpiserver.dto.interaction.SubmitInteractionRequest;
import org.example.classpiserver.dto.interaction.VoteStatDTO;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.entity.InteractionPick;
import org.example.classpiserver.entity.InteractionResponse;
import org.example.classpiserver.live.LiveEventPublisher;
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

    @Autowired
    private LiveEventPublisher liveEventPublisher;

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
        String kind = request.getInteraction_kind();
        if (kind == null || kind.isBlank()) {
            kind = "qa";
        }
        activity.setInteraction_kind(kind);
        try {
            java.util.Map<String, Object> options = new java.util.LinkedHashMap<>();
            options.put("status", "active");
            options.put("round", 1);
            options.put("race_round", 1);
            options.put("race_open", false);
            if ("vote".equals(kind) && request.getOptions() != null && !request.getOptions().isEmpty()) {
                options.put("vote_options", request.getOptions());
            } else if ("vote".equals(kind)) {
                options.put("vote_options", java.util.List.of("赞成", "反对"));
            }
            activity.setInteraction_options(JSON.writeValueAsString(options));
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
        dto.setInteraction_kind(activity.getInteraction_kind());
        if ("vote".equals(activity.getInteraction_kind())) {
            dto.setVote_options(meta.voteOptions);
            dto.setVote_stats(buildVoteStats(activityId, meta.voteOptions));
            if (account != null) {
                InteractionResponse vote = interactionMapper.getInteractionResponseByAccountAndRound(activityId, account, 1);
                if (vote != null) {
                    dto.setMy_option_index(vote.getOption_index());
                    dto.setParticipated(true);
                }
            }
        }
        if ("race".equals(activity.getInteraction_kind())) {
            dto.setRace_open(meta.raceOpen);
            List<InteractionResponse> raceRows = interactionMapper.getInteractionResponsesByRound(activityId, meta.raceRound);
            List<QaAnswerItemDTO> raceResults = new ArrayList<>();
            for (InteractionResponse row : raceRows) {
                QaAnswerItemDTO item = new QaAnswerItemDTO();
                item.setAccount(row.getAccount());
                item.setAccount_name(row.getAccount_name());
                if (row.getCreate_time() != null) {
                    item.setCreate_time(HomeworkDeadlineUtil.formatDisplay(row.getCreate_time()));
                }
                raceResults.add(item);
            }
            dto.setRace_results(raceResults);
        }
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
        String kind = activity.getInteraction_kind() == null ? "qa" : activity.getInteraction_kind();
        if ("vote".equals(kind)) {
            return submitVote(request, request.getActivity_id(), meta);
        }
        if ("race".equals(kind)) {
            return submitRace(request, request.getActivity_id(), meta);
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
        boolean ok = interactionMapper.addInteractionResponse(response);
        if (ok) {
            liveEventPublisher.publishInteraction(request.getActivity_id(), "interaction_updated", null);
        }
        return ok;
    }

    private boolean submitVote(SubmitInteractionRequest request, Long activityId, InteractionMeta meta) {
        if (request.getOption_index() == null || meta.voteOptions == null || meta.voteOptions.isEmpty()) {
            return false;
        }
        if (request.getOption_index() < 0 || request.getOption_index() >= meta.voteOptions.size()) {
            return false;
        }
        Integer existing = interactionMapper.countVoteByAccount(activityId, request.getAccount());
        if (existing != null && existing > 0) {
            return false;
        }
        InteractionResponse response = new InteractionResponse();
        response.setActivity_id(activityId);
        response.setAccount(request.getAccount());
        response.setOption_index(request.getOption_index());
        response.setContent(meta.voteOptions.get(request.getOption_index()));
        response.setRound_num(1);
        boolean ok = interactionMapper.addInteractionResponse(response);
        if (ok) {
            liveEventPublisher.publishInteraction(activityId, "interaction_updated", null);
        }
        return ok;
    }

    private boolean submitRace(SubmitInteractionRequest request, Long activityId, InteractionMeta meta) {
        if (!meta.raceOpen) {
            return false;
        }
        Integer existing = interactionMapper.countRaceByAccountAndRound(activityId, request.getAccount(), meta.raceRound);
        if (existing != null && existing > 0) {
            return false;
        }
        InteractionResponse response = new InteractionResponse();
        response.setActivity_id(activityId);
        response.setAccount(request.getAccount());
        response.setContent("抢答");
        response.setRound_num(meta.raceRound);
        boolean ok = interactionMapper.addInteractionResponse(response);
        if (ok) {
            liveEventPublisher.publishInteraction(activityId, "interaction_updated", null);
        }
        return ok;
    }

    @Override
    public boolean startRace(StartRaceRequest request) {
        if (request == null || request.getActivity_id() == null) {
            return false;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(request.getTeacher_account()))) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || !"interaction".equals(activity.getType()) || !"race".equals(activity.getInteraction_kind())) {
            return false;
        }
        InteractionMeta meta = parseInteractionMeta(activity.getInteraction_options());
        if (!"active".equals(meta.status)) {
            return false;
        }
        meta.raceRound = meta.raceRound + 1;
        meta.raceOpen = true;
        try {
            java.util.Map<String, Object> options = buildOptionsMap(meta);
            boolean ok = activityMapper.updateCourseInteractionState(activity.getId(), activity.getContent(), JSON.writeValueAsString(options));
            if (ok) {
                liveEventPublisher.publishInteraction(activity.getId(), "race_started", java.util.Map.of("race_round", meta.raceRound));
            }
            return ok;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean deleteInteraction(ActivityIdRequest request) {
        if (request == null || request.getActivity_id() == null || request.getTeacher_account() == null) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || !"interaction".equals(activity.getType())) {
            return false;
        }
        if (!request.getTeacher_account().equals(activity.getCreator_account())) {
            return false;
        }
        return activityMapper.deleteActivityById(request.getActivity_id());
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
            String optionsJson = JSON.writeValueAsString(buildOptionsMap(meta));
            boolean ok = activityMapper.updateCourseInteractionState(activity.getId(), question, optionsJson);
            if (ok) {
                liveEventPublisher.publishInteraction(activity.getId(), "interaction_updated", null);
            }
            return ok;
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
        meta.raceOpen = false;
        try {
            String optionsJson = JSON.writeValueAsString(buildOptionsMap(meta));
            boolean ok = activityMapper.updateCourseInteractionState(activity.getId(), activity.getContent(), optionsJson);
            if (ok) {
                liveEventPublisher.publishInteraction(activity.getId(), "interaction_closed", null);
            }
            return ok;
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
        meta.raceRound = 1;
        meta.raceOpen = false;
        meta.voteOptions = new ArrayList<>();
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
            Object raceRound = map.get("race_round");
            if (raceRound instanceof Number rr) {
                meta.raceRound = rr.intValue();
            } else if (raceRound != null) {
                meta.raceRound = Integer.parseInt(String.valueOf(raceRound));
            }
            Object raceOpen = map.get("race_open");
            if (raceOpen instanceof Boolean b) {
                meta.raceOpen = b;
            } else if (raceOpen != null) {
                meta.raceOpen = Boolean.parseBoolean(String.valueOf(raceOpen));
            }
            Object voteOptions = map.get("vote_options");
            if (voteOptions instanceof List<?> list) {
                for (Object item : list) {
                    if (item != null) {
                        meta.voteOptions.add(String.valueOf(item));
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return meta;
    }

    private java.util.Map<String, Object> buildOptionsMap(InteractionMeta meta) {
        java.util.Map<String, Object> options = new java.util.LinkedHashMap<>();
        options.put("status", meta.status);
        options.put("round", meta.round);
        options.put("race_round", meta.raceRound);
        options.put("race_open", meta.raceOpen);
        if (meta.voteOptions != null && !meta.voteOptions.isEmpty()) {
            options.put("vote_options", meta.voteOptions);
        }
        return options;
    }

    private List<VoteStatDTO> buildVoteStats(Long activityId, List<String> voteOptions) {
        List<VoteStatDTO> stats = new ArrayList<>();
        if (voteOptions == null || voteOptions.isEmpty()) {
            return stats;
        }
        int total = 0;
        int[] counts = new int[voteOptions.size()];
        for (int i = 0; i < voteOptions.size(); i++) {
            Integer count = interactionMapper.countVotesByOption(activityId, i);
            counts[i] = count == null ? 0 : count;
            total += counts[i];
        }
        for (int i = 0; i < voteOptions.size(); i++) {
            VoteStatDTO stat = new VoteStatDTO();
            stat.setIndex(i);
            stat.setLabel(voteOptions.get(i));
            stat.setCount(counts[i]);
            stat.setPercent(total == 0 ? 0 : (int) Math.round(counts[i] * 100.0 / total));
            stats.add(stat);
        }
        return stats;
    }

    private static class InteractionMeta {
        private String status;
        private int round;
        private int raceRound;
        private boolean raceOpen;
        private List<String> voteOptions;
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
