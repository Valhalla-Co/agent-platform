package br.com.valhalla.orchestrator;

import br.com.valhalla.core.agent.AgentContext;
import br.com.valhalla.core.agent.AgentResult;
import br.com.valhalla.core.brain.KnowledgeBase;
import br.com.valhalla.core.brain.LessonRepository;
import br.com.valhalla.dev.WaylandAgent;
import br.com.valhalla.architect.MimirAgent;
import br.com.valhalla.providers.ollama.OllamaProvider;
import br.com.valhalla.core.llm.FakeLLMProvider;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * OdinOrchestrator: orquestra chamados para agentes registrados (skeleton funcional).
 *
 * Este orquestrador é um implemento simples para o MVP: instancia um provider (Ollama quando
 * disponível, ou FakeLLMProvider como fallback), um KnowledgeBase (LessonRepository) e dois agentes
 * (Dev e Architect). Para cada agente que puder processar o contexto (canHandle), chama execute()
 * e agrega os resultados.
 */
public class OdinOrchestrator {

    public AgentResult handle(AgentContext context) {
        List<AgentResult> results = new ArrayList<>();

        // Inicializa provider e base de conhecimento
        OllamaProvider provider = new OllamaProvider();
        br.com.valhalla.core.llm.LLMProvider llm = provider;
        if (!provider.isAvailable()) {
            // fallback para provider fake local
            llm = new FakeLLMProvider();
        }

        KnowledgeBase kb = new LessonRepository(Paths.get("odin-brain"));
        try {
            kb.initialize();
        } catch (Exception e) {
            // initialize deve lançar RuntimeException se falhar
            // apenas logamos e seguimos com KB possivelmente vazia
            System.err.println("Warning: failed to initialize knowledge base: " + e.getMessage());
        }

        // determine provider name for debugging/demo
        System.out.println("Provider in use: " + llm.getProviderName());

        // Instancia agentes (usando os novos nomes Wayland/Mimir)
        WaylandAgent wayland = new WaylandAgent(llm, kb);
        MimirAgent mimir = new MimirAgent(llm, kb);

        // Lista de agentes - ordem define prioridade simples
        List<br.com.valhalla.core.agent.Agent> agents = List.of(wayland, mimir);
        var allowedAgents = extractAllowedAgents(context);

        StringJoiner messages = new StringJoiner("; ");
        boolean anySuccess = false;

        for (br.com.valhalla.core.agent.Agent agent : agents) {
            try {
                // If persona/Odin defines an allowlist, honor it.
                if (allowedAgents != null && !allowedAgents.isEmpty() && !allowedAgents.contains(agent.getName())) {
                    continue;
                }

                if (agent.canHandle(context)) {
                    AgentResult r = agent.execute(context);
                    results.add(r);
                    messages.add(agent.getName() + ": " + (r.getMessage() == null ? (r.getError() == null ? "(no message)" : r.getError()) : r.getMessage()));
                    if (r.isSuccess()) {
                        anySuccess = true;
                    }
                }
            } catch (Exception e) {
                messages.add(agent.getName() + ": exception -> " + e.getMessage());
            }
        }

        // If no agent handled the request, return a helpful message
        if (results.isEmpty()) {
            return AgentResult.error("OdinOrchestrator", "No agent could handle the request: " + context.getTask());
        }

        // Aggregate result
        if (anySuccess) {
            return AgentResult.success("OdinOrchestrator", messages.toString());
        } else {
            return AgentResult.error("OdinOrchestrator", messages.toString());
        }
    }

    /**
     * Optional allowlist set by OdinApp/Odin through AgentContext.parameters.
     * Expected types: List&lt;String&gt; / Set&lt;String&gt; / String ("Wayland,Mimir").
     */
    @SuppressWarnings("unchecked")
    private java.util.Set<String> extractAllowedAgents(AgentContext context) {
        if (context == null) return null;

        Object param = context.getParameter("allowedAgents");
        if (param == null) return null;

        if (param instanceof java.util.Set<?> set) {
            java.util.Set<String> out = new java.util.HashSet<>();
            for (Object o : set) {
                if (o != null) out.add(o.toString());
            }
            return out;
        }

        if (param instanceof java.util.List<?> list) {
            java.util.Set<String> out = new java.util.HashSet<>();
            for (Object o : list) {
                if (o != null) out.add(o.toString());
            }
            return out;
        }

        if (param instanceof String s) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) return null;
            String[] parts = trimmed.split(",");
            java.util.Set<String> out = new java.util.HashSet<>();
            for (String part : parts) {
                String p = part.trim();
                if (!p.isEmpty()) out.add(p);
            }
            return out;
        }

        // Unknown type
        return null;
    }
}