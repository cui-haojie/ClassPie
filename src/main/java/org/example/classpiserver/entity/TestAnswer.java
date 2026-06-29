package org.example.classpiserver.entity;

public class TestAnswer {
    private Long id;
    private Long submission_id;
    private Long question_id;
    private String answer;
    private Integer score;
    private Boolean is_correct;
    private Boolean is_auto_graded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(Long submission_id) {
        this.submission_id = submission_id;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getIs_correct() {
        return is_correct;
    }

    public void setIs_correct(Boolean is_correct) {
        this.is_correct = is_correct;
    }

    public Boolean getIs_auto_graded() {
        return is_auto_graded;
    }

    public void setIs_auto_graded(Boolean is_auto_graded) {
        this.is_auto_graded = is_auto_graded;
    }
}
