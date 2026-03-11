package br.com.valhalla.agentcore.domain.events;

import br.com.valhalla.agentcore.domain.shared.DomainEvent;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

/**
 * Evento publicado quando uma tarefa falha.
 */
public class TaskFailed extends DomainEvent {

    private final TaskId taskId;
    private final String errorMessage;

    public TaskFailed(TaskId taskId, String errorMessage) {
        super();
        this.taskId = taskId;
        this.errorMessage = errorMessage;
    }

    public TaskId getTaskId() { return taskId; }
    public String getErrorMessage() { return errorMessage; }

    @Override
    public String getEventType() {
        return "TaskFailed";
    }
}
