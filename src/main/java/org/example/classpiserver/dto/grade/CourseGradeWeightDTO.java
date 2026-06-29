package org.example.classpiserver.dto.grade;

public class CourseGradeWeightDTO {
    private Integer homework_weight;
    private Integer test_weight;
    private Integer attendance_weight;
    private Integer interaction_weight;

    public CourseGradeWeightDTO() {
    }

    public CourseGradeWeightDTO(Integer homework_weight, Integer test_weight, Integer attendance_weight, Integer interaction_weight) {
        this.homework_weight = homework_weight;
        this.test_weight = test_weight;
        this.attendance_weight = attendance_weight;
        this.interaction_weight = interaction_weight;
    }

    public Integer getHomework_weight() { return homework_weight; }
    public void setHomework_weight(Integer homework_weight) { this.homework_weight = homework_weight; }
    public Integer getTest_weight() { return test_weight; }
    public void setTest_weight(Integer test_weight) { this.test_weight = test_weight; }
    public Integer getAttendance_weight() { return attendance_weight; }
    public void setAttendance_weight(Integer attendance_weight) { this.attendance_weight = attendance_weight; }
    public Integer getInteraction_weight() { return interaction_weight; }
    public void setInteraction_weight(Integer interaction_weight) { this.interaction_weight = interaction_weight; }
}
