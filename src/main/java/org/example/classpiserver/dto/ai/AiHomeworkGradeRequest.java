package org.example.classpiserver.dto.ai;

public class AiHomeworkGradeRequest {
    private String teacher_account;
    private String homework_name;
    private String homework_description;
    private String student_answer;
    private Integer max_score;

    public String getTeacher_account() {
        return teacher_account;
    }

    public void setTeacher_account(String teacher_account) {
        this.teacher_account = teacher_account;
    }

    public String getHomework_name() {
        return homework_name;
    }

    public void setHomework_name(String homework_name) {
        this.homework_name = homework_name;
    }

    public String getHomework_description() {
        return homework_description;
    }

    public void setHomework_description(String homework_description) {
        this.homework_description = homework_description;
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
