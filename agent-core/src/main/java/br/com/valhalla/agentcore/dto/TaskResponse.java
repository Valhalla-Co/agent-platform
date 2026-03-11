package br.com.valhalla.agentcore.dto;

public class TaskResponse {
    private String id;
    private String externalKey; // e.g., platform issue id
    private String status;

    public TaskResponse() {}

    public TaskResponse(String id, String externalKey, String status) {
        this.id = id;
        this.externalKey = externalKey;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
