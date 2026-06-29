package org.example.classpiserver.service.prep;

import org.example.classpiserver.dto.prep.ListPrepRequest;
import org.example.classpiserver.dto.prep.PrepIdRequest;
import org.example.classpiserver.dto.prep.PrepItemDetailDTO;
import org.example.classpiserver.dto.prep.PublishPrepRequest;
import org.example.classpiserver.entity.TeacherPrepItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PrepService {
    List<TeacherPrepItem> listPrepItems(ListPrepRequest request);
    PrepItemDetailDTO getPrepItem(PrepIdRequest request);
    PrepItemDetailDTO savePrepItem(Long id, String teacherAccount, String kind, String title, String content,
                                   String homeworkType, String deadline, String startTime,
                                   String attachmentUrl, String attachmentName,
                                   String questionsJson, MultipartFile file);
    boolean deletePrepItem(PrepIdRequest request);
    boolean publishPrepToCourse(PublishPrepRequest request);
}
