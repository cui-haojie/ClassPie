package org.example.classpiserver.service.test;

import org.example.classpiserver.dto.test.AddCourseTestRequest;
import org.example.classpiserver.dto.test.GradeTestAnswerRequest;
import org.example.classpiserver.dto.test.SaveTestDraftResult;
import org.example.classpiserver.dto.test.SubmitTestRequest;
import org.example.classpiserver.dto.test.TestDetailDTO;

public interface TestService {
    SaveTestDraftResult saveCourseTestDraft(AddCourseTestRequest request);
    boolean addCourseTest(AddCourseTestRequest request);
    TestDetailDTO getTestDetail(Long activityId, String account);
    boolean submitTest(SubmitTestRequest request);
    boolean gradeTestAnswer(GradeTestAnswerRequest request);
}
