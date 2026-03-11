package br.com.valhalla.agentcore.ports;

import br.com.valhalla.agentcore.domain.shared.DomainEvent;

/**
 * Port para publicação de eventos de domínio.
 */
public interface EventPublisher {

    /**
     * Publica um evento de domínio.
     */
    void publish(DomainEvent event);
}
