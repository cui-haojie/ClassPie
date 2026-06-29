package org.example.classpiserver.dto.ai;

public class AiStatusDTO {
    private Boolean configured;
    private String model;

    public Boolean getConfigured() {
        return configured;
    }

    public void setConfigured(Boolean configured) {
        this.configured = configured;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
