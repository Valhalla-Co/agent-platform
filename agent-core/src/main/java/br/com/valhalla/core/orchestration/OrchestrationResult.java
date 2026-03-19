package br.com.valhalla.core.orchestration;

import br.com.valhalla.core.agent.AgentResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de uma orquestração.
 */
public class OrchestrationResult {

    private final boolean success;
    private final String message;
    private final List<AgentResult> agentResults;
    private final String error;
    private final LocalDateTime timestamp;
    private final Duration totalExecutionTime;

    private OrchestrationResult(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.agentResults = builder.agentResults;
        this.error = builder.error;
        this.timestamp = LocalDateTime.now();
        this.totalExecutionTime = builder.totalExecutionTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<AgentResult> getAgentResults() {
        return new ArrayList<>(agentResults);
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Duration getTotalExecutionTime() {
        return totalExecutionTime;
    }

    // Builder Pattern
    public static class Builder {
        private boolean success = true;
        private String message;
        private List<AgentResult> agentResults = new ArrayList<>();
        private String error;
        private Duration totalExecutionTime;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder addAgentResult(AgentResult result) {
            this.agentResults.add(result);
            return this;
        }

        public Builder agentResults(List<AgentResult> results) {
            this.agentResults.addAll(results);
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            this.success = false;
            return this;
        }

        public Builder totalExecutionTime(Duration duration) {
            this.totalExecutionTime = duration;
            return this;
        }

        public OrchestrationResult build() {
            return new OrchestrationResult(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
