package br.com.valhalla.agentcore.dto;

import java.util.Map;

public class TaskRequest {
    private String id;
    private String title;
    private String description;
    private Map<String, Object> metadata;

    public TaskRequest() {}

    public TaskRequest(String id, String title, String description, Map<String, Object> metadata) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
