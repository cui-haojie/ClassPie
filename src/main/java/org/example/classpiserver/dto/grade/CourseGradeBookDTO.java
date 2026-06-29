package org.example.classpiserver.dto.grade;

import java.util.List;

public class CourseGradeBookDTO {
    private Long class_id;
    private Integer student_count;
    private CourseGradeWeightDTO weights;
    private List<StudentGradeRowDTO> rows;

    public Long getClass_id() { return class_id; }
    public void setClass_id(Long class_id) { this.class_id = class_id; }
    public Integer getStudent_count() { return student_count; }
    public void setStudent_count(Integer student_count) { this.student_count = student_count; }
    public CourseGradeWeightDTO getWeights() { return weights; }
    public void setWeights(CourseGradeWeightDTO weights) { this.weights = weights; }
    public List<StudentGradeRowDTO> getRows() { return rows; }
    public void setRows(List<StudentGradeRowDTO> rows) { this.rows = rows; }
}
