package br.com.valhalla.agentcore.domain;

import br.com.valhalla.agentcore.domain.shared.TenantId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Agent Aggregate Root.
 * Representa um agente autônomo no sistema de orquestração.
 * Framework-agnostic: sem Lombok, sem Spring.
 */
public class Agent {

    private AgentId id;
    private TenantId tenantId;
    private AgentType type;
    private String name;
    private String description;
    private Set<String> capabilities;
    private AgentStatus status;
    private Map<String, Object> configuration;
    private int maxConcurrentTasks;
    private int currentTaskCount;
    private Instant createdAt;
    private Instant lastActiveAt;

    private Agent() {
        this.capabilities = new HashSet<>();
        this.configuration = new HashMap<>();
    }

    /**
     * Factory method para criar um novo agente.
     */
    public static Agent create(TenantId tenantId, AgentType type, String name, String description) {
        Agent agent = new Agent();
        agent.id = AgentId.generate();
        agent.tenantId = tenantId;
        agent.type = type;
        agent.name = name;
        agent.description = description;
        agent.status = AgentStatus.AVAILABLE;
        agent.maxConcurrentTasks = getDefaultMaxConcurrentTasks(type);
        agent.currentTaskCount = 0;
        agent.createdAt = Instant.now();
        agent.lastActiveAt = Instant.now();
        agent.initializeCapabilities(type);
        return agent;
    }

    /**
     * Reconstituição a partir de persistência.
     */
    public static Agent reconstitute(
            AgentId id, TenantId tenantId, AgentType type, String name,
            String description, Set<String> capabilities, AgentStatus status,
            Map<String, Object> configuration, int maxConcurrentTasks,
            int currentTaskCount, Instant createdAt, Instant lastActiveAt) {
        Agent agent = new Agent();
        agent.id = id;
        agent.tenantId = tenantId;
        agent.type = type;
        agent.name = name;
        agent.description = description;
        agent.capabilities = capabilities != null ? new HashSet<>(capabilities) : new HashSet<>();
        agent.status = status;
        agent.configuration = configuration != null ? new HashMap<>(configuration) : new HashMap<>();
        agent.maxConcurrentTasks = maxConcurrentTasks;
        agent.currentTaskCount = currentTaskCount;
        agent.createdAt = createdAt;
        agent.lastActiveAt = lastActiveAt;
        return agent;
    }

    private static int getDefaultMaxConcurrentTasks(AgentType type) {
        return switch (type) {
            case ORCHESTRATOR -> 10;
            case JAVA_ENGINEER -> 5;
            case ARCHITECT_PRODUCT_OWNER -> 3;
        };
    }

    private void initializeCapabilities(AgentType type) {
        switch (type) {
            case ORCHESTRATOR -> {
                capabilities.add("TASK_ORCHESTRATION");
                capabilities.add("AGENT_COORDINATION");
                capabilities.add("RESULT_AGGREGATION");
            }
            case JAVA_ENGINEER -> {
                capabilities.add("CODE_ANALYSIS");
                capabilities.add("CODE_GENERATION");
                capabilities.add("CODE_REVIEW");
                capabilities.add("REFACTORING");
            }
            case ARCHITECT_PRODUCT_OWNER -> {
                capabilities.add("ARCHITECTURE_REVIEW");
                capabilities.add("SYSTEM_DESIGN");
                capabilities.add("TECHNICAL_DECISIONS");
                capabilities.add("TRADE_OFF_ANALYSIS");
            }
        }
    }

    public boolean canAcceptTask() {
        return status == AgentStatus.AVAILABLE && currentTaskCount < maxConcurrentTasks;
    }

    public void assignTask() {
        if (!canAcceptTask()) {
            throw new IllegalStateException(
                "Agent " + id + " cannot accept more tasks. Status: " + status +
                ", Current tasks: " + currentTaskCount + "/" + maxConcurrentTasks
            );
        }
        currentTaskCount++;
        if (currentTaskCount >= maxConcurrentTasks) {
            status = AgentStatus.BUSY;
        }
        lastActiveAt = Instant.now();
    }

    public void completeTask() {
        if (currentTaskCount > 0) {
            currentTaskCount--;
        }
        if (currentTaskCount < maxConcurrentTasks && status == AgentStatus.BUSY) {
            status = AgentStatus.AVAILABLE;
        }
        lastActiveAt = Instant.now();
    }

    public void markUnavailable() {
        this.status = AgentStatus.UNAVAILABLE;
        this.lastActiveAt = Instant.now();
    }

    public void markAvailable() {
        this.status = AgentStatus.AVAILABLE;
        this.lastActiveAt = Instant.now();
    }

    public void addCapability(String capability) {
        this.capabilities.add(capability);
    }

    public void updateConfiguration(String key, Object value) {
        this.configuration.put(key, value);
        this.lastActiveAt = Instant.now();
    }

    public boolean hasCapability(String capability) {
        return capabilities.contains(capability);
    }

    // --- Getters ---

    public AgentId getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public AgentType getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Set<String> getCapabilities() { return Collections.unmodifiableSet(capabilities); }
    public AgentStatus getStatus() { return status; }
    public Map<String, Object> getConfiguration() { return Collections.unmodifiableMap(configuration); }
    public int getMaxConcurrentTasks() { return maxConcurrentTasks; }
    public int getCurrentTaskCount() { return currentTaskCount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActiveAt() { return lastActiveAt; }
}
