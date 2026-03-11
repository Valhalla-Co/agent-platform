package br.com.valhalla.agentcore.domain;

import br.com.valhalla.agentcore.domain.shared.TenantId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentConversationId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;
import br.com.valhalla.agentcore.domain.valueobjects.TaskId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AgentConversation Aggregate Root.
 * Gerencia comunicação entre agentes durante execução de tarefas.
 * Framework-agnostic.
 */
public class AgentConversation {

    private AgentConversationId id;
    private TenantId tenantId;
    private TaskId taskId;
    private Set<AgentId> participants;
    private List<AgentMessage> messages;
    private ConversationStatus status;
    private Map<String, Object> context;
    private Instant startedAt;
    private Instant lastMessageAt;
    private Instant closedAt;

    private AgentConversation() {
        this.participants = new HashSet<>();
        this.messages = new ArrayList<>();
        this.context = new HashMap<>();
    }

    public static AgentConversation create(TenantId tenantId, TaskId taskId, Set<AgentId> participants) {
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Conversation must have at least one participant");
        }
        AgentConversation conversation = new AgentConversation();
        conversation.id = AgentConversationId.generate();
        conversation.tenantId = tenantId;
        conversation.taskId = taskId;
        conversation.participants = new HashSet<>(participants);
        conversation.status = ConversationStatus.ACTIVE;
        conversation.startedAt = Instant.now();
        conversation.lastMessageAt = Instant.now();
        return conversation;
    }

    /**
     * Reconstituição a partir de persistência.
     */
    public static AgentConversation reconstitute(
            AgentConversationId id, TenantId tenantId, TaskId taskId,
            Set<AgentId> participants, List<AgentMessage> messages,
            ConversationStatus status, Map<String, Object> context,
            Instant startedAt, Instant lastMessageAt, Instant closedAt) {
        AgentConversation conversation = new AgentConversation();
        conversation.id = id;
        conversation.tenantId = tenantId;
        conversation.taskId = taskId;
        conversation.participants = participants != null ? new HashSet<>(participants) : new HashSet<>();
        conversation.messages = messages != null ? new ArrayList<>(messages) : new ArrayList<>();
        conversation.status = status;
        conversation.context = context != null ? new HashMap<>(context) : new HashMap<>();
        conversation.startedAt = startedAt;
        conversation.lastMessageAt = lastMessageAt;
        conversation.closedAt = closedAt;
        return conversation;
    }

    public void addMessage(AgentMessage message) {
        if (status != ConversationStatus.ACTIVE) {
            throw new IllegalStateException(
                "Cannot add message to conversation " + id + ". Status: " + status
            );
        }
        if (!participants.contains(message.getSenderId())) {
            throw new IllegalArgumentException(
                "Sender " + message.getSenderId() + " is not a participant in conversation " + id
            );
        }
        if (message.getRecipientId() != null && !participants.contains(message.getRecipientId())) {
            throw new IllegalArgumentException(
                "Recipient " + message.getRecipientId() + " is not a participant in conversation " + id
            );
        }
        this.messages.add(message);
        this.lastMessageAt = Instant.now();
    }

    public void addParticipant(AgentId agentId) {
        if (status == ConversationStatus.CLOSED) {
            throw new IllegalStateException(
                "Cannot add participant to closed conversation " + id
            );
        }
        this.participants.add(agentId);
    }

    public void close() {
        if (status == ConversationStatus.CLOSED) {
            throw new IllegalStateException("Conversation " + id + " is already closed");
        }
        this.status = ConversationStatus.CLOSED;
        this.closedAt = Instant.now();
    }

    public List<AgentMessage> getMessagesBetween(AgentId agent1, AgentId agent2) {
        return messages.stream()
            .filter(msg ->
                (msg.getSenderId().equals(agent1) && msg.getRecipientId().equals(agent2)) ||
                (msg.getSenderId().equals(agent2) && msg.getRecipientId().equals(agent1))
            )
            .toList();
    }

    public List<AgentMessage> getLastMessages(int count) {
        int size = messages.size();
        int fromIndex = Math.max(0, size - count);
        return Collections.unmodifiableList(new ArrayList<>(messages.subList(fromIndex, size)));
    }

    public void addContext(String key, Object value) {
        this.context.put(key, value);
    }

    public int getMessageCount() {
        return messages.size();
    }

    // --- Getters ---

    public AgentConversationId getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public TaskId getTaskId() { return taskId; }
    public Set<AgentId> getParticipants() { return Collections.unmodifiableSet(participants); }
    public List<AgentMessage> getMessages() { return Collections.unmodifiableList(messages); }
    public ConversationStatus getStatus() { return status; }
    public Map<String, Object> getContext() { return Collections.unmodifiableMap(context); }
    public Instant getStartedAt() { return startedAt; }
    public Instant getLastMessageAt() { return lastMessageAt; }
    public Instant getClosedAt() { return closedAt; }

    public enum ConversationStatus {
        ACTIVE,
        CLOSED
    }
}
