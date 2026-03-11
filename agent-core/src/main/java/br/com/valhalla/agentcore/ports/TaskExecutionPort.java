package br.com.valhalla.agentcore.ports;

import br.com.valhalla.agentcore.domain.Task;
import br.com.valhalla.agentcore.domain.TaskStatus;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Port para execução de tarefas por agentes.
 */
public interface TaskExecutionPort {

    /**
     * Executa uma tarefa de forma assíncrona.
     */
    CompletableFuture<Map<String, Object>> executeTask(Task task);

    /**
     * Verifica o status de uma tarefa em execução.
     */
    TaskStatus checkTaskStatus(TaskId taskId);

    /**
     * Cancela uma tarefa em execução.
     */
    CompletableFuture<Void> cancelTask(TaskId taskId);
}
