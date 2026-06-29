package org.example.classpiserver.dto.interaction;

import org.example.classpiserver.entity.CourseActivity;

import java.util.List;

public class InteractionDetailDTO {
    private CourseActivity activity;
    private Boolean is_teacher;
    private String status;
    private Integer current_round;
    private String current_question;
    private Boolean participated;
    private String my_content;
    private Integer participant_count;
    private Integer answer_count;
    private Integer student_count;
    private Boolean i_was_picked;
    private List<QaAnswerItemDTO> answers;
    private List<PickItemDTO> picks;
    private PickItemDTO latest_pick;
    private String interaction_kind;
    private java.util.List<String> vote_options;
    private java.util.List<VoteStatDTO> vote_stats;
    private Integer my_option_index;
    private Boolean race_open;
    private java.util.List<QaAnswerItemDTO> race_results;

    public CourseActivity getActivity() {
        return activity;
    }

    public void setActivity(CourseActivity activity) {
        this.activity = activity;
    }

    public Boolean getIs_teacher() {
        return is_teacher;
    }

    public void setIs_teacher(Boolean is_teacher) {
        this.is_teacher = is_teacher;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCurrent_round() {
        return current_round;
    }

    public void setCurrent_round(Integer current_round) {
        this.current_round = current_round;
    }

    public String getCurrent_question() {
        return current_question;
    }

    public void setCurrent_question(String current_question) {
        this.current_question = current_question;
    }

    public Boolean getParticipated() {
        return participated;
    }

    public void setParticipated(Boolean participated) {
        this.participated = participated;
    }

    public String getMy_content() {
        return my_content;
    }

    public void setMy_content(String my_content) {
        this.my_content = my_content;
    }

    public Integer getParticipant_count() {
        return participant_count;
    }

    public void setParticipant_count(Integer participant_count) {
        this.participant_count = participant_count;
    }

    public Integer getAnswer_count() {
        return answer_count;
    }

    public void setAnswer_count(Integer answer_count) {
        this.answer_count = answer_count;
    }

    public Integer getStudent_count() {
        return student_count;
    }

    public void setStudent_count(Integer student_count) {
        this.student_count = student_count;
    }

    public Boolean getI_was_picked() {
        return i_was_picked;
    }

    public void setI_was_picked(Boolean i_was_picked) {
        this.i_was_picked = i_was_picked;
    }

    public List<QaAnswerItemDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QaAnswerItemDTO> answers) {
        this.answers = answers;
    }

    public List<PickItemDTO> getPicks() {
        return picks;
    }

    public void setPicks(List<PickItemDTO> picks) {
        this.picks = picks;
    }

    public PickItemDTO getLatest_pick() {
        return latest_pick;
    }

    public void setLatest_pick(PickItemDTO latest_pick) {
        this.latest_pick = latest_pick;
    }

    public String getInteraction_kind() { return interaction_kind; }
    public void setInteraction_kind(String interaction_kind) { this.interaction_kind = interaction_kind; }
    public java.util.List<String> getVote_options() { return vote_options; }
    public void setVote_options(java.util.List<String> vote_options) { this.vote_options = vote_options; }
    public java.util.List<VoteStatDTO> getVote_stats() { return vote_stats; }
    public void setVote_stats(java.util.List<VoteStatDTO> vote_stats) { this.vote_stats = vote_stats; }
    public Integer getMy_option_index() { return my_option_index; }
    public void setMy_option_index(Integer my_option_index) { this.my_option_index = my_option_index; }
    public Boolean getRace_open() { return race_open; }
    public void setRace_open(Boolean race_open) { this.race_open = race_open; }
    public java.util.List<QaAnswerItemDTO> getRace_results() { return race_results; }
    public void setRace_results(java.util.List<QaAnswerItemDTO> race_results) { this.race_results = race_results; }
}
