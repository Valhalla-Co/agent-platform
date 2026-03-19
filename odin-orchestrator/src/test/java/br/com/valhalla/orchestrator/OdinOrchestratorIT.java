package br.com.valhalla.orchestrator;

import br.com.valhalla.core.agent.AgentContext;
import br.com.valhalla.core.agent.AgentResult;
import br.com.valhalla.core.llm.FakeLLMProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

/**
 * Integration test for OdinOrchestrator using FakeLLMProvider to ensure deterministic output.
 */
public class OdinOrchestratorIT {

    @Test
    public void testOdinOrchestratorWithFakeProvider() {
        // Arrange
        OdinOrchestrator orchestrator = new OdinOrchestrator();

        AgentContext ctx = AgentContext.builder()
                .task("analyze repository for architecture and code quality")
                .workingDirectory(Paths.get("."))
                .requestedBy("unittest")
                .build();

        // Force fake via system property to ensure deterministic behavior
        System.setProperty("valhalla.forceFake", "true");

        // Act
        AgentResult result = orchestrator.handle(ctx);

        // Assert
        Assertions.assertNotNull(result, "AgentResult should not be null");
        Assertions.assertTrue(result.getMessage() != null || result.getError() != null, "Result should contain a message or an error");

        // with fake provider at least one agent should have produced a message
        Assertions.assertTrue(result.getMessage().contains("Wayland") || result.getMessage().contains("Mimir") || (result.getError() != null && (result.getError().contains("Wayland") || result.getError().contains("Mimir"))));
    }
}
