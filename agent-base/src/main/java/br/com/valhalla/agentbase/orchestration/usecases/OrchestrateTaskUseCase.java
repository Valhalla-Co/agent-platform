package br.com.valhalla.agentbase.orchestration.usecases;

import br.com.valhalla.agentcore.domain.*;
import br.com.valhalla.agentcore.domain.events.TaskAssigned;
import br.com.valhalla.agentcore.domain.events.TaskCreated;
import br.com.valhalla.agentcore.domain.repositories.AgentRepository;
import br.com.valhalla.agentcore.domain.repositories.TaskRepository;
import br.com.valhalla.agentcore.domain.shared.TenantId;
import br.com.valhalla.agentcore.domain.valueobjects.AgentId;
import br.com.valhalla.agentcore.dto.OrchestrationContext;
import br.com.valhalla.agentcore.dto.OrchestrationTaskRequest;
import br.com.valhalla.agentcore.dto.OrchestrationTaskResponse;
import br.com.valhalla.agentcore.ports.EventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso principal de orquestração.
 * Recebe tarefas, analisa requisitos, determina quais agentes envolver,
 * cria plano de orquestração, delega para use cases especializados,
 * monitora progresso e agrega resultados.
 * Framework-agnostic — sem Spring, sem Lombok.
 */
