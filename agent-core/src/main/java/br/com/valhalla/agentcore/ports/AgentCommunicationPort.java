package br.com.valhalla.agentcore.ports;

import br.com.valhalla.agentcore.domain.AgentMessage;
import br.com.valhalla.agentcore.domain.valueobjects.AgentConversationId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;
import br.com.valhalla.agentcore.dto.AgentMessageDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Port para comunicação inter-agentes.
 */
public interface AgentCommunicationPort {

    /**
     * Envia uma mensagem de um agente para outro.
     */
    CompletableFuture<Void> sendMessage(AgentMessage message);

    /**
     * Subscreve para mensagens de um agente específico.
     */
    void subscribeToMessages(AgentId agentId, MessageHandler handler);

    /**
     * Publica um evento para todos os agentes inscritos.
     */
    CompletableFuture<Void> publishEvent(String eventType, Object eventData);

    /**
     * Obtém histórico de conversa.
     */
    List<AgentMessageDTO> getConversationHistory(AgentConversationId conversationId);

    /**
     * Handler para mensagens recebidas.
     */
    @FunctionalInterface
    interface MessageHandler {
        void handle(AgentMessage message);
    }
}
