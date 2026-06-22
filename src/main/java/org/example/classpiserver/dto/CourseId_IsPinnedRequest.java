package org.example.classpiserver.dto;

public class CourseId_IsPinnedRequest {
    private Long id;
    private boolean is_pinned;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isIs_pinned() {
        return is_pinned;
    }

    public void setIs_pinned(boolean is_pinned) {
        this.is_pinned = is_pinned;
    }
}
