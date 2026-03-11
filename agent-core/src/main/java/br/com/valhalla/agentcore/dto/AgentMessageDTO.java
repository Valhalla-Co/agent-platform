package br.com.valhalla.agentcore.dto;

import java.time.Instant;
import java.util.Map;

/**
 * DTO para mensagens de comunicação inter-agentes.
 */
public record AgentMessageDTO(
    String senderId,
    String recipientId,
    String messageType,
    String content,
    Map<String, Object> metadata,
    Instant timestamp
) {
}
