package org.example.classpiserver.service.grade;

import org.example.classpiserver.dto.grade.CourseGradeBookDTO;
import org.example.classpiserver.dto.grade.CourseGradeWeightDTO;
import org.example.classpiserver.dto.grade.StudentGradeRowDTO;
import org.example.classpiserver.dto.grade.UpdateGradeWeightRequest;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.CourseGradeWeight;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.mapper.account.AccountMapper;
import org.example.classpiserver.mapper.attendance.AttendanceMapper;
import org.example.classpiserver.mapper.course.CourseMapper;
import org.example.classpiserver.mapper.grade.GradeMapper;
import org.example.classpiserver.mapper.grade.GradeWeightMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GradeServiceImpl implements GradeService {

    private static final int DEFAULT_HOMEWORK_WEIGHT = 35;
    private static final int DEFAULT_TEST_WEIGHT = 35;
    private static final int DEFAULT_ATTENDANCE_WEIGHT = 20;
    private static final int DEFAULT_INTERACTION_WEIGHT = 10;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private GradeWeightMapper gradeWeightMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    @Transactional(readOnly = true)
    public CourseGradeBookDTO getCourseGradeBook(Long classId) {
        CourseGradeBookDTO book = new CourseGradeBookDTO();
        book.setClass_id(classId);
        CourseGradeWeightDTO weights = resolveWeights(classId);
        book.setWeights(weights);
        List<CourseMember> students = courseMapper.getCourseStudents(classId);
        Integer totalSessions = attendanceMapper.countSessionsByClassId(classId);
        int sessionCount = totalSessions == null ? 0 : totalSessions;
        List<StudentGradeRowDTO> rows = new ArrayList<>();
        for (CourseMember student : students) {
            StudentGradeRowDTO row = new StudentGradeRowDTO();
            row.setAccount(student.getAccount());
            row.setName(student.getName());
            Double hw = gradeMapper.avgHomeworkScore(classId, student.getAccount());
            Double test = gradeMapper.avgTestScorePercent(classId, student.getAccount());
            Integer interaction = gradeMapper.countInteractionParticipation(classId, student.getAccount());
            Integer attended = attendanceMapper.countStudentAttendanceSessions(classId, student.getAccount());
            int hwScore = hw == null ? 0 : (int) Math.round(hw);
            int testScore = test == null ? 0 : (int) Math.round(test);
            int attendRate = sessionCount == 0 ? 100 : (int) Math.round((attended == null ? 0 : attended) * 100.0 / sessionCount);
            int interactionCount = interaction == null ? 0 : interaction;
            row.setHomework_avg(hwScore);
            row.setTest_avg(testScore);
            row.setAttendance_rate(Math.min(100, attendRate));
            row.setInteraction_count(interactionCount);
            row.setComposite_score(calcCompositeScore(hwScore, testScore, attendRate, interactionCount, weights));
            rows.add(row);
        }
        book.setRows(rows);
        book.setStudent_count(rows.size());
        return book;
    }

    @Override
    @Transactional(readOnly = true)
    public CourseGradeWeightDTO getCourseGradeWeight(Long classId) {
        return resolveWeights(classId);
    }

    @Override
    public boolean updateCourseGradeWeight(UpdateGradeWeightRequest request) {
        if (request == null || request.getClass_id() == null || request.getTeacher_account() == null) {
            return false;
        }
        if (!canManageCourseGrades(request.getTeacher_account(), request.getClass_id())) {
            return false;
        }
        CourseGradeWeightDTO weights = normalizeWeights(
                request.getHomework_weight(),
                request.getTest_weight(),
                request.getAttendance_weight(),
                request.getInteraction_weight()
        );
        if (weights == null) {
            return false;
        }
        CourseGradeWeight entity = new CourseGradeWeight();
        entity.setCourse_id(request.getClass_id());
        entity.setHomework_weight(weights.getHomework_weight());
        entity.setTest_weight(weights.getTest_weight());
        entity.setAttendance_weight(weights.getAttendance_weight());
        entity.setInteraction_weight(weights.getInteraction_weight());
        return gradeWeightMapper.upsert(entity);
    }

    private CourseGradeWeightDTO resolveWeights(Long classId) {
        CourseGradeWeight stored = gradeWeightMapper.getByCourseId(classId);
        if (stored == null) {
            return defaultWeights();
        }
        CourseGradeWeightDTO dto = new CourseGradeWeightDTO(
                stored.getHomework_weight(),
                stored.getTest_weight(),
                stored.getAttendance_weight(),
                stored.getInteraction_weight()
        );
        if (sumWeights(dto) != 100) {
            return defaultWeights();
        }
        return dto;
    }

    private CourseGradeWeightDTO defaultWeights() {
        return new CourseGradeWeightDTO(
                DEFAULT_HOMEWORK_WEIGHT,
                DEFAULT_TEST_WEIGHT,
                DEFAULT_ATTENDANCE_WEIGHT,
                DEFAULT_INTERACTION_WEIGHT
        );
    }

    private int calcCompositeScore(int hwScore, int testScore, int attendRate, int interactionCount, CourseGradeWeightDTO weights) {
        int interactionScore = Math.min(100, interactionCount * 10);
        double composite = hwScore * weights.getHomework_weight() / 100.0
                + testScore * weights.getTest_weight() / 100.0
                + attendRate * weights.getAttendance_weight() / 100.0
                + interactionScore * weights.getInteraction_weight() / 100.0;
        return Math.min(100, (int) Math.round(composite));
    }

    private CourseGradeWeightDTO normalizeWeights(Integer homework, Integer test, Integer attendance, Integer interaction) {
        if (homework == null || test == null || attendance == null || interaction == null) {
            return null;
        }
        if (homework < 0 || test < 0 || attendance < 0 || interaction < 0) {
            return null;
        }
        CourseGradeWeightDTO dto = new CourseGradeWeightDTO(homework, test, attendance, interaction);
        if (sumWeights(dto) != 100) {
            return null;
        }
        return dto;
    }

    private int sumWeights(CourseGradeWeightDTO weights) {
        return weights.getHomework_weight()
                + weights.getTest_weight()
                + weights.getAttendance_weight()
                + weights.getInteraction_weight();
    }

    private boolean canManageCourseGrades(String teacherAccount, Long classId) {
        if (teacherAccount == null || classId == null) {
            return false;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(teacherAccount))) {
            return false;
        }
        Course course = courseMapper.getCourseByCourseId(classId);
        if (course == null) {
            return false;
        }
        if (teacherAccount.equals(course.getTeacher_account())) {
            return true;
        }
        Integer inCourse = courseMapper.countAccountInCourse(teacherAccount, classId);
        return inCourse != null && inCourse > 0;
    }
}
