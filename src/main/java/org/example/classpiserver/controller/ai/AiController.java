package org.example.classpiserver.controller.ai;

import org.example.classpiserver.dto.ai.AiGenerateTestRequest;
import org.example.classpiserver.dto.ai.AiGenerateTestResultDTO;
import org.example.classpiserver.dto.ai.AiGradeSuggestionDTO;
import org.example.classpiserver.dto.ai.AiHomeworkGradeRequest;
import org.example.classpiserver.dto.ai.AiStatusDTO;
import org.example.classpiserver.dto.ai.AiTestShortGradeRequest;
import org.example.classpiserver.service.account.AccountService;
import org.example.classpiserver.service.ai.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/editor/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/status")
    public AiStatusDTO status() {
        return aiService.getStatus();
    }

    @PostMapping("/suggestHomeworkGrade")
    public AiGradeSuggestionDTO suggestHomeworkGrade(@RequestBody AiHomeworkGradeRequest request) {
        if (!isTeacher(request == null ? null : request.getTeacher_account())) {
            return AiGradeSuggestionDTO.unavailable("仅教师可使用 AI 批阅辅助");
        }
        int maxScore = request.getMax_score() == null ? 100 : request.getMax_score();
        try {
            return aiService.suggestHomeworkGrade(
                    request.getHomework_name(),
                    request.getHomework_description(),
                    request.getStudent_answer(),
                    maxScore
            );
        } catch (Exception ex) {
            return AiGradeSuggestionDTO.unavailable("AI 调用失败：" + ex.getMessage());
        }
    }

    @PostMapping("/suggestTestShortGrade")
    public AiGradeSuggestionDTO suggestTestShortGrade(@RequestBody AiTestShortGradeRequest request) {
        if (!isTeacher(request == null ? null : request.getTeacher_account())) {
            return AiGradeSuggestionDTO.unavailable("仅教师可使用 AI 批阅辅助");
        }
        int maxScore = request.getMax_score() == null ? 10 : request.getMax_score();
        try {
            return aiService.suggestTestShortGrade(
                    request.getQuestion_stem(),
                    request.getStudent_answer(),
                    maxScore
            );
        } catch (Exception ex) {
            return AiGradeSuggestionDTO.unavailable("AI 调用失败：" + ex.getMessage());
        }
    }

    @PostMapping("/generateTestQuestions")
    public AiGenerateTestResultDTO generateTestQuestions(@RequestBody AiGenerateTestRequest request) {
        if (!isTeacher(request == null ? null : request.getTeacher_account())) {
            return AiGenerateTestResultDTO.unavailable("仅教师可使用 AI 出题");
        }
        if (request.getTopic() == null || request.getTopic().isBlank()) {
            return AiGenerateTestResultDTO.unavailable("请填写知识点或主题");
        }
        try {
            return aiService.generateTestQuestions(
                    request.getTopic().trim(),
                    request.getCourse_name(),
                    request.getChoice_count() == null ? 3 : request.getChoice_count(),
                    request.getShort_count() == null ? 1 : request.getShort_count()
            );
        } catch (Exception ex) {
            return AiGenerateTestResultDTO.unavailable("AI 调用失败：" + ex.getMessage());
        }
    }

    private boolean isTeacher(String account) {
        return account != null && !account.isBlank() && "老师".equals(accountService.getAccountStatus(account));
    }
}
