package br.com.valhalla.orchestrator;

import br.com.valhalla.core.agent.AgentContext;
import br.com.valhalla.core.agent.AgentResult;
import br.com.valhalla.core.llm.FakeLLMProvider;
import br.com.valhalla.core.llm.LLMProvider;
import br.com.valhalla.core.llm.LLMResponse;
import br.com.valhalla.providers.ollama.OllamaProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Odin (persona + routing): recebe o input do usuário (via OdinApp),
 * decide quais agentes chamar e delega para OdinOrchestrator.
 */
public class Odin {

    private static final String DEFAULT_PERSONA_PROMPT =
            "Você é Odin, um orquestrador de agentes. Responda em português. " +
            "Ao entender o pedido do usuário, explique brevemente o que vai fazer e " +
            "quais agentes (Wayland/Mimir) serão acionados para executar cada parte.";

    private final ObjectMapper mapper = new ObjectMapper();

    public String handleUserRequest(
            String userPrompt,
            Path repoPath,
            String intent,
            String personaPrompt,
            List<String> allowedAgentsOverride
    ) {
        if (userPrompt == null || userPrompt.isBlank()) {
            return "Odin: prompt vazio. Informe o que deseja fazer.";
        }

        String effectivePersona = (personaPrompt == null || personaPrompt.isBlank()) ? DEFAULT_PERSONA_PROMPT : personaPrompt.trim();
        String effectiveIntent = (intent == null || intent.isBlank()) ? null : intent.trim();

        OllamaProvider provider = new OllamaProvider();
        LLMProvider llm = provider.isAvailable() ? provider : new FakeLLMProvider();

        OdinDecision decision = decideAgentsWithPersona(llm, userPrompt, effectiveIntent, effectivePersona, allowedAgentsOverride);

        AgentContext ctx = AgentContext.builder()
                .task(decision.taskForAgents)
                .workingDirectory(repoPath)
                .requestedBy("odin-app")
                .parameter("allowedAgents", decision.allowedAgents)
                .build();

        AgentResult agentResult = new OdinOrchestrator().handle(ctx);

        String agentOutcome;
        if (agentResult.isSuccess()) {
            agentOutcome = agentResult.getMessage();
        } else {
            agentOutcome = agentResult.getError() != null ? agentResult.getError() : agentResult.getMessage();
        }

        String personaReply = decision.personaReply;
        if (personaReply == null || personaReply.isBlank()) {
            personaReply = "Odin: vou acionar os agentes " + decision.allowedAgents + " para atender ao seu pedido.";
        }

        return personaReply + "\n\nOdin delegou e recebeu:\n" + (agentOutcome == null ? "<sem saída>" : agentOutcome);
    }

    private OdinDecision decideAgentsWithPersona(
            LLMProvider llm,
            String userPrompt,
            String intent,
            String personaPrompt,
            List<String> allowedAgentsOverride
    ) {
        // If user forces agents, we still generate a persona reply if possible.
        if (allowedAgentsOverride != null && !allowedAgentsOverride.isEmpty()) {
            Set<String> allowed = new HashSet<>();
            for (String a : allowedAgentsOverride) {
                if (a != null && !a.isBlank()) allowed.add(a.trim());
            }
            return new OdinDecision(
                    new ArrayList<>(allowed),
                    taskForAgentsFromAllowlist(userPrompt, intent, allowed),
                    "Odin: execução manual (override) dos agentes: " + allowed
            );
        }

        // If we're using fake, skip LLM decision and use deterministic routing.
        if (llm instanceof FakeLLMProvider) {
            return decideByRules(userPrompt, intent);
        }

        String decisionPrompt = buildDecisionPrompt(userPrompt, intent, personaPrompt);
        LLMResponse resp = llm.chat(decisionPrompt);
        String content = resp != null ? resp.getContent() : null;

        OdinDecision parsed = tryParseDecisionJson(content);
        if (parsed != null && parsed.allowedAgents != null && !parsed.allowedAgents.isEmpty() && parsed.taskForAgents != null) {
            return parsed;
        }

        // Fallback: deterministic routing.
        return decideByRules(userPrompt, intent);
    }

