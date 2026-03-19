package br.com.valhalla.architect;

import br.com.valhalla.core.brain.KnowledgeBase;
import br.com.valhalla.core.llm.LLMProvider;

/**
 * Deprecated alias kept for compatibility. Prefer using MimirAgent directly.
 */
@Deprecated
public class ArchitectAgent extends MimirAgent {

    public ArchitectAgent(LLMProvider llm, KnowledgeBase brain) {
        super(llm, brain);
    }
}
