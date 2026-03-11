package br.com.valhalla.agentcore.domain;

import br.com.valhalla.agentcore.domain.shared.TenantId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Task Entity.
 * Representa uma tarefa a ser executada por um agente.
 * Framework-agnostic: sem Lombok, sem Spring.
 */
public class Task {

    private TaskId id;
    private TenantId tenantId;
    private TaskType type;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private AgentId assignedAgent;
    private Map<String, Object> context;
    private Map<String, Object> result;
    private String errorMessage;
    private int retryCount;
    private int maxRetries;
    private Instant createdAt;
    private Instant assignedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Duration timeout;

    private Task() {
        this.context = new HashMap<>();
        this.result = new HashMap<>();
    }

    /**
     * Factory method para criar uma nova tarefa.
     */
    public static Task create(
            TenantId tenantId,
            TaskType type,
            String description,
            TaskPriority priority,
            Map<String, Object> context) {
        Task task = new Task();
        task.id = TaskId.generate();
        task.tenantId = tenantId;
        task.type = type;
        task.description = description;
        task.status = TaskStatus.PENDING;
        task.priority = priority;
        task.context = context != null ? new HashMap<>(context) : new HashMap<>();
        task.retryCount = 0;
        task.maxRetries = 3;
        task.createdAt = Instant.now();
        task.timeout = getDefaultTimeout(type);
        return task;
    }

    /**
     * Reconstituição a partir de persistência.
     */
    public static Task reconstitute(
            TaskId id, TenantId tenantId, TaskType type, String description,
            TaskStatus status, TaskPriority priority, AgentId assignedAgent,
            Map<String, Object> context, Map<String, Object> result,
            String errorMessage, int retryCount, int maxRetries,
            Instant createdAt, Instant assignedAt, Instant startedAt,
            Instant completedAt, Duration timeout) {
        Task task = new Task();
        task.id = id;
        task.tenantId = tenantId;
        task.type = type;
        task.description = description;
        task.status = status;
        task.priority = priority;
        task.assignedAgent = assignedAgent;
        task.context = context != null ? new HashMap<>(context) : new HashMap<>();
        task.result = result != null ? new HashMap<>(result) : new HashMap<>();
        task.errorMessage = errorMessage;
        task.retryCount = retryCount;
        task.maxRetries = maxRetries;
        task.createdAt = createdAt;
        task.assignedAt = assignedAt;
        task.startedAt = startedAt;
        task.completedAt = completedAt;
        task.timeout = timeout;
        return task;
    }

    private static Duration getDefaultTimeout(TaskType type) {
        return switch (type) {
            case CODE_ANALYSIS -> Duration.ofMinutes(2);
            case CODE_GENERATION -> Duration.ofMinutes(3);
            case ARCHITECTURE_REVIEW -> Duration.ofMinutes(3);
            case TECHNICAL_DESIGN, SYSTEM_DESIGN -> Duration.ofMinutes(5);
            case PRODUCT_REQUIREMENT -> Duration.ofMinutes(2);
        };
    }

    public void assignTo(AgentId agentId) {
        if (status != TaskStatus.PENDING && status != TaskStatus.FAILED) {
            throw new IllegalStateException(
                "Task " + id + " cannot be assigned. Current status: " + status
            );
        }
        this.assignedAgent = agentId;
        this.assignedAt = Instant.now();
        this.status = TaskStatus.PENDING;
    }

    public void start() {
        if (status != TaskStatus.PENDING) {
            throw new IllegalStateException(
                "Task " + id + " cannot be started. Current status: " + status
            );
        }
        this.status = TaskStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
    }

    public void complete(Map<String, Object> result) {
        if (status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "Task " + id + " cannot be completed. Current status: " + status
            );
        }
        this.status = TaskStatus.COMPLETED;
        this.result = result != null ? new HashMap<>(result) : new HashMap<>();
        this.completedAt = Instant.now();
    }

    public void fail(String errorMessage) {
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
            throw new IllegalStateException(
                "Task " + id + " cannot be failed. Current status: " + status
            );
        }
        this.status = TaskStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }

    public void cancel() {
        if (status == TaskStatus.COMPLETED) {
            throw new IllegalStateException(
                "Task " + id + " is already completed and cannot be cancelled"
            );
        }
        this.status = TaskStatus.CANCELLED;
        this.completedAt = Instant.now();
    }

    public void retry() {
        if (retryCount >= maxRetries) {
            throw new IllegalStateException(
                "Task " + id + " has exceeded maximum retry count: " + maxRetries
            );
        }
        this.retryCount++;
        this.status = TaskStatus.PENDING;
        this.errorMessage = null;
        this.startedAt = null;
    }

    public boolean canRetry() {
        return status == TaskStatus.FAILED && retryCount < maxRetries;
    }

    public boolean isTimedOut() {
        if (status != TaskStatus.IN_PROGRESS || startedAt == null) {
            return false;
        }
        return Duration.between(startedAt, Instant.now()).compareTo(timeout) > 0;
    }

    public Duration getExecutionDuration() {
        if (startedAt == null) {
            return Duration.ZERO;
        }
        Instant endTime = completedAt != null ? completedAt : Instant.now();
        return Duration.between(startedAt, endTime);
    }

    public void addContext(String key, Object value) {
        this.context.put(key, value);
    }

    // --- Getters ---

    public TaskId getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public TaskType getType() { return type; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public AgentId getAssignedAgent() { return assignedAgent; }
    public Map<String, Object> getContext() { return Collections.unmodifiableMap(context); }
    public Map<String, Object> getResult() { return Collections.unmodifiableMap(result); }
    public String getErrorMessage() { return errorMessage; }
    public int getRetryCount() { return retryCount; }
    public int getMaxRetries() { return maxRetries; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public Duration getTimeout() { return timeout; }
}
