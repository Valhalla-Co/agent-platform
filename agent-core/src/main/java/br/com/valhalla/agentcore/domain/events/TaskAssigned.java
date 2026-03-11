package br.com.valhalla.agentcore.domain.events;

import br.com.valhalla.agentcore.domain.shared.DomainEvent;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

/**
 * Evento publicado quando uma tarefa é atribuída a um agente.
 */
public class TaskAssigned extends DomainEvent {

    private final TaskId taskId;
    private final AgentId agentId;

    public TaskAssigned(TaskId taskId, AgentId agentId) {
        super();
        this.taskId = taskId;
        this.agentId = agentId;
    }

    public TaskId getTaskId() { return taskId; }
    public AgentId getAgentId() { return agentId; }

    @Override
    public String getEventType() {
        return "TaskAssigned";
    }
}
