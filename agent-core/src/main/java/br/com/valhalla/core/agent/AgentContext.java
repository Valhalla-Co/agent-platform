package br.com.valhalla.core.agent;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Contexto de execução para um agente.
 * Contém todas as informações necessárias para executar uma tarefa.
 */
public class AgentContext {
    private final String task;
    private final Path workingDirectory;
    private final Map<String, Object> parameters;
    private final String requestedBy;

    private AgentContext(Builder builder) {
        this.task = builder.task;
        this.workingDirectory = builder.workingDirectory;
        this.parameters = new HashMap<>(builder.parameters);
        this.requestedBy = builder.requestedBy;
    }

    public String getTask() {
        return task;
    }

    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }

    public Object getParameter(String key) {
        return parameters.get(key);
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    // Builder Pattern
    public static class Builder {
        private String task;
        private Path workingDirectory;
        private Map<String, Object> parameters = new HashMap<>();
        private String requestedBy;

        public Builder task(String task) {
            this.task = task;
            return this;
        }

        public Builder workingDirectory(Path workingDirectory) {
            this.workingDirectory = workingDirectory;
            return this;
        }

        public Builder parameter(String key, Object value) {
            this.parameters.put(key, value);
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            if (parameters != null) {
                this.parameters.putAll(parameters);
            }
            return this;
        }

        public Builder requestedBy(String requestedBy) {
            this.requestedBy = requestedBy;
            return this;
        }

        public AgentContext build() {
            if (task == null || task.isBlank()) {
                throw new IllegalStateException("Task is required");
            }
            return new AgentContext(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
