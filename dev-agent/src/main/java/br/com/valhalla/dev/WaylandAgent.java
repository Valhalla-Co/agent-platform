package br.com.valhalla.dev;

import br.com.valhalla.core.agent.AgentContext;
import br.com.valhalla.core.agent.AgentResult;
import br.com.valhalla.core.agent.BaseAgent;
import br.com.valhalla.core.brain.KnowledgeBase;
import br.com.valhalla.core.llm.LLMResponse;
import br.com.valhalla.core.llm.LLMProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * WaylandAgent (inspired by Völundr / Wayland the Smith) - focuses on building, refactoring
 * and producing code changes (patches, PR suggestions).
 */
public class WaylandAgent extends BaseAgent {

    public WaylandAgent(LLMProvider llm, KnowledgeBase brain) {
        super(llm, brain);
    }

    @Override
    public String getName() {
        return "Wayland";
    }

    @Override
    public br.com.valhalla.core.agent.AgentCapability getCapabilities() {
        return new br.com.valhalla.core.agent.AgentCapability(java.util.Set.of("code","refactor"), java.util.Set.of("java"), 5);
    }

    @Override
    protected AgentResult doExecute(AgentContext context) {
        try {
            // POC: coleta alguns arquivos .java do workingDirectory (limita a 5 arquivos pequenos)
            Path wd = context.getWorkingDirectory();
            String codeSample;
            try (var stream = Files.walk(wd)) {
                codeSample = stream
                        .filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java"))
                        .limit(5)
                        .map(p -> "// file: " + wd.relativize(p) + "\n" + readFileSafe(p))
                        .collect(Collectors.joining("\n\n// ----\n\n"));
            } catch (Exception ignored) {
                // se falhar em ler arquivos, usamos tarefa no contexto como entrada
                codeSample = "(could not read files) task=" + context.getTask();
            }

            String instruction = "Provide a concise refactoring suggestion and a small patch example. Prioritize safety and tests.";
            LLMResponse resp = llm.analyze(codeSample, instruction);

            // Debug information using logger
            try {
                logger.debug("WaylandAgent: llm provider={}, resp.success={}, resp.content={}",
                        (llm == null ? "<null>" : llm.getProviderName()),
                        (resp == null ? "<null>" : resp.isSuccess()),
                        (resp == null ? "<null>" : resp.getContent()));
            } catch (Exception e) {
                logger.debug("WaylandAgent: error printing debug: {}", e.getMessage());
            }

            if (resp == null || !resp.isSuccess()) {
                return AgentResult.error(getName(), "LLM analyze failed: " + (resp == null ? "null response" : resp.getError()));
            }

            String summary = "Wayland suggestion: " + (resp.getContent().length() > 200 ? resp.getContent().substring(0, 200) + "..." : resp.getContent());

            return AgentResult.builder()
                    .success(true)
                    .agentName(getName())
                    .message(summary)
                    .data("llm_full", resp.getContent())
                    .build();

        } catch (Exception e) {
            logger.error("WaylandAgent exception", e);
            return AgentResult.error(getName(), "Exception: " + e.getMessage());
        }
    }

    private String readFileSafe(Path p) {
        try {
            return Files.readString(p);
        } catch (IOException e) {
            return "// could not read file: " + e.getMessage();
        }
    }
}
