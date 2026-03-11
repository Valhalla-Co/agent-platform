package br.com.valhalla.agentcore.domain.shared;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representando o identificador de um Tenant.
 * Imutável, valida formato UUID. Sem dependências externas.
 */
public class TenantId implements Serializable {

    private final String value;

    private TenantId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TenantId cannot be null or empty");
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("TenantId must be a valid UUID format", e);
        }
        this.value = value;
    }

    public static TenantId of(String value) {
        return new TenantId(value);
    }

    public static TenantId generate() {
        return new TenantId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantId tenantId = (TenantId) o;
        return Objects.equals(value, tenantId.value);
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
