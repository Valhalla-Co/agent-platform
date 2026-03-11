package br.com.valhalla.agentbase.orchestration.usecases;

import br.com.valhalla.agentcore.domain.Task;
import br.com.valhalla.agentcore.dto.ArchitectRequest;
import br.com.valhalla.agentcore.ports.AgentLLMPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso para delegação de tarefas ao agente Architect/Product Owner.
 * Framework-agnostic.
 */
public class DelegateToArchitectUseCase {

    private static final Logger log = LoggerFactory.getLogger(DelegateToArchitectUseCase.class);

    private final AgentLLMPort agentLLMPort;

    public DelegateToArchitectUseCase(AgentLLMPort agentLLMPort) {
        this.agentLLMPort = agentLLMPort;
    }

    /**
     * Delega uma tarefa para o agente Architect.
     */
    public CompletableFuture<Map<String, Object>> delegate(Task task) {
        log.info("Delegando tarefa {} para agente Architect", task.getId());

        ArchitectRequest request = prepareArchitectRequest(task);

        return switch (task.getType()) {
            case ARCHITECTURE_REVIEW -> agentLLMPort.reviewArchitecture(request);
            case TECHNICAL_DESIGN, SYSTEM_DESIGN -> agentLLMPort.proposeDesign(request);
            case PRODUCT_REQUIREMENT -> agentLLMPort.proposeDesign(request);
            default -> CompletableFuture.failedFuture(
                new IllegalArgumentException(
                    "Architect não pode tratar tarefa do tipo: " + task.getType()
                )
            );
        };
    }

    @SuppressWarnings("unchecked")
    private ArchitectRequest prepareArchitectRequest(Task task) {
        Map<String, Object> context = task.getContext();

        Map<String, Object> systemContext = (Map<String, Object>)
            context.getOrDefault("systemContext", Map.of());

        List<String> constraints = (List<String>)
            context.getOrDefault("constraints", List.of());

        List<String> qualityAttributes = (List<String>)
            context.getOrDefault("qualityAttributes",
                List.of("Scalability", "Maintainability", "Security"));

        String projectContext = buildProjectContext(context);

        return new ArchitectRequest(
            task.getDescription(), systemContext, constraints,
            qualityAttributes, projectContext
        );
    }

    private String buildProjectContext(Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Architecture Style: Clean Architecture + Domain-Driven Design\n");
        sb.append("Key Components:\n");
        sb.append("- Domain Layer: Aggregates, Entities, Value Objects, Domain Events\n");
        sb.append("- Application Layer: Use Cases, DTOs, Ports\n");
        sb.append("- Infrastructure Layer: Adapters\n");
        sb.append("- Presentation Layer: REST Controllers\n");

        if (context.containsKey("engineerResult")) {
            sb.append("\nEngineer's Implementation:\n");
            sb.append(context.get("engineerResult"));
        }

        return sb.toString();
    }
}
