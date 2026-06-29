package org.example.classpiserver.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.classpiserver.config.AiProperties;
import org.example.classpiserver.dto.ai.AiGenerateTestResultDTO;
import org.example.classpiserver.dto.ai.AiGeneratedQuestionDTO;
import org.example.classpiserver.dto.ai.AiGradeSuggestionDTO;
import org.example.classpiserver.dto.ai.AiStatusDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String GRADE_SYSTEM_PROMPT = """
            你是一名严谨的高校课程助教，负责根据题目与学生答案给出评分建议。
            必须只返回 JSON，不要 markdown，格式：
            {"suggested_score":整数,"comment":"50字以内的评语"}
            suggested_score 不得超过满分，不得低于 0。
            """;
    private static final String TEST_GENERATE_SYSTEM_PROMPT = """
            你是一名高校出题老师，根据知识点生成测试题。
            必须只返回 JSON，不要 markdown，格式：
            {"questions":[{"question_type":"choice|short","stem":"题干","option_a":"A选项","option_b":"B选项","option_c":"C选项","option_d":"D选项","correct_option":"A|B|C|D","score":整数}]}
            选择题必须有四个选项和正确答案；简答题省略 option 字段与 correct_option。
            题目应贴合中国高校课程，表述清晰，难度适中。
            """;

    private final AiProperties aiProperties;
    private final RestClient restClient;

    public AiService(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(15));
        factory.setReadTimeout(Duration.ofSeconds(Math.max(15, aiProperties.getTimeoutSeconds())));
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    public AiStatusDTO getStatus() {
        AiStatusDTO dto = new AiStatusDTO();
        dto.setConfigured(aiProperties.isConfigured());
        dto.setModel(aiProperties.getModel());
        return dto;
    }

    public AiGradeSuggestionDTO suggestHomeworkGrade(String homeworkName, String homeworkDescription,
                                                     String studentAnswer, int maxScore) {
        if (!aiProperties.isConfigured()) {
            return AiGradeSuggestionDTO.unavailable(configHint());
        }
        String userPrompt = """
                作业名称：%s
                作业要求：%s
                满分：%d
                学生提交：
                %s
                """.formatted(
                safe(homeworkName),
                safe(homeworkDescription),
                maxScore,
                safe(studentAnswer)
        );
        return parseGradeSuggestion(chat(GRADE_SYSTEM_PROMPT, userPrompt), maxScore);
    }

    public AiGradeSuggestionDTO suggestTestShortGrade(String questionStem, String studentAnswer, int maxScore) {
        if (!aiProperties.isConfigured()) {
            return AiGradeSuggestionDTO.unavailable(configHint());
        }
        String userPrompt = """
                简答题题干：%s
                满分：%d
                学生答案：
                %s
                """.formatted(safe(questionStem), maxScore, safe(studentAnswer));
        return parseGradeSuggestion(chat(GRADE_SYSTEM_PROMPT, userPrompt), maxScore);
    }

    public AiGenerateTestResultDTO generateTestQuestions(String topic, String courseName,
                                                         int choiceCount, int shortCount) {
        if (!aiProperties.isConfigured()) {
            return AiGenerateTestResultDTO.unavailable(configHint());
        }
        int choices = Math.min(Math.max(choiceCount, 0), 10);
        int shorts = Math.min(Math.max(shortCount, 0), 5);
        if (choices + shorts == 0) {
            return AiGenerateTestResultDTO.unavailable("请至少指定 1 道题");
        }
        String userPrompt = """
                课程：%s
                知识点/主题：%s
                请生成选择题 %d 道、简答题 %d 道。
                选择题 score 建议 5，简答题 score 建议 10。
                """.formatted(safe(courseName), safe(topic), choices, shorts);
        try {
            String raw = chat(TEST_GENERATE_SYSTEM_PROMPT, userPrompt);
            JsonNode root = JSON.readTree(extractJson(raw));
            JsonNode questionsNode = root.get("questions");
            if (questionsNode == null || !questionsNode.isArray() || questionsNode.isEmpty()) {
                return AiGenerateTestResultDTO.unavailable("AI 未返回有效题目，请重试");
            }
            List<AiGeneratedQuestionDTO> questions = new ArrayList<>();
            for (JsonNode node : questionsNode) {
                AiGeneratedQuestionDTO q = JSON.treeToValue(node, AiGeneratedQuestionDTO.class);
                if (q == null || q.getStem() == null || q.getStem().isBlank()) {
                    continue;
                }
                if ("choice".equals(q.getQuestion_type())) {
                    if (q.getOption_a() == null || q.getOption_b() == null
                            || q.getOption_c() == null || q.getOption_d() == null) {
                        continue;
                    }
                    if (q.getCorrect_option() == null || q.getCorrect_option().isBlank()) {
                        q.setCorrect_option("A");
                    }
                    if (q.getScore() == null || q.getScore() <= 0) {
                        q.setScore(5);
                    }
                } else {
                    q.setQuestion_type("short");
                    if (q.getScore() == null || q.getScore() <= 0) {
                        q.setScore(10);
                    }
                }
                questions.add(q);
            }
            if (questions.isEmpty()) {
                return AiGenerateTestResultDTO.unavailable("AI 返回的题目格式无效，请重试");
            }
            AiGenerateTestResultDTO dto = new AiGenerateTestResultDTO();
            dto.setAvailable(true);
            dto.setQuestions(questions);
            return dto;
        } catch (Exception ex) {
            return AiGenerateTestResultDTO.unavailable("AI 生成失败：" + ex.getMessage());
        }
    }

    private AiGradeSuggestionDTO parseGradeSuggestion(String raw, int maxScore) {
        try {
            JsonNode root = JSON.readTree(extractJson(raw));
            AiGradeSuggestionDTO dto = new AiGradeSuggestionDTO();
            dto.setAvailable(true);
            int score = root.path("suggested_score").asInt(0);
            dto.setSuggested_score(Math.max(0, Math.min(maxScore, score)));
            dto.setComment(root.path("comment").asText(""));
            return dto;
        } catch (Exception ex) {
            return AiGradeSuggestionDTO.unavailable("AI 评分解析失败，请重试");
        }
    }

    private String chat(String systemPrompt, String userPrompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", aiProperties.getModel());
        body.put("temperature", 0.3);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));
        String url = trimTrailingSlash(aiProperties.getBaseUrl()) + "/chat/completions";
        String response = restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        try {
            JsonNode root = JSON.readTree(response);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                throw new IllegalStateException("AI 响应为空");
            }
            return content.asText();
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("AI 响应解析失败", ex);
        }
    }

    private String extractJson(String text) {
        if (text == null) {
            return "{}";
        }
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf('\n');
            int end = trimmed.lastIndexOf("```");
            if (start >= 0 && end > start) {
                trimmed = trimmed.substring(start + 1, end).trim();
            }
        }
        return trimmed;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "（未提供）" : value.trim();
    }

    private String trimTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String configHint() {
        return "AI 未配置：请在 application.yaml 中设置 classpi.ai.enabled=true 并填写 api-key（支持 OpenAI 兼容接口）";
    }
}
