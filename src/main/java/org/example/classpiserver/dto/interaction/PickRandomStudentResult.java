package org.example.classpiserver.dto.interaction;

public class PickRandomStudentResult {
    private PickItemDTO picked;
    private Boolean no_student_left;

    public PickItemDTO getPicked() {
        return picked;
    }

    public void setPicked(PickItemDTO picked) {
        this.picked = picked;
    }

    public Boolean getNo_student_left() {
        return no_student_left;
    }

    public void setNo_student_left(Boolean no_student_left) {
        this.no_student_left = no_student_left;
    }
}
