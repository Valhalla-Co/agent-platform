package br.com.valhalla.agentcore.ports;

import br.com.valhalla.agentcore.dto.ArchitectRequest;
import br.com.valhalla.agentcore.dto.JavaEngineerRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Port LLM especializado para operações de agentes.
 * Estende o LLMPort base com métodos específicos de cada agente.
 */
public interface AgentLLMPort extends LLMPort {

    /**
     * Analisa código usando prompts do Java Engineer.
     */
    CompletableFuture<Map<String, Object>> analyzeCode(JavaEngineerRequest request);

    /**
     * Gera código usando prompts do Java Engineer.
     */
    CompletableFuture<Map<String, Object>> generateCode(JavaEngineerRequest request);

    /**
     * Revisa arquitetura usando prompts do Architect.
     */
    CompletableFuture<Map<String, Object>> reviewArchitecture(ArchitectRequest request);

    /**
     * Propõe design de sistema usando prompts do Architect.
     */
    CompletableFuture<Map<String, Object>> proposeDesign(ArchitectRequest request);

    /**
     * Analisa trade-offs para uma decisão.
     */
    CompletableFuture<List<String>> analyzeTradeoffs(String decision, List<String> options);
}
