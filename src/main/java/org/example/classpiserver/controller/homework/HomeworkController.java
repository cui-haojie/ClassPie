package org.example.classpiserver.controller.homework;

import org.example.classpiserver.dto.homework.ClassIdRequest;
import org.example.classpiserver.dto.homework.ContentIdRequest;
import org.example.classpiserver.dto.homework.HomeworkIdRequest;
import org.example.classpiserver.dto.homework.RemindHomeworkRequest;
import org.example.classpiserver.dto.homework.ScoreRequest;
import org.example.classpiserver.entity.Content;
import org.example.classpiserver.entity.Homework;
import org.example.classpiserver.service.homework.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/editor")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    @PostMapping(value = "/addHomework", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addHomework(
            @RequestParam("class_id") Integer classId,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("deadline") String deadline,
            @RequestParam(value = "details", required = false) String details,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        Homework homework = new Homework();
        homework.setName(name);
        homework.setType(type);
        homework.setDeadline(deadline);
        homework.setDetails(details);
        return homeworkService.addHomework(homework, classId, file);
    }

    @PostMapping("/getCountByClassId")
    public Integer getCountByClassId(@RequestBody ClassIdRequest request) {
        return homeworkService.getCountByClassId(request.getClass_id());
    }

    @PostMapping("/getHomeworkByClassId")
    public List<Homework> getHomeworkByClassId(@RequestBody ClassIdRequest request) {
        return homeworkService.getHomeworkByClassId(request.getClass_id());
    }

    @PostMapping("/getHomeworkById")
    public Homework getHomeworkById(@RequestBody HomeworkIdRequest request) {
        return homeworkService.getHomeworkById(request.getHomework_id());
    }

    @PostMapping("/getContentById")
    public List<Content> getContentById(@RequestBody ContentIdRequest request) {
        return homeworkService.getContentById(request.getContentId());
    }

    @PutMapping("/setScore")
    public boolean setScore(@RequestBody ScoreRequest request) {
        return homeworkService.setContentScore(request.getScore(), request.getContent_id(), request.getAccount());
    }

    @PostMapping("/addContent")
    public boolean addContent(@RequestBody Content content) {
        return homeworkService.addContent(content);
    }

    @PostMapping(value = "/submitHomework", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean submitHomework(
            @RequestParam("content_id") Long contentId,
            @RequestParam("account") String account,
            @RequestParam(value = "details", required = false) String details,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        return homeworkService.submitHomework(contentId, account, details, file);
    }

    @PostMapping("/remindHomework")
    public boolean remindHomework(@RequestBody RemindHomeworkRequest request) {
        return homeworkService.remindHomework(request);
    }
}
