package br.com.valhalla.agentcore.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO para resultados padronizados de tarefas de agentes.
 */
public record AgentTaskResult(
    boolean success,
    Map<String, Object> data,
    List<String> errors,
    List<String> suggestions,
    List<String> nextSteps
) {
}
