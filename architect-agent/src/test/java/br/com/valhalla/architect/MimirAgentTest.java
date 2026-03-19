package br.com.valhalla.architect;

import br.com.valhalla.core.agent.AgentContext;
import br.com.valhalla.core.agent.AgentResult;
import br.com.valhalla.core.brain.KnowledgeBase;
import br.com.valhalla.core.brain.LessonRepository;
import br.com.valhalla.core.llm.LLMProvider;
import br.com.valhalla.core.llm.LLMResponse;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FakeLLM implements LLMProvider {
    @Override
    public LLMResponse chat(String prompt) {
        return LLMResponse.success("step1: do X\nstep2: do Y\nstep3: do Z", 1);
    }

    @Override
    public LLMResponse analyze(String code, String instruction) {
        return LLMResponse.success("analyze", 1);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getProviderName() {
        return "fake";
    }
}

public class MimirAgentTest {

    @Test
    void shouldReturnPlanUsingFakeLLM() {
        LLMProvider fake = new FakeLLM();
        KnowledgeBase kb = new LessonRepository(Paths.get("odin-brain"));

        MimirAgent agent = new MimirAgent(fake, kb);

        AgentContext ctx = AgentContext.builder()
                .task("evaluate architecture")
                .workingDirectory(Paths.get("."))
                .requestedBy("test")
                .build();

        AgentResult res = agent.execute(ctx);

        assertTrue(res.isSuccess());
        assertNotNull(res.getMessage());
        assertTrue(res.getMessage().contains("Mimir plan") || res.getData().containsKey("llm_full"));
    }
}
