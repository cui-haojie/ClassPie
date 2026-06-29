package org.example.classpiserver.dto.test;

import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.TestQuestion;

import java.util.List;
import java.util.Map;

public class TestDetailDTO {
    private CourseActivity activity;
    private List<TestQuestion> questions;
    private Integer choice_count;
    private Integer short_count;
    private Boolean submitted;
    private Map<Long, String> my_answers;
    private Map<Long, TestAnswerResultDTO> answer_results;
    private Boolean is_teacher;
    private Integer total_score;
    private Integer max_score;
    private Integer auto_score;
    private Integer manual_score;
    private Boolean is_fully_graded;
    private List<TestSubmissionSummaryDTO> submissions;

    public Map<Long, TestAnswerResultDTO> getAnswer_results() {
        return answer_results;
    }

    public void setAnswer_results(Map<Long, TestAnswerResultDTO> answer_results) {
        this.answer_results = answer_results;
    }

    public Integer getTotal_score() {
        return total_score;
    }

    public void setTotal_score(Integer total_score) {
        this.total_score = total_score;
    }

    public Integer getMax_score() {
        return max_score;
    }

    public void setMax_score(Integer max_score) {
        this.max_score = max_score;
    }

    public Integer getAuto_score() {
        return auto_score;
    }

    public void setAuto_score(Integer auto_score) {
        this.auto_score = auto_score;
    }

    public Integer getManual_score() {
        return manual_score;
    }

    public void setManual_score(Integer manual_score) {
        this.manual_score = manual_score;
    }

    public Boolean getIs_fully_graded() {
        return is_fully_graded;
    }

    public void setIs_fully_graded(Boolean is_fully_graded) {
        this.is_fully_graded = is_fully_graded;
    }

    public List<TestSubmissionSummaryDTO> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<TestSubmissionSummaryDTO> submissions) {
        this.submissions = submissions;
    }

    public CourseActivity getActivity() {
        return activity;
    }

    public void setActivity(CourseActivity activity) {
        this.activity = activity;
    }

    public List<TestQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TestQuestion> questions) {
        this.questions = questions;
    }

    public Integer getChoice_count() {
        return choice_count;
    }

    public void setChoice_count(Integer choice_count) {
        this.choice_count = choice_count;
    }

    public Integer getShort_count() {
        return short_count;
    }

    public void setShort_count(Integer short_count) {
        this.short_count = short_count;
    }

    public Boolean getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public Map<Long, String> getMy_answers() {
        return my_answers;
    }

    public void setMy_answers(Map<Long, String> my_answers) {
        this.my_answers = my_answers;
    }

    public Boolean getIs_teacher() {
        return is_teacher;
    }

    public void setIs_teacher(Boolean is_teacher) {
        this.is_teacher = is_teacher;
    }
}
