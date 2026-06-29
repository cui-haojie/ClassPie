package org.example.classpiserver.support;

import org.example.classpiserver.mapper.course.CourseMapper;
import org.example.classpiserver.mapper.schoolclass.SchoolClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class EnrollmentSupport {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SchoolClassMapper schoolClassMapper;

    public void enrollAccountIfAbsent(String account, Long courseId) {
        Integer count = courseMapper.countAccountInCourse(account, courseId);
        if (count == null || count == 0) {
            Integer maxOrder = courseMapper.getMaxSortOrder(account);
            int nextOrder = (maxOrder == null ? -1 : maxOrder) + 1;
            courseMapper.createCourseWithOrder(account, courseId, nextOrder);
        }
    }

    public void enrollStudentInExistingCourses(String account, Integer schoolClassId) {
        Set<Long> courseIds = new LinkedHashSet<>();
        List<Long> legacyIds = schoolClassMapper.getCourseIdsBySchoolClassLegacy(schoolClassId);
        if (legacyIds != null) {
            courseIds.addAll(legacyIds);
        }
        List<Long> linkedIds = schoolClassMapper.getCourseIdsBySchoolClassLink(schoolClassId);
        if (linkedIds != null) {
            courseIds.addAll(linkedIds);
        }
        for (Long courseId : courseIds) {
            enrollAccountIfAbsent(account, courseId);
        }
    }

    public List<Integer> resolveSchoolClassIds(Integer singleId, List<Integer> multipleIds) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (multipleIds != null) {
            for (Integer id : multipleIds) {
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        if (singleId != null) {
            ids.add(singleId);
        }
        return new ArrayList<>(ids);
    }

    public String resolveDefaultSemester() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        if (month >= 9) {
            return year + "-" + (year + 1) + " 第一学期";
        }
        if (month >= 3) {
            return (year - 1) + "-" + year + " 第二学期";
        }
        return (year - 1) + "-" + year + " 第一学期";
    }

    public String generateUniqueCourseCode() {
        for (int i = 0; i < 10; i++) {
            String code = generateCourseCode();
            if (courseMapper.getCourseByCode(code) == null) {
                return code;
            }
        }
        return generateCourseCode() + (System.currentTimeMillis() % 1000);
    }

    private String generateCourseCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String encryptPassword(String password) {
        return password;
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public boolean isValidStudentId(String statusNumber) {
        return statusNumber != null && statusNumber.matches("\\d{6,20}");
    }
}
