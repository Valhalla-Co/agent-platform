package br.com.valhalla.agentcore.dto;

import br.com.valhalla.agentcore.domain.AgentType;
import br.com.valhalla.agentcore.domain.TaskStatus;

import java.time.Instant;
import java.util.Map;

/**
 * DTO para respostas de tarefa na orquestração.
 */
public record OrchestrationTaskResponse(
    String taskId,
    TaskStatus status,
    AgentType assignedAgent,
    Instant estimatedCompletion,
    Map<String, Object> result,
    String errorMessage
) {
}
