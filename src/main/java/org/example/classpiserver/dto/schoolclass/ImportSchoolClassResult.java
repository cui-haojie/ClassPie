package org.example.classpiserver.dto.schoolclass;

import java.util.ArrayList;
import java.util.List;

public class ImportSchoolClassResult {
    private int created;
    private int skipped;
    private int failed;
    private List<Integer> created_ids = new ArrayList<>();
    private List<String> messages = new ArrayList<>();

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public List<Integer> getCreated_ids() {
        return created_ids;
    }

    public void setCreated_ids(List<Integer> created_ids) {
        this.created_ids = created_ids;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String message) {
        if (message != null && !message.isBlank()) {
            this.messages.add(message);
        }
    }

    public void addCreatedId(Integer id) {
        if (id != null) {
            this.created_ids.add(id);
        }
    }
}
