package org.example.classpiserver.service.homework;

import org.example.classpiserver.dto.homework.RemindHomeworkRequest;
import org.example.classpiserver.entity.Content;
import org.example.classpiserver.entity.Homework;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HomeworkService {
    boolean addHomework(Homework homework, Integer classId, MultipartFile attachment);
    Integer getCountByClassId(Integer classId);
    List<Homework> getHomeworkByClassId(Integer classId);
    Homework getHomeworkById(Integer homeworkId);
    List<Content> getContentById(Long contentId);
    boolean setContentScore(int newScore, Long contentId, String account);
    boolean addContent(Content content);
    boolean submitHomework(Long contentId, String account, String details, MultipartFile file);
    boolean remindHomework(RemindHomeworkRequest request);
}
