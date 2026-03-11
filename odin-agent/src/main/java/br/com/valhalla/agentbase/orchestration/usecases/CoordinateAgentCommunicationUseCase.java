package br.com.valhalla.agentbase.orchestration.usecases;

import br.com.valhalla.agentcore.domain.*;
import br.com.valhalla.agentcore.domain.events.AgentCollaborationStarted;
import br.com.valhalla.agentcore.domain.events.AgentMessageSent;
import br.com.valhalla.agentcore.domain.repositories.AgentConversationRepository;
import br.com.valhalla.agentcore.domain.repositories.AgentRepository;
import br.com.valhalla.agentcore.domain.valueobjects.AgentConversationId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;
import br.com.valhalla.agentcore.ports.AgentCommunicationPort;
import br.com.valhalla.agentcore.ports.EventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso para coordenar comunicação entre agentes.
 * Framework-agnostic.
 */
public class CoordinateAgentCommunicationUseCase {

    private static final Logger log = LoggerFactory.getLogger(CoordinateAgentCommunicationUseCase.class);

    private final AgentConversationRepository conversationRepository;
    private final AgentRepository agentRepository;
    private final AgentCommunicationPort communicationPort;
    private final EventPublisher eventPublisher;

    public CoordinateAgentCommunicationUseCase(
            AgentConversationRepository conversationRepository,
            AgentRepository agentRepository,
            AgentCommunicationPort communicationPort,
            EventPublisher eventPublisher) {
        this.conversationRepository = conversationRepository;
        this.agentRepository = agentRepository;
        this.communicationPort = communicationPort;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Inicia colaboração entre múltiplos agentes para uma tarefa.
     */
    public CompletableFuture<AgentConversationId> startCollaboration(Task task) {
        log.info("Iniciando colaboração de agentes para tarefa: {}", task.getId());

        Set<AgentId> participants = determineRequiredAgents(task);

        AgentConversation conversation = AgentConversation.create(
            task.getTenantId(), task.getId(), participants
        );
        conversation = conversationRepository.save(conversation);

        eventPublisher.publish(new AgentCollaborationStarted(
            conversation.getId(), participants
        ));

        return CompletableFuture.completedFuture(conversation.getId());
    }

    /**
     * Envia mensagem entre agentes em uma conversa.
     */
    public CompletableFuture<Void> sendMessage(
            AgentConversationId conversationId,
            AgentId senderId,
            AgentId recipientId,
            String content) {

        log.info("Enviando mensagem na conversa {} de {} para {}",
            conversationId, senderId, recipientId);

        return conversationRepository.findById(conversationId)
            .map(conversation -> {
                AgentMessage message = AgentMessage.create(
                    conversationId, senderId, recipientId,
                    AgentMessage.AgentMessageType.REQUEST, content
                );
                conversation.addMessage(message);
                conversationRepository.save(conversation);

                eventPublisher.publish(new AgentMessageSent(message));

                return communicationPort.sendMessage(message);
            })
            .orElseGet(() -> CompletableFuture.failedFuture(
                new IllegalArgumentException("Conversa não encontrada: " + conversationId)
            ));
    }

    /**
     * Agrega resultados de múltiplos agentes.
     */
    public CompletableFuture<Map<String, Object>> aggregateResults(
            AgentConversationId conversationId,
            Map<String, Object> agentResults) {

        log.info("Agregando resultados para conversa: {}", conversationId);

        return conversationRepository.findById(conversationId)
            .map(conversation -> {
                conversation.close();
                conversationRepository.save(conversation);
                return CompletableFuture.completedFuture(agentResults);
            })
            .orElseGet(() -> CompletableFuture.failedFuture(
                new IllegalArgumentException("Conversa não encontrada: " + conversationId)
            ));
    }

    private Set<AgentId> determineRequiredAgents(Task task) {
        Set<AgentId> agents = new HashSet<>();

        agentRepository.findAvailableByType(AgentType.ORCHESTRATOR)
            .stream().findFirst()
            .ifPresent(agent -> agents.add(agent.getId()));

        switch (task.getType()) {
            case CODE_ANALYSIS, CODE_GENERATION ->
                agentRepository.findAvailableByType(AgentType.JAVA_ENGINEER)
                    .stream().findFirst()
                    .ifPresent(agent -> agents.add(agent.getId()));
            case ARCHITECTURE_REVIEW, TECHNICAL_DESIGN, SYSTEM_DESIGN, PRODUCT_REQUIREMENT ->
                agentRepository.findAvailableByType(AgentType.ARCHITECT_PRODUCT_OWNER)
                    .stream().findFirst()
                    .ifPresent(agent -> agents.add(agent.getId()));
        }

        return agents;
    }
}
