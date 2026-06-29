package org.example.classpiserver.dto.ai;

public class AiTestShortGradeRequest {
    private String teacher_account;
    private String question_stem;
    private String student_answer;
    private Integer max_score;

    public String getTeacher_account() {
        return teacher_account;
    }

    public void setTeacher_account(String teacher_account) {
        this.teacher_account = teacher_account;
    }

    public String getQuestion_stem() {
        return question_stem;
    }

    public void setQuestion_stem(String question_stem) {
        this.question_stem = question_stem;
    }

    public String getStudent_answer() {
        return student_answer;
    }

    public void setStudent_answer(String student_answer) {
        this.student_answer = student_answer;
    }

    public Integer getMax_score() {
        return max_score;
    }

    public void setMax_score(Integer max_score) {
        this.max_score = max_score;
    }
}
