package org.example.classpiserver.service.grade;

import org.example.classpiserver.dto.grade.CourseGradeBookDTO;
import org.example.classpiserver.dto.grade.CourseGradeWeightDTO;
import org.example.classpiserver.dto.grade.UpdateGradeWeightRequest;

public interface GradeService {
    CourseGradeBookDTO getCourseGradeBook(Long classId);
    CourseGradeWeightDTO getCourseGradeWeight(Long classId);
    boolean updateCourseGradeWeight(UpdateGradeWeightRequest request);
}
