package org.example.classpiserver.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportStudentResult {
    private int created;
    private int linked;
    private int skipped;
    private int failed;
    private List<String> messages = new ArrayList<>();

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getLinked() {
        return linked;
    }

    public void setLinked(int linked) {
        this.linked = linked;
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
}
