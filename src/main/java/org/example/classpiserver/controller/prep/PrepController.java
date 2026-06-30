package org.example.classpiserver.controller.prep;

import org.example.classpiserver.dto.prep.ListPrepRequest;
import org.example.classpiserver.dto.prep.PrepIdRequest;
import org.example.classpiserver.dto.prep.PrepItemDetailDTO;
import org.example.classpiserver.dto.prep.PublishPrepRequest;
import org.example.classpiserver.entity.TeacherPrepItem;
import org.example.classpiserver.service.prep.PrepService;
import org.example.classpiserver.util.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/editor")
public class PrepController {

    @Autowired
    private PrepService prepService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(value = "/uploadTestQuestionImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadTestQuestionImage(@RequestParam("file") MultipartFile file) throws Exception {
        FileStorageService.StoredFile stored = fileStorageService.saveTestQuestionImage(file);
        return Map.of("url", stored.url());
    }

    @PostMapping(value = "/uploadRichTextImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadRichTextImage(@RequestParam("file") MultipartFile file) throws Exception {
        FileStorageService.StoredFile stored = fileStorageService.saveRichTextImage(file);
        return Map.of("url", stored.url());
    }

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
