package org.example.classpiserver.controller.prep;

import org.example.classpiserver.dto.prep.ListPrepRequest;
import org.example.classpiserver.dto.prep.PrepIdRequest;
import org.example.classpiserver.dto.prep.PrepItemDetailDTO;
import org.example.classpiserver.dto.prep.PublishPrepRequest;
import org.example.classpiserver.entity.TeacherPrepItem;
import org.example.classpiserver.service.prep.PrepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/editor")
public class PrepController {

    @Autowired
    private PrepService prepService;

    @PostMapping("/listPrepItems")
    public List<TeacherPrepItem> listPrepItems(@RequestBody ListPrepRequest request) {
        return prepService.listPrepItems(request);
    }

    @PostMapping("/getPrepItem")
    public PrepItemDetailDTO getPrepItem(@RequestBody PrepIdRequest request) {
        return prepService.getPrepItem(request);
    }

    @PostMapping(value = "/savePrepItem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PrepItemDetailDTO savePrepItem(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("teacher_account") String teacherAccount,
            @RequestParam("kind") String kind,
            @RequestParam("title") String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "homework_type", required = false) String homeworkType,
            @RequestParam(value = "deadline", required = false) String deadline,
            @RequestParam(value = "start_time", required = false) String startTime,
            @RequestParam(value = "attachment_url", required = false) String attachmentUrl,
            @RequestParam(value = "attachment_name", required = false) String attachmentName,
            @RequestParam(value = "questions_json", required = false) String questionsJson,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        return prepService.savePrepItem(id, teacherAccount, kind, title, content,
                homeworkType, deadline, startTime, attachmentUrl, attachmentName, questionsJson, file);
    }

    @PostMapping("/deletePrepItem")
    public boolean deletePrepItem(@RequestBody PrepIdRequest request) {
        return prepService.deletePrepItem(request);
    }

    @PostMapping("/publishPrepToCourse")
    public boolean publishPrepToCourse(@RequestBody PublishPrepRequest request) {
        return prepService.publishPrepToCourse(request);
    }
}
