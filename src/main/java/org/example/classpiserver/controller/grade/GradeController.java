package org.example.classpiserver.controller.grade;

import org.example.classpiserver.dto.grade.CourseGradeBookDTO;
import org.example.classpiserver.dto.grade.CourseGradeWeightDTO;
import org.example.classpiserver.dto.grade.UpdateGradeWeightRequest;
import org.example.classpiserver.dto.homework.ClassIdRequest;
import org.example.classpiserver.service.grade.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/editor")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @PostMapping("/getCourseGradeBook")
    public CourseGradeBookDTO getCourseGradeBook(@RequestBody ClassIdRequest request) {
        return gradeService.getCourseGradeBook(request.getClass_id().longValue());
    }

    @PostMapping("/getCourseGradeWeight")
    public CourseGradeWeightDTO getCourseGradeWeight(@RequestBody ClassIdRequest request) {
        return gradeService.getCourseGradeWeight(request.getClass_id().longValue());
    }

    @PutMapping("/updateCourseGradeWeight")
    public boolean updateCourseGradeWeight(@RequestBody UpdateGradeWeightRequest request) {
        return gradeService.updateCourseGradeWeight(request);
    }
}
