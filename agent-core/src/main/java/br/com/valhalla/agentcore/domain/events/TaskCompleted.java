package br.com.valhalla.agentcore.domain.events;

import br.com.valhalla.agentcore.domain.shared.DomainEvent;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

/**
 * Evento publicado quando uma tarefa é concluída com sucesso.
 */
public class TaskCompleted extends DomainEvent {

    private final TaskId taskId;

    public TaskCompleted(TaskId taskId) {
        super();
        this.taskId = taskId;
    }

    public TaskId getTaskId() { return taskId; }

    @Override
    public String getEventType() {
        return "TaskCompleted";
    }
}
