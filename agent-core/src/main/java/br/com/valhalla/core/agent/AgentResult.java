package br.com.valhalla.core.agent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Resultado da execução de um agente.
 */
public class AgentResult {
    private final boolean success;
    private final String message;
    private final String agentName;
    private final Map<String, Object> data;
    private final String error;
    private final LocalDateTime timestamp;
    private final Duration executionTime;

    private AgentResult(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.agentName = builder.agentName;
        this.data = new HashMap<>(builder.data);
        this.error = builder.error;
        this.executionTime = builder.executionTime;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAgentName() {
        return agentName;
    }

    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Duration getExecutionTime() {
        return executionTime;
    }

    // Builder Pattern
    public static class Builder {
        private boolean success;
        private String message;
        private String agentName;
        private Map<String, Object> data = new HashMap<>();
        private String error;
        private Duration executionTime;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder agentName(String agentName) {
            this.agentName = agentName;
            return this;
        }

        public Builder data(String key, Object value) {
            this.data.put(key, value);
            return this;
        }

        public Builder data(Map<String, Object> data) {
            if (data != null) {
                this.data.putAll(data);
            }
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            this.success = false;
            return this;
        }

        public Builder executionTime(Duration executionTime) {
            this.executionTime = executionTime;
            return this;
        }

        public AgentResult build() {
            return new AgentResult(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AgentResult success(String agentName, String message) {
        return builder()
                .success(true)
                .agentName(agentName)
                .message(message)
                .build();
    }

    public static AgentResult error(String agentName, String error) {
        return builder()
                .success(false)
                .agentName(agentName)
                .error(error)
                .build();
    }
}
