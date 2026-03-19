package br.com.valhalla.dev;

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
        return LLMResponse.success("fake chat response", 1);
    }

    @Override
    public LLMResponse analyze(String code, String instruction) {
        return LLMResponse.success("fake analyze response for:" + instruction, 1);
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

public class WaylandAgentTest {

    @Test
    void shouldReturnSuggestionUsingFakeLLM() {
        LLMProvider fake = new FakeLLM();
        KnowledgeBase kb = new LessonRepository(Paths.get("odin-brain"));

        WaylandAgent agent = new WaylandAgent(fake, kb);

        AgentContext ctx = AgentContext.builder()
                .task("analyze repo")
                .workingDirectory(Paths.get("."))
                .requestedBy("test")
                .build();

        AgentResult res = agent.execute(ctx);

        // debug output for CI
        System.out.println("DEBUG: agent result success=" + res.isSuccess());
        System.out.println("DEBUG: agent result agentName=" + res.getAgentName());
        System.out.println("DEBUG: agent result message=" + res.getMessage());
        System.out.println("DEBUG: agent result data keys=" + res.getData().keySet());

        assertTrue(res.isSuccess());
        assertNotNull(res.getMessage());
        assertTrue(res.getMessage().contains("Wayland suggestion") || res.getData().containsKey("llm_full"));
    }
}
