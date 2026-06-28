package org.example.classpiserver.dto;

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
    private Boolean is_teacher;

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
