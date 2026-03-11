package br.com.valhalla.agentcore.domain.repositories;

import br.com.valhalla.agentcore.domain.Task;
import br.com.valhalla.agentcore.domain.TaskStatus;
import br.com.valhalla.agentcore.domain.shared.TenantId;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

import java.util.List;
import java.util.Optional;

/**
 * Port de repositório para a entidade Task.
 */
public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(TaskId taskId);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByTenantAndStatus(TenantId tenantId, TaskStatus status);

    List<Task> findByTenant(TenantId tenantId);

    List<Task> findPendingTasksByPriority(TenantId tenantId);

    void delete(TaskId taskId);
}
