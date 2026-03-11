package br.com.valhalla.agentcore.domain.events;

import br.com.valhalla.agentcore.domain.TaskPriority;
import br.com.valhalla.agentcore.domain.TaskType;
import br.com.valhalla.agentcore.domain.shared.DomainEvent;
import br.com.valhalla.agentcore.domain.shared.TenantId;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

/**
 * Evento publicado quando uma tarefa é criada.
 */
public class TaskCreated extends DomainEvent {

    private final TaskId taskId;
    private final TenantId tenantId;
    private final TaskType taskType;
    private final TaskPriority priority;
    private final String description;

    public TaskCreated(TaskId taskId, TenantId tenantId, TaskType taskType,
                       TaskPriority priority, String description) {
        super();
        this.taskId = taskId;
        this.tenantId = tenantId;
        this.taskType = taskType;
        this.priority = priority;
        this.description = description;
    }

    public TaskId getTaskId() { return taskId; }
    public TenantId getTenantId() { return tenantId; }
    public TaskType getTaskType() { return taskType; }
    public TaskPriority getPriority() { return priority; }
    public String getDescription() { return description; }

    @Override
    public String getEventType() {
        return "TaskCreated";
    }
}
