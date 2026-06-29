package org.example.classpiserver.service.schoolclass;

import org.example.classpiserver.dto.schoolclass.ImportSchoolClassResult;
import org.example.classpiserver.dto.schoolclass.ImportStudentResult;
import org.example.classpiserver.dto.schoolclass.JoinStudentClassRequest;
import org.example.classpiserver.dto.schoolclass.SchoolClassRequest;
import org.example.classpiserver.dto.schoolclass.UpdateStudentSchoolClassRequest;
import org.example.classpiserver.entity.SchoolClass;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SchoolClassService {
    List<SchoolClass> listSchoolClasses();
    SchoolClass createSchoolClass(SchoolClassRequest request);
    boolean joinStudentClass(JoinStudentClassRequest request);
    boolean updateStudentSchoolClasses(UpdateStudentSchoolClassRequest request);
    List<SchoolClass> getStudentSchoolClasses(String account);
    ImportStudentResult importSchoolClassStudents(MultipartFile file, Integer schoolClassId);
    ImportSchoolClassResult importSchoolClasses(MultipartFile file, String teacherAccount, String defaultMechanism);
    byte[] buildStudentImportTemplate();
    byte[] buildSchoolClassImportTemplate();
}
