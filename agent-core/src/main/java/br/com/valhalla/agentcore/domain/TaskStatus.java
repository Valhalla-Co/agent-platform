package br.com.valhalla.agentcore.domain;

/**
 * Status possíveis de uma tarefa no sistema de orquestração.
 */
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}
