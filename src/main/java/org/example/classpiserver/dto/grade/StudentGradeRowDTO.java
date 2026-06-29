package org.example.classpiserver.dto.grade;

public class StudentGradeRowDTO {
    private String account;
    private String name;
    private Integer homework_avg;
    private Integer test_avg;
    private Integer attendance_rate;
    private Integer interaction_count;
    private Integer composite_score;

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getHomework_avg() { return homework_avg; }
    public void setHomework_avg(Integer homework_avg) { this.homework_avg = homework_avg; }
    public Integer getTest_avg() { return test_avg; }
    public void setTest_avg(Integer test_avg) { this.test_avg = test_avg; }
    public Integer getAttendance_rate() { return attendance_rate; }
    public void setAttendance_rate(Integer attendance_rate) { this.attendance_rate = attendance_rate; }
    public Integer getInteraction_count() { return interaction_count; }
    public void setInteraction_count(Integer interaction_count) { this.interaction_count = interaction_count; }
    public Integer getComposite_score() { return composite_score; }
    public void setComposite_score(Integer composite_score) { this.composite_score = composite_score; }
}
