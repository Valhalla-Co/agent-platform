package br.com.valhalla.agentcore.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO para requisições específicas do Java Engineer.
 */
public record JavaEngineerRequest(
    String taskDescription,
    Map<String, Object> codeContext,
    List<String> filePaths,
    String requirements,
    String projectContext
) {
}
