package br.com.valhalla.agentcore.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO para requisições específicas do Architect.
 */
public record ArchitectRequest(
    String taskDescription,
    Map<String, Object> systemContext,
    List<String> constraints,
    List<String> qualityAttributes,
    String projectContext
) {
}
