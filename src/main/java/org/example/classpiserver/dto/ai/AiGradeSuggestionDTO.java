package org.example.classpiserver.dto.ai;

public class AiGradeSuggestionDTO {
    private Boolean available;
    private Integer suggested_score;
    private String comment;
    private String message;

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getSuggested_score() {
        return suggested_score;
    }

    public void setSuggested_score(Integer suggested_score) {
        this.suggested_score = suggested_score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static AiGradeSuggestionDTO unavailable(String message) {
        AiGradeSuggestionDTO dto = new AiGradeSuggestionDTO();
        dto.setAvailable(false);
        dto.setMessage(message);
        return dto;
    }
}
