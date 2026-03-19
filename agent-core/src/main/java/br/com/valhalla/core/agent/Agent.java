package br.com.valhalla.core.agent;

/**
 * Interface base para todos os agentes do sistema.
 * Define o contrato que todos os agentes devem seguir.
 */
public interface Agent {

    /**
     * Retorna o nome do agente.
     * @return Nome do agente (ex: "Odin", "Dev", "Architect")
     */
    String getName();

    /**
     * Retorna as capabilities (habilidades) do agente.
     * @return Capabilities do agente
     */
    AgentCapability getCapabilities();

    /**
     * Executa uma tarefa com base no contexto fornecido.
     * @param context Contexto de execução
     * @return Resultado da execução
     */
    AgentResult execute(AgentContext context);

    /**
     * Verifica se o agente pode handle (processar) o contexto fornecido.
     * @param context Contexto a ser analisado
     * @return true se o agente pode processar, false caso contrário
     */
    boolean canHandle(AgentContext context);

    /**
     * Retorna a base de conhecimento (brain) do agente.
     * @return Knowledge base
     */
    br.com.valhalla.core.brain.KnowledgeBase getBrain();
}
