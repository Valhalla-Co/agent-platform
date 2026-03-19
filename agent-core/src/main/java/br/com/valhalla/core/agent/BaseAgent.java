package br.com.valhalla.core.agent;

import br.com.valhalla.core.brain.KnowledgeBase;
import br.com.valhalla.core.llm.LLMProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

/**
 * Classe base abstrata para todos os agentes.
 * Implementa o Template Method Pattern para executar tarefas.
 */
public abstract class BaseAgent implements Agent {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected LLMProvider llm;
    protected KnowledgeBase brain;

    /**
     * Construtor com LLM e Brain.
     */
    protected BaseAgent(LLMProvider llm, KnowledgeBase brain) {
        this.llm = llm;
        this.brain = brain;
    }

    @Override
    public final AgentResult execute(AgentContext context) {
        logger.info("[{}] Executing task: {}", getName(), context.getTask());
        Instant start = Instant.now();
        Duration executionTime = Duration.ZERO;

        try {
            // Template Method Pattern
            preExecute(context);
            AgentResult result = doExecute(context);
            postExecute(context, result);

            executionTime = Duration.between(start, Instant.now());
            logger.info("[{}] Task completed in {}ms", getName(), executionTime.toMillis());

            // Build a new AgentResult preserving success/message/data, and only set error if present
            AgentResult.Builder builder = AgentResult.builder()
                    .success(result.isSuccess())
                    .agentName(getName())
                    .message(result.getMessage())
                    .data(result.getData())
                    .executionTime(executionTime);

            if (result.getError() != null) {
                builder.error(result.getError());
            }

            return builder.build();

        } catch (Exception e) {
            executionTime = Duration.between(start, Instant.now());
            logger.error("[{}] Error executing task", getName(), e);

            return AgentResult.error(getName(), "Error: " + e.getMessage());
        }
    }

    /**
     * Hook executado antes da execução principal.
     * Pode ser sobrescrito por subclasses.
     */
    @SuppressWarnings("unused")
    protected void preExecute(AgentContext context) {
        // Hook method - pode ser sobrescrito
    }

    /**
     * Método abstrato que deve ser implementado pelas subclasses.
     * Contém a lógica específica de execução do agente.
     */
    protected abstract AgentResult doExecute(AgentContext context);

    /**
     * Hook executado após a execução principal.
     * Pode ser sobrescrito por subclasses.
     */
    @SuppressWarnings("unused")
    protected void postExecute(AgentContext context, AgentResult result) {
        // Hook method - pode ser sobrescrito
    }

    @Override
    public boolean canHandle(AgentContext context) {
        return getCapabilities().canHandle(context.getTask());
    }

    @Override
    public KnowledgeBase getBrain() {
        return brain;
    }

    @SuppressWarnings("unused")
    protected LLMProvider getLLM() {
        return llm;
    }
}
