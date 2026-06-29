package org.example.classpiserver.controller.schoolclass;

import org.example.classpiserver.dto.account.AccountRequest;
import org.example.classpiserver.dto.schoolclass.ImportSchoolClassResult;
import org.example.classpiserver.dto.schoolclass.ImportStudentResult;
import org.example.classpiserver.dto.schoolclass.JoinStudentClassRequest;
import org.example.classpiserver.dto.schoolclass.SchoolClassRequest;
import org.example.classpiserver.dto.schoolclass.UpdateStudentSchoolClassRequest;
import org.example.classpiserver.entity.SchoolClass;
import org.example.classpiserver.service.schoolclass.SchoolClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/editor")
public class SchoolClassController {

    @Autowired
    private SchoolClassService schoolClassService;

    @PostMapping("/listSchoolClasses")
    public List<SchoolClass> listSchoolClasses() {
        return schoolClassService.listSchoolClasses();
    }

    @PostMapping("/createSchoolClass")
    public SchoolClass createSchoolClass(@RequestBody SchoolClassRequest request) {
        return schoolClassService.createSchoolClass(request);
    }

    @PostMapping("/joinStudentClass")
    public boolean joinStudentClass(@RequestBody JoinStudentClassRequest request) {
        return schoolClassService.joinStudentClass(request);
    }

    @PutMapping("/updateStudentSchoolClasses")
    public boolean updateStudentSchoolClasses(@RequestBody UpdateStudentSchoolClassRequest request) {
        return schoolClassService.updateStudentSchoolClasses(request);
    }

    @PostMapping("/studentSchoolClass")
    public List<SchoolClass> getStudentSchoolClass(@RequestBody AccountRequest request) {
        return schoolClassService.getStudentSchoolClasses(request.getAccount());
    }

    @PostMapping(value = "/importSchoolClassStudents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportStudentResult importSchoolClassStudents(
            @RequestParam("file") MultipartFile file,
            @RequestParam("school_class_id") Integer schoolClassId) {
        return schoolClassService.importSchoolClassStudents(file, schoolClassId);
    }

    @GetMapping("/downloadStudentImportTemplate")
    public ResponseEntity<byte[]> downloadStudentImportTemplate() {
        byte[] bytes = schoolClassService.buildStudentImportTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_import_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/downloadSchoolClassImportTemplate")
    public ResponseEntity<byte[]> downloadSchoolClassImportTemplate() {
        byte[] bytes = schoolClassService.buildSchoolClassImportTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=school_class_import_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @PostMapping(value = "/importSchoolClasses", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportSchoolClassResult importSchoolClasses(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teacher_account") String teacherAccount,
            @RequestParam(value = "default_mechanism", required = false) String defaultMechanism) {
        return schoolClassService.importSchoolClasses(file, teacherAccount, defaultMechanism);
    }
}
