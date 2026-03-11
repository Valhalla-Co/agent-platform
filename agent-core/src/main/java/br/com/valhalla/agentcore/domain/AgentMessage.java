package br.com.valhalla.agentcore.domain;

import br.com.valhalla.agentcore.domain.valueobjects.AgentConversationId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AgentMessage Entity.
 * Representa uma mensagem trocada entre agentes.
 * Framework-agnostic.
 */
public class AgentMessage {

    private String id;
    private AgentConversationId conversationId;
    private AgentId senderId;
    private AgentId recipientId;
    private AgentMessageType messageType;
    private String content;
    private Map<String, Object> metadata;
    private Instant sentAt;
    private Instant receivedAt;
    private boolean delivered;

    private AgentMessage() {
        this.metadata = new HashMap<>();
    }

    public static AgentMessage create(
            AgentConversationId conversationId,
            AgentId senderId,
            AgentId recipientId,
            AgentMessageType messageType,
            String content) {
        AgentMessage message = new AgentMessage();
        message.id = UUID.randomUUID().toString();
        message.conversationId = conversationId;
        message.senderId = senderId;
        message.recipientId = recipientId;
        message.messageType = messageType;
        message.content = content;
        message.sentAt = Instant.now();
        message.delivered = false;
        return message;
    }

    public void markAsDelivered() {
        this.delivered = true;
        this.receivedAt = Instant.now();
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    // --- Getters ---

    public String getId() { return id; }
    public AgentConversationId getConversationId() { return conversationId; }
    public AgentId getSenderId() { return senderId; }
    public AgentId getRecipientId() { return recipientId; }
    public AgentMessageType getMessageType() { return messageType; }
    public String getContent() { return content; }
    public Map<String, Object> getMetadata() { return metadata; }
    public Instant getSentAt() { return sentAt; }
    public Instant getReceivedAt() { return receivedAt; }
    public boolean isDelivered() { return delivered; }

    public enum AgentMessageType {
        REQUEST,
        RESPONSE,
        NOTIFICATION,
        QUESTION,
        ANSWER,
        ERROR
    }
}
