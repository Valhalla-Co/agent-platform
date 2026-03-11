package br.com.valhalla.agentbase.orchestration.usecases;

import br.com.valhalla.agentcore.domain.Task;
import br.com.valhalla.agentcore.domain.TaskStatus;
import br.com.valhalla.agentcore.domain.repositories.TaskRepository;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Caso de uso para monitorar progresso de tarefas.
 * Framework-agnostic.
 */
public class MonitorTaskProgressUseCase {

    private static final Logger log = LoggerFactory.getLogger(MonitorTaskProgressUseCase.class);

    private final TaskRepository taskRepository;

    public MonitorTaskProgressUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Obtém o status atual de uma tarefa.
     */
    public Optional<TaskStatus> getTaskStatus(TaskId taskId) {
        log.debug("Verificando status da tarefa: {}", taskId);
        return taskRepository.findById(taskId).map(Task::getStatus);
    }

    /**
     * Verifica se uma tarefa excedeu o timeout.
     */
    public boolean checkTimeout(TaskId taskId) {
        return taskRepository.findById(taskId)
            .map(task -> {
                if (task.isTimedOut()) {
                    log.warn("Tarefa {} excedeu timeout", taskId);
                    task.fail("Timeout de execução da tarefa excedido");
                    taskRepository.save(task);
                    return true;
                }
                return false;
            })
            .orElse(false);
    }

    /**
     * Tenta reexecutar uma tarefa falhada.
     */
    public boolean retryTask(TaskId taskId) {
        return taskRepository.findById(taskId)
            .map(task -> {
                if (task.canRetry()) {
                    log.info("Reexecutando tarefa: {}", taskId);
                    task.retry();
                    taskRepository.save(task);
                    return true;
                }
                log.warn("Tarefa {} não pode ser reexecutada", taskId);
                return false;
            })
            .orElse(false);
    }
}
