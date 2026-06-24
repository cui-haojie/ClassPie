package org.example.classpiserver.entity;

public class Homework {
    private int homework_id;
    private String submitter;
    private int content_id;
    private String name;
    private String deadline;
    private boolean isEnd;
    private String type;
    private boolean isCorrect;
    private int score;
    private String details;

    private int graded_count;
    private int ungraded_count;
    private int unsubmitted_count;

    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getHomework_id() {
        return homework_id;
    }

    public void setHomework_id(int homework_id) {
        this.homework_id = homework_id;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGraded_count() {
        return graded_count;
    }

    public void setGraded_count(int graded_count) {
        this.graded_count = graded_count;
    }

    public int getUngraded_count() {
        return ungraded_count;
    }

    public void setUngraded_count(int ungraded_count) {
        this.ungraded_count = ungraded_count;
    }

    public int getUnsubmitted_count() {
        return unsubmitted_count;
    }

    public void setUnsubmitted_count(int unsubmitted_count) {
        this.unsubmitted_count = unsubmitted_count;
    }
}
