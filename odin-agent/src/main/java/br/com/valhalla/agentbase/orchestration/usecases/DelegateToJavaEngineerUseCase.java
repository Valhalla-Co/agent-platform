package br.com.valhalla.agentbase.orchestration.usecases;

import br.com.valhalla.agentcore.domain.Task;
import br.com.valhalla.agentcore.dto.JavaEngineerRequest;
import br.com.valhalla.agentcore.ports.AgentLLMPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso para delegação de tarefas ao agente Java Engineer.
 * Framework-agnostic.
 */
public class DelegateToJavaEngineerUseCase {

    private static final Logger log = LoggerFactory.getLogger(DelegateToJavaEngineerUseCase.class);

    private final AgentLLMPort agentLLMPort;

    public DelegateToJavaEngineerUseCase(AgentLLMPort agentLLMPort) {
        this.agentLLMPort = agentLLMPort;
    }

    /**
     * Delega uma tarefa para o agente Java Engineer.
     */
    public CompletableFuture<Map<String, Object>> delegate(Task task) {
        log.info("Delegando tarefa {} para agente Java Engineer", task.getId());

        JavaEngineerRequest request = prepareEngineerRequest(task);

        return switch (task.getType()) {
            case CODE_ANALYSIS -> agentLLMPort.analyzeCode(request);
            case CODE_GENERATION -> agentLLMPort.generateCode(request);
            default -> CompletableFuture.failedFuture(
                new IllegalArgumentException(
                    "Java Engineer não pode tratar tarefa do tipo: " + task.getType()
                )
            );
        };
    }

    @SuppressWarnings("unchecked")
    private JavaEngineerRequest prepareEngineerRequest(Task task) {
        Map<String, Object> context = task.getContext();

        List<String> filePaths = (List<String>) context.getOrDefault("files", List.of());
        Map<String, Object> codeContext = (Map<String, Object>) context.getOrDefault("codeContext", Map.of());
        String requirements = (String) context.getOrDefault("requirements", "");
        String projectContext = buildProjectContext(context);

        return new JavaEngineerRequest(
            task.getDescription(), codeContext, filePaths, requirements, projectContext
        );
    }

    private String buildProjectContext(Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Architecture: Clean Architecture + DDD\n");

        if (context.containsKey("architectResult")) {
            sb.append("\nArchitect's Analysis:\n");
            sb.append(context.get("architectResult"));
        }

        return sb.toString();
    }
}
