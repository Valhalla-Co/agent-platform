package br.com.valhalla.agentcore.domain.shared;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Classe base para todos os eventos de domínio.
 * Segue o padrão DDD para publicação de eventos.
 * Framework-agnostic: sem dependências externas.
 */
public abstract class DomainEvent implements Serializable {

    private final String eventId;
    private final Instant occurredAt;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    /**
     * Retorna o tipo do evento (usado para roteamento/serialização).
     */
    public abstract String getEventType();
}
