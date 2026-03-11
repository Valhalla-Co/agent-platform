package br.com.valhalla.agentcore.domain.repositories;

import br.com.valhalla.agentcore.domain.AgentConversation;
import br.com.valhalla.agentcore.domain.shared.TenantId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentConversationId;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

import java.util.List;
import java.util.Optional;

/**
 * Port de repositório para o aggregate AgentConversation.
 */
public interface AgentConversationRepository {

    AgentConversation save(AgentConversation conversation);

    Optional<AgentConversation> findById(AgentConversationId conversationId);

    Optional<AgentConversation> findByTaskId(TaskId taskId);

    List<AgentConversation> findByTenant(TenantId tenantId);

    List<AgentConversation> findActiveByTenant(TenantId tenantId);

    void delete(AgentConversationId conversationId);
}
