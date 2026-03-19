package br.com.valhalla.dev;

import br.com.valhalla.core.brain.KnowledgeBase;
import br.com.valhalla.core.llm.LLMProvider;

/**
 * Deprecated alias kept for compatibility. Prefer using WaylandAgent directly.
 */
@Deprecated
public class DevAgent extends WaylandAgent {

    public DevAgent(LLMProvider llm, KnowledgeBase brain) {
        super(llm, brain);
    }
}
