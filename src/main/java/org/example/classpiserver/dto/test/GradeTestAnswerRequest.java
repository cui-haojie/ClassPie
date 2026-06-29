package org.example.classpiserver.dto.test;

public class GradeTestAnswerRequest {
    private Long activity_id;
    private String student_account;
    private Long question_id;
    private Integer score;
    private String teacher_account;

    public Long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Long activity_id) {
        this.activity_id = activity_id;
    }

    public String getStudent_account() {
        return student_account;
    }

    public void setStudent_account(String student_account) {
        this.student_account = student_account;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTeacher_account() {
        return teacher_account;
    }

    public void setTeacher_account(String teacher_account) {
        this.teacher_account = teacher_account;
    }
}
