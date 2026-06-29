package org.example.classpiserver.service.course;

import org.example.classpiserver.dto.course.ArchiveCourseRequest;
import org.example.classpiserver.dto.course.CourseOrderRequest;
import org.example.classpiserver.dto.course.CourseRequest;
import org.example.classpiserver.dto.course.CourseUpdateRequest;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.entity.SchoolClass;
import org.example.classpiserver.mapper.course.CourseMapper;
import org.example.classpiserver.mapper.schoolclass.SchoolClassMapper;
import org.example.classpiserver.support.EnrollmentSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SchoolClassMapper schoolClassMapper;

    @Autowired
    private EnrollmentSupport enrollmentSupport;

    @Override
    public List<Long> getCourseIdByAccount(String account) {
        return courseMapper.getCourseIdByAccount(account);
    }

    @Override
    public List<Long> getArchivedCourseIdByAccount(String account) {
        return courseMapper.getArchivedCourseIdByAccount(account);
    }

    @Override
    public Course getCourseByCode(String account, String code) {
        Course course = courseMapper.getCourseByCode(code);
        if (course != null) {
            enrollmentSupport.enrollAccountIfAbsent(account, course.getId());
        }
        return course;
    }

    @Override
    public void addCourse(String account, Long classId) {
        enrollmentSupport.enrollAccountIfAbsent(account, classId);
    }

    @Override
    public List<Course> getCourseByCourseId(List<Long> courseId) {
        List<Course> courseList = new ArrayList<>();
        for (Long id : courseId) {
            Course course = courseMapper.getCourseByCourseId(id);
            if (course != null) {
                courseList.add(course);
            }
        }
        return courseList;
    }

    @Override
    public List<Course> getCourseByCourseIdWithPinStatus(String account, List<Long> courseId) {
        List<Course> courseList = new ArrayList<>();
        for (Long id : courseId) {
            Course course = courseMapper.getCourseByCourseId(id);
            if (course != null) {
                Integer pinnedStatus = courseMapper.getCoursePinStatus(account, id);
                course.setIs_pinned(pinnedStatus != null && pinnedStatus == 1);
                courseList.add(course);
            }
        }
        return courseList;
    }

    @Override
    public boolean togglePinCourse(String account, Long courseId, Boolean isPinned) {
        Course course = courseMapper.getCourseByCourseId(courseId);
        if (course == null) return false;
        return courseMapper.updateCoursePin(isPinned, account, courseId);
    }

    @Override
    public Course addCourse(CourseRequest course) {
        if (course == null || course.getTeacher_account() == null) {
            return null;
        }
        List<Integer> schoolClassIds = enrollmentSupport.resolveSchoolClassIds(course.getSchool_class_id(), course.getSchool_class_ids());
        if (!schoolClassIds.isEmpty()) {
            List<String> classNames = new ArrayList<>();
            for (Integer classId : schoolClassIds) {
                SchoolClass schoolClass = schoolClassMapper.getSchoolClassById(classId);
                if (schoolClass != null && schoolClass.getName() != null) {
                    classNames.add(schoolClass.getName());
                }
            }
            if ((course.getSelected_classes() == null || course.getSelected_classes().isBlank()) && !classNames.isEmpty()) {
                course.setSelected_classes(String.join("、", classNames));
            }
            course.setSchool_class_id(schoolClassIds.get(0));
        } else {
            course.setSchool_class_id(null);
        }
        if (course.getSemester() == null || course.getSemester().isBlank()) {
            course.setSemester(enrollmentSupport.resolveDefaultSemester());
        }
        course.setCode(enrollmentSupport.generateUniqueCourseCode());
        if (!courseMapper.addCourse(course)) {
            return null;
        }
        Long courseId = courseMapper.getLastInsertCourseId();
        if (courseId == null) {
            return null;
        }
        enrollmentSupport.enrollAccountIfAbsent(course.getTeacher_account(), courseId);
        Set<String> enrolledStudents = new LinkedHashSet<>();
        for (Integer classId : schoolClassIds) {
            courseMapper.insertCourseSchoolClass(courseId, classId);
            for (String studentAccount : schoolClassMapper.getStudentAccountsBySchoolClass(classId)) {
                if (enrolledStudents.add(studentAccount)) {
                    enrollmentSupport.enrollAccountIfAbsent(studentAccount, courseId);
                }
            }
        }
        return courseMapper.getCourseByCourseId(courseId);
    }

    @Override
    public boolean updateCourseOrder(CourseOrderRequest request) {
        if (request == null || request.getAccount() == null || request.getAccount().isBlank()) {
            return false;
        }
        List<Long> courseIds = request.getCourse_ids();
        if (courseIds == null || courseIds.isEmpty()) {
            return false;
        }
        for (int i = 0; i < courseIds.size(); i++) {
            Long courseId = courseIds.get(i);
            if (courseId == null) {
                continue;
            }
            Integer linked = courseMapper.countAccountInCourse(request.getAccount(), courseId);
            if (linked == null || linked == 0) {
                continue;
            }
            courseMapper.updateCourseSortOrder(request.getAccount(), courseId, i);
        }
        return true;
    }

    @Override
    public boolean addTeacherCourse(String account) {
        try {
            for (Long id : courseMapper.getCourseIdByTeacherAccount(account)) {
                courseMapper.createCourse(account, id);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Course getCourseById(Long courseId) {
        Course course = courseMapper.getCourseByCourseId(courseId);
        if (course != null) {
            course.setSchool_class_ids(getCourseSchoolClassIds(courseId));
        }
        return course;
    }

    @Override
    public Integer getCountByCourseId(Long id) {
        return courseMapper.getCountByCourseId(id);
    }

    @Override
    public boolean leaveCourse(String account, Long courseId) {
        return courseMapper.leaveCourse(courseId, account);
    }

    @Override
    public boolean updateCourseInfo(CourseUpdateRequest request) {
        if (request == null || request.getId() == null || request.getTeacher_account() == null) {
            return false;
        }
        Course existing = courseMapper.getCourseByCourseId(request.getId());
        if (existing == null || !request.getTeacher_account().equals(existing.getTeacher_account())) {
            return false;
        }
        List<Integer> oldClassIds = getCourseSchoolClassIds(request.getId());
        List<Integer> newClassIds = request.getSchool_class_ids() != null
                ? enrollmentSupport.resolveSchoolClassIds(null, request.getSchool_class_ids())
                : oldClassIds;
        for (Integer classId : newClassIds) {
            if (schoolClassMapper.getSchoolClassById(classId) == null) {
                return false;
            }
        }
        if ((request.getSelected_classes() == null || request.getSelected_classes().isBlank()) && !newClassIds.isEmpty()) {
            List<String> classNames = new ArrayList<>();
            for (Integer classId : newClassIds) {
                SchoolClass schoolClass = schoolClassMapper.getSchoolClassById(classId);
                if (schoolClass != null && schoolClass.getName() != null) {
                    classNames.add(schoolClass.getName());
                }
            }
            if (!classNames.isEmpty()) {
                request.setSelected_classes(String.join("、", classNames));
            }
        }
        request.setSchool_class_id(newClassIds.isEmpty() ? null : newClassIds.get(0));
        if (!courseMapper.updateCourseInfo(request)) {
            return false;
        }
        if (request.getSchool_class_ids() != null) {
            courseMapper.deleteCourseSchoolClassLinks(request.getId());
            for (Integer classId : newClassIds) {
                courseMapper.insertCourseSchoolClass(request.getId(), classId);
            }
            Set<Integer> addedClassIds = new LinkedHashSet<>(newClassIds);
            oldClassIds.forEach(addedClassIds::remove);
            for (Integer classId : addedClassIds) {
                for (String studentAccount : schoolClassMapper.getStudentAccountsBySchoolClass(classId)) {
                    enrollmentSupport.enrollAccountIfAbsent(studentAccount, request.getId());
                }
            }
        }
        return true;
    }

    @Override
    public boolean archiveCourse(ArchiveCourseRequest request) {
        if (request == null || request.getClass_id() == null || request.getAccount() == null || request.getAccount().isBlank()) {
            return false;
        }
        Course course = courseMapper.getCourseByCourseId(request.getClass_id());
        if (course == null || !request.getAccount().equals(course.getTeacher_account())) {
            return false;
        }
        return courseMapper.setCourseArchivedForClass(
                request.getClass_id(),
                request.isArchived() ? 1 : 0
        );
    }

    @Override
    public List<CourseMember> getCourseMembers(Long classId) {
        return courseMapper.getCourseMembers(classId);
    }

    private List<Integer> getCourseSchoolClassIds(Long courseId) {
        Set<Integer> ids = new LinkedHashSet<>();
        List<Integer> linked = courseMapper.getSchoolClassIdsByCourseId(courseId);
        if (linked != null) {
            ids.addAll(linked);
        }
        Course course = courseMapper.getCourseByCourseId(courseId);
        if (course != null && course.getSchool_class_id() != null) {
            ids.add(course.getSchool_class_id());
        }
        return new ArrayList<>(ids);
    }
}
