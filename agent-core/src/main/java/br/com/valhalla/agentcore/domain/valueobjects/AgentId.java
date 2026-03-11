package br.com.valhalla.agentcore.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representando o identificador de um Agent.
 */
public class AgentId {

    private final String value;

    private AgentId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AgentId cannot be null or blank");
        }
        this.value = value;
    }

    public static AgentId of(String value) {
        return new AgentId(value);
    }

    public static AgentId generate() {
        return new AgentId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentId agentId = (AgentId) o;
        return Objects.equals(value, agentId.value);
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
