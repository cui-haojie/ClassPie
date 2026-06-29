package org.example.classpiserver.dto.test;

public class TestSubmissionSummaryDTO {
    private Long submission_id;
    private String account;
    private String account_name;
    private Integer auto_score;
    private Integer manual_score;
    private Integer total_score;
    private Integer max_score;
    private Boolean is_fully_graded;
    private String submit_time;

    public Long getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(Long submission_id) {
        this.submission_id = submission_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
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

    public Boolean getIs_fully_graded() {
        return is_fully_graded;
    }

    public void setIs_fully_graded(Boolean is_fully_graded) {
        this.is_fully_graded = is_fully_graded;
    }

    public String getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(String submit_time) {
        this.submit_time = submit_time;
    }
}
