package br.com.valhalla.agentcore.dto;

import java.util.Map;

/**
 * DTO contendo dados de contexto de orquestração.
 */
public record OrchestrationContext(
    String tenantId,
    String userId,
    String requestId,
    Map<String, Object> metadata
) {
}
