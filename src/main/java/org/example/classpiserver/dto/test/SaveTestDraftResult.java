package org.example.classpiserver.dto.test;

public class SaveTestDraftResult {
    private Long activity_id;
    private boolean ok;

    public SaveTestDraftResult() {
    }

    public SaveTestDraftResult(Long activity_id, boolean ok) {
        this.activity_id = activity_id;
        this.ok = ok;
    }

    public Long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Long activity_id) {
        this.activity_id = activity_id;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
