package br.com.valhalla.agentbase.orchestration.usecases;

import br.com.valhalla.agentcore.domain.TaskStatus;
import br.com.valhalla.agentcore.domain.repositories.TaskRepository;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caso de uso para cancelamento de tarefas.
 * Framework-agnostic.
 */
public class CancelTaskUseCase {

    private static final Logger log = LoggerFactory.getLogger(CancelTaskUseCase.class);

    private final TaskRepository taskRepository;

    public CancelTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Cancela uma tarefa em execução.
     */
    public boolean cancel(TaskId taskId) {
        log.info("Tentando cancelar tarefa: {}", taskId);

        return taskRepository.findById(taskId)
            .map(task -> {
                if (task.getStatus() == TaskStatus.COMPLETED) {
                    log.warn("Não é possível cancelar tarefa concluída: {}", taskId);
                    return false;
                }
                task.cancel();
                taskRepository.save(task);
                log.info("Tarefa {} cancelada com sucesso", taskId);
                return true;
            })
            .orElse(false);
    }
}