public class OrchestrateTaskUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrchestrateTaskUseCase.class);

    private final TaskRepository taskRepository;
    private final AgentRepository agentRepository;
    private final DelegateToJavaEngineerUseCase javaEngineerDelegate;
    private final DelegateToArchitectUseCase architectDelegate;
    private final CoordinateAgentCommunicationUseCase coordinationUseCase;
    private final EventPublisher eventPublisher;

    public OrchestrateTaskUseCase(
            TaskRepository taskRepository,
            AgentRepository agentRepository,
            DelegateToJavaEngineerUseCase javaEngineerDelegate,
            DelegateToArchitectUseCase architectDelegate,
            CoordinateAgentCommunicationUseCase coordinationUseCase,
            EventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.agentRepository = agentRepository;
        this.javaEngineerDelegate = javaEngineerDelegate;
        this.architectDelegate = architectDelegate;
        this.coordinationUseCase = coordinationUseCase;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Orquestra a execução de uma tarefa.
     */
    public CompletableFuture<OrchestrationTaskResponse> orchestrate(
            OrchestrationTaskRequest request, OrchestrationContext context) {

        log.info("Iniciando orquestração para tarefa tipo: {} com prioridade: {}",
            request.type(), request.priority());

        TenantId tenantId = TenantId.of(context.tenantId());

        // Criar tarefa
        Task task = Task.create(
            tenantId, request.type(), request.description(),
            request.priority(), request.context()
        );
        task.addContext("orchestrationContext", context);
        Task savedTask = taskRepository.save(task);

        // Publicar evento de criação
        eventPublisher.publish(new TaskCreated(
            savedTask.getId(), tenantId, savedTask.getType(),
            savedTask.getPriority(), savedTask.getDescription()
        ));

        // Analisar complexidade e determinar estratégia
        TaskComplexity complexity = analyzeComplexity(savedTask);
        log.info("Tarefa {} complexidade: {}", savedTask.getId(), complexity);

        return executeBasedOnComplexity(savedTask, complexity)
            .thenApply(result -> {
                savedTask.complete(result);
                taskRepository.save(savedTask);
                return buildResponse(savedTask);
            })
            .exceptionally(error -> {
                log.error("Execução da tarefa falhou: {}", error.getMessage(), error);
                savedTask.fail(error.getMessage());
                taskRepository.save(savedTask);
                return buildResponse(savedTask);
            });
    }

    private TaskComplexity analyzeComplexity(Task task) {
        return switch (task.getType()) {
            case CODE_ANALYSIS -> TaskComplexity.SIMPLE;
            case CODE_GENERATION -> {
                Map<String, Object> ctx = task.getContext();
                int fileCount = ctx.containsKey("files")
                    ? ((List<?>) ctx.get("files")).size() : 0;
                yield fileCount > 3 ? TaskComplexity.MODERATE : TaskComplexity.SIMPLE;
            }
            case ARCHITECTURE_REVIEW, TECHNICAL_DESIGN -> TaskComplexity.MODERATE;
            case SYSTEM_DESIGN, PRODUCT_REQUIREMENT -> TaskComplexity.COMPLEX;
        };
    }

    private CompletableFuture<Map<String, Object>> executeBasedOnComplexity(
            Task task, TaskComplexity complexity) {
        return switch (complexity) {
            case SIMPLE -> executeSingleAgent(task);
            case MODERATE -> executeSequentialDelegation(task);
            case COMPLEX -> executeMultiAgentCollaboration(task);
        };
    }

    private CompletableFuture<Map<String, Object>> executeSingleAgent(Task task) {
        log.info("Executando tarefa {} com agente único", task.getId());

        AgentType agentType = determineAgentType(task.getType());

        List<Agent> availableAgents = agentRepository.findAvailableByType(agentType);
        if (availableAgents.isEmpty()) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("Nenhum agente disponível do tipo: " + agentType)
            );
        }

        Agent agent = availableAgents.get(0);
        agent.assignTask();
        agentRepository.save(agent);

        task.assignTo(agent.getId());
        task.start();
        taskRepository.save(task);

        eventPublisher.publish(new TaskAssigned(task.getId(), agent.getId()));

        return delegateToAgent(task, agentType)
            .whenComplete((result, error) -> {
                agent.completeTask();
                agentRepository.save(agent);
            });
    }

    private CompletableFuture<Map<String, Object>> executeSequentialDelegation(Task task) {
        log.info("Executando tarefa {} com delegação sequencial", task.getId());

        return architectDelegate.delegate(task)
            .thenCompose(architectResult -> {
                log.info("Architect concluído, passando para engineer");
                task.addContext("architectResult", architectResult);
                return javaEngineerDelegate.delegate(task)
                    .thenApply(engineerResult -> {
                        Map<String, Object> combined = new HashMap<>();
                        combined.put("architectResult", architectResult);
                        combined.put("engineerResult", engineerResult);
                        return combined;
                    });
            });
    }

    private CompletableFuture<Map<String, Object>> executeMultiAgentCollaboration(Task task) {
        log.info("Executando tarefa {} com colaboração multi-agente", task.getId());

        return coordinationUseCase.startCollaboration(task)
            .thenCompose(conversationId -> {
                CompletableFuture<Map<String, Object>> architectFuture =
                    architectDelegate.delegate(task);
                CompletableFuture<Map<String, Object>> engineerFuture =
                    javaEngineerDelegate.delegate(task);

                return CompletableFuture.allOf(architectFuture, engineerFuture)
                    .thenApply(v -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("architectResult", architectFuture.join());
                        result.put("engineerResult", engineerFuture.join());
                        return coordinationUseCase.aggregateResults(conversationId, result).join();
                    });
            });
    }

    private AgentType determineAgentType(TaskType taskType) {
        return switch (taskType) {
            case CODE_ANALYSIS, CODE_GENERATION -> AgentType.JAVA_ENGINEER;
            case ARCHITECTURE_REVIEW, TECHNICAL_DESIGN, SYSTEM_DESIGN, PRODUCT_REQUIREMENT ->
                AgentType.ARCHITECT_PRODUCT_OWNER;
        };
    }

    private CompletableFuture<Map<String, Object>> delegateToAgent(Task task, AgentType agentType) {
        return switch (agentType) {
            case JAVA_ENGINEER -> javaEngineerDelegate.delegate(task);
            case ARCHITECT_PRODUCT_OWNER -> architectDelegate.delegate(task);
            case ORCHESTRATOR -> CompletableFuture.failedFuture(
                new IllegalStateException("Não é possível delegar para o orquestrador")
            );
        };
    }

    private OrchestrationTaskResponse buildResponse(Task task) {
        Agent agent = task.getAssignedAgent() != null
            ? agentRepository.findById(task.getAssignedAgent()).orElse(null) : null;

        return new OrchestrationTaskResponse(
            task.getId().getValue(),
            task.getStatus(),
            agent != null ? agent.getType() : null,
            calculateEstimatedCompletion(task),
            task.getResult(),
            task.getErrorMessage()
        );
    }

    private Instant calculateEstimatedCompletion(Task task) {
        if (task.getStatus() == TaskStatus.COMPLETED ||
            task.getStatus() == TaskStatus.FAILED ||
            task.getStatus() == TaskStatus.CANCELLED) {
            return task.getCompletedAt();
        }
        return Instant.now().plus(task.getTimeout());
    }

    private enum TaskComplexity {
        SIMPLE, MODERATE, COMPLEX
    }
}
