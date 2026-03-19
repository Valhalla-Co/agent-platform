package br.com.valhalla.core.orchestration;

import br.com.valhalla.core.agent.Agent;
import br.com.valhalla.core.agent.AgentContext;
import br.com.valhalla.core.agent.AgentResult;

import java.nio.file.Path;
import java.util.List;

/**
 * Interface para orquestradores de agentes.
 * Um orchestrator coordena múltiplos agentes para executar tarefas complexas.
 */
public interface Orchestrator {

    /**
     * Registra um agente no orchestrator.
     * @param agent agente a ser registrado
     */
    void registerAgent(Agent agent);

    /**
     * Remove um agente do orchestrator.
     * @param agentName nome do agente
     */
    void unregisterAgent(String agentName);

    /**
     * Lista todos os agentes disponíveis.
     * @return lista de agentes
     */
    List<Agent> getAvailableAgents();

    /**
     * Delega uma tarefa para um agente específico.
     * @param task tarefa a ser executada
     * @param context contexto de execução
     * @return resultado da execução
     */
    AgentResult delegateTask(String task, AgentContext context);

    /**
     * Seleciona o melhor agente para uma tarefa.
     * @param task tarefa a ser executada
     * @return agente selecionado ou null se nenhum puder processar
     */
    Agent selectAgent(String task);
}
