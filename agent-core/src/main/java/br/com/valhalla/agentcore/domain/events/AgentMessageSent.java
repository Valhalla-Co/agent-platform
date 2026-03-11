package br.com.valhalla.agentcore.domain.events;

import br.com.valhalla.agentcore.domain.AgentMessage;
import br.com.valhalla.agentcore.domain.shared.DomainEvent;

/**
 * Evento publicado quando um agente envia uma mensagem.
 */
public class AgentMessageSent extends DomainEvent {

    private final AgentMessage message;

    public AgentMessageSent(AgentMessage message) {
        super();
        this.message = message;
    }

    public AgentMessage getMessage() { return message; }

    @Override
    public String getEventType() {
        return "AgentMessageSent";
    }
}
