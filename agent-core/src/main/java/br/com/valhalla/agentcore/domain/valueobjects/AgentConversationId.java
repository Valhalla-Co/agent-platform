package br.com.valhalla.agentcore.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representando o identificador de uma AgentConversation.
 */
public class AgentConversationId {

    private final String value;

    private AgentConversationId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AgentConversationId cannot be null or blank");
        }
        this.value = value;
    }

    public static AgentConversationId of(String value) {
        return new AgentConversationId(value);
    }

    public static AgentConversationId generate() {
        return new AgentConversationId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentConversationId that = (AgentConversationId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