    private OdinDecision decideByRules(String userPrompt, String intent) {
        String lower = (userPrompt + " " + (intent == null ? "" : intent)).toLowerCase(Locale.ROOT);

        Set<String> allowed = new HashSet<>();

        // Mimir: arquitetura / design / sistema
        if (containsAny(lower, "arquitetura", "architecture", "design", "sistema", "system", "trade-off", "tradeoff")) {
            allowed.add("Mimir");
        }

        // Wayland: código / refactor / java / patch / PR
        if (containsAny(lower, "refactor", "code", "java", "patch", "pr", "pull request", "implement", "melhoria", "melhorias")) {
            allowed.add("Wayland");
        }

        // If still nothing matched, default to both (safer in this MVP).
        if (allowed.isEmpty()) {
            allowed.add("Wayland");
            allowed.add("Mimir");
        }

        return new OdinDecision(
                new ArrayList<>(allowed),
                taskForAgentsFromAllowlist(userPrompt, intent, allowed),
                "Odin: entendi seu pedido e vou acionar os agentes " + allowed + "."
        );
    }

    private String buildDecisionPrompt(String userPrompt, String intent, String personaPrompt) {
        String effectiveIntent = (intent == null || intent.isBlank()) ? "<auto>" : intent;

        return ""
                + personaPrompt + "\n\n"
                + "Você é Odin (orquestrador). Decida quais agentes executar para este pedido.\n"
                + "Agentes disponíveis:\n"
                + "- Wayland: bom para tarefas envolvendo código/refatoração Java (palavras-chave: code, refactor, java, patch, PR)\n"
                + "- Mimir: bom para arquitetura/design de sistema (palavras-chave: architecture, design, system, trade-off)\n\n"
                + "Regras:\n"
                + "1) Retorne EXCLUSIVAMENTE JSON válido (sem texto extra), com estas chaves:\n"
                + "   {\"allowedAgents\": [\"Wayland\"|\"Mimir\"], \"taskForAgents\": string, \"personaReply\": string}\n"
                + "2) taskForAgents precisa conter as palavras-chave necessárias para que OdinOrchestrator chame os agentes corretos.\n"
                + "   - Se incluir Wayland, inclua pelo menos uma das: code/refactor/java\n"
                + "   - Se incluir Mimir, inclua pelo menos uma das: architecture/design/system\n"
                + "3) Inclua o pedido do usuário dentro de taskForAgents (para contexto do agente).\n"
                + "4) personaReply deve ser a resposta ao usuário (em português), explicando em 1-3 frases o que vai fazer.\n\n"
                + "Pedido do usuário:\n"
                + userPrompt + "\n\n"
                + "Intent (opcional): " + effectiveIntent + "\n";
    }

    private OdinDecision tryParseDecisionJson(String content) {
        if (content == null || content.isBlank()) return null;

        String json = extractJsonObject(content);
        if (json == null) return null;

        try {
            // We intentionally use a simple DTO to accept both string arrays and list.
            OdinDecision dto = mapper.readValue(json, OdinDecision.class);
            if (dto.allowedAgents == null) return null;

            // Normalize allowed agents values (capitalization).
            List<String> normalized = new ArrayList<>();
            for (String a : dto.allowedAgents) {
                if (a == null) continue;
                String t = a.trim();
                if (t.equalsIgnoreCase("wayland")) t = "Wayland";
                if (t.equalsIgnoreCase("mimir")) t = "Mimir";
                if (!t.isBlank()) normalized.add(t);
            }
            dto.allowedAgents = normalized;
            return dto;
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Extract first JSON object from a response that should be JSON-only.
     */
    private String extractJsonObject(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) return null;
        return text.substring(start, end + 1);
    }

    private String taskForAgentsFromAllowlist(String userPrompt, String intent, Set<String> allowedAgents) {
        StringBuilder task = new StringBuilder();
        task.append(userPrompt.trim());

        if (intent != null && !intent.isBlank()) {
            task.append("\nIntent: ").append(intent.trim());
        }

        if (allowedAgents.contains("Wayland")) {
            task.append("\nWaylandKeywords: code refactor java patch");
        }
        if (allowedAgents.contains("Mimir")) {
            task.append("\nMimirKeywords: architecture design system trade-off");
        }

        return task.toString();
    }

    private boolean containsAny(String haystackLower, String... needles) {
        for (String n : needles) {
            if (n != null && !n.isBlank() && haystackLower.contains(n.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    // DTO for JSON parsing: keep fields public for Jackson.
    public static class OdinDecision {
        public List<String> allowedAgents;
        public String taskForAgents;
        public String personaReply;

        // Jackson no-arg ctor.
        public OdinDecision() {
        }

        public OdinDecision(List<String> allowedAgents, String taskForAgents, String personaReply) {
            this.allowedAgents = allowedAgents;
            this.taskForAgents = taskForAgents;
            this.personaReply = personaReply;
        }
    }
}

