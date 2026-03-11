package br.com.valhalla.agentcore.domain.repositories;

import br.com.valhalla.agentcore.domain.Agent;
import br.com.valhalla.agentcore.domain.AgentType;
import br.com.valhalla.agentcore.domain.shared.TenantId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;

import java.util.List;
import java.util.Optional;

/**
 * Port de repositório para o aggregate Agent.
 */
public interface AgentRepository {

    Agent save(Agent agent);

    Optional<Agent> findById(AgentId agentId);

    List<Agent> findByType(AgentType type);

    List<Agent> findByTenantAndType(TenantId tenantId, AgentType type);

    List<Agent> findAvailableByType(AgentType type);

    List<Agent> findByTenant(TenantId tenantId);

    void delete(AgentId agentId);
}
