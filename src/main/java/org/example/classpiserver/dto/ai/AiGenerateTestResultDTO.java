package org.example.classpiserver.dto.ai;

import java.util.List;

public class AiGenerateTestResultDTO {
    private Boolean available;
    private String message;
    private List<AiGeneratedQuestionDTO> questions;

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AiGeneratedQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AiGeneratedQuestionDTO> questions) {
        this.questions = questions;
    }

    public static AiGenerateTestResultDTO unavailable(String message) {
        AiGenerateTestResultDTO dto = new AiGenerateTestResultDTO();
        dto.setAvailable(false);
        dto.setMessage(message);
        return dto;
    }
}
