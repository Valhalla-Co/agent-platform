package br.com.valhalla.architect;

import br.com.valhalla.core.agent.AgentContext;
import br.com.valhalla.core.agent.AgentResult;
import br.com.valhalla.core.agent.BaseAgent;
import br.com.valhalla.core.brain.KnowledgeBase;
import br.com.valhalla.core.llm.LLMProvider;
import br.com.valhalla.core.llm.LLMResponse;

/**
 * MimirAgent - inspired by Mímir, the source of wisdom; focuses on architecture advice and decisions.
 */
public class MimirAgent extends BaseAgent {

    public MimirAgent(LLMProvider llm, KnowledgeBase brain) {
        super(llm, brain);
    }

    @Override
    public String getName() {
        return "Mimir";
    }

    @Override
    public br.com.valhalla.core.agent.AgentCapability getCapabilities() {
        return new br.com.valhalla.core.agent.AgentCapability(java.util.Set.of("architecture","design"), java.util.Set.of("system"), 3);
    }

    @Override
    protected AgentResult doExecute(AgentContext context) {
        try {
            String prompt = "You are Mimir, an expert architect. The task is: " + context.getTask() + ". Provide a concise 3-step action plan with estimated effort (S/M/L) and key risks.";
            LLMResponse resp = llm.chat(prompt);

            // debug using logger
            try {
                logger.debug("MimirAgent: llm provider={}, resp={}",
                        (llm == null ? "<null>" : llm.getProviderName()),
                        (resp == null ? "<null>" : (resp.isSuccess() + ":" + resp.getContent())));
            } catch (Exception e) {
                logger.debug("MimirAgent: error printing debug: {}", e.getMessage());
            }

            if (resp == null || !resp.isSuccess()) {
                return AgentResult.error(getName(), "LLM chat failed: " + (resp == null ? "null response" : resp.getError()));
            }

            String summary = "Mimir plan: " + (resp.getContent().length() > 300 ? resp.getContent().substring(0, 300) + "..." : resp.getContent());

            return AgentResult.builder()
                    .success(true)
                    .agentName(getName())
                    .message(summary)
                    .data("llm_full", resp.getContent())
                    .build();

        } catch (Exception e) {
            return AgentResult.error(getName(), "Exception: " + e.getMessage());
        }
    }
}
