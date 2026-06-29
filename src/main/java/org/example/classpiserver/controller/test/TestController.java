package org.example.classpiserver.controller.test;

import org.example.classpiserver.dto.test.AddCourseTestRequest;
import org.example.classpiserver.dto.test.GetTestDetailRequest;
import org.example.classpiserver.dto.test.GradeTestAnswerRequest;
import org.example.classpiserver.dto.test.SaveTestDraftResult;
import org.example.classpiserver.dto.test.SubmitTestRequest;
import org.example.classpiserver.dto.test.TestDetailDTO;
import org.example.classpiserver.service.test.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/editor")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping("/addCourseTest")
    public boolean addCourseTest(@RequestBody AddCourseTestRequest request) {
        try {
            return testService.addCourseTest(request);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @PostMapping("/saveCourseTestDraft")
    public SaveTestDraftResult saveCourseTestDraft(@RequestBody AddCourseTestRequest request) {
        return testService.saveCourseTestDraft(request);
    }

    @PostMapping("/getTestDetail")
    public TestDetailDTO getTestDetail(@RequestBody GetTestDetailRequest request) {
        return testService.getTestDetail(request.getActivity_id(), request.getAccount());
    }

    @PostMapping("/submitTest")
    public boolean submitTest(@RequestBody SubmitTestRequest request) {
        return testService.submitTest(request);
    }

    @PostMapping("/gradeTestAnswer")
    public boolean gradeTestAnswer(@RequestBody GradeTestAnswerRequest request) {
        return testService.gradeTestAnswer(request);
    }

    @PostMapping("/deleteTest")
    public boolean deleteTest(@RequestBody org.example.classpiserver.dto.activity.ActivityIdRequest request) {
        return testService.deleteTest(request.getActivity_id(), request.getTeacher_account());
    }
}
