package br.com.valhalla.agentcore.dto;

import br.com.valhalla.agentcore.domain.TaskPriority;
import br.com.valhalla.agentcore.domain.TaskType;

import java.util.Map;

/**
 * DTO para requisições de criação de tarefa na orquestração.
 * (Versão rica, diferente do TaskRequest POJO legado usado pelo CLI)
 */
public record OrchestrationTaskRequest(
    TaskType type,
    String description,
    TaskPriority priority,
    Map<String, Object> context
) {
}
