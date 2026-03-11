package br.com.valhalla.agentcore.domain.events;

import br.com.valhalla.agentcore.domain.shared.DomainEvent;
import br.com.valhalla.agentcore.domain.valueobjects.AgentConversationId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;

import java.util.Set;

/**
 * Evento publicado quando uma colaboração entre agentes é iniciada.
 */
public class AgentCollaborationStarted extends DomainEvent {

    private final AgentConversationId conversationId;
    private final Set<AgentId> participants;

    public AgentCollaborationStarted(AgentConversationId conversationId, Set<AgentId> participants) {
        super();
        this.conversationId = conversationId;
        this.participants = participants;
    }

    public AgentConversationId getConversationId() { return conversationId; }
    public Set<AgentId> getParticipants() { return participants; }

    @Override
    public String getEventType() {
        return "AgentCollaborationStarted";
    }
}
