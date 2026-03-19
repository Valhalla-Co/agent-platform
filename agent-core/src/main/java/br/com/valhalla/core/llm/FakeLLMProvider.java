package br.com.valhalla.core.llm;

/**
 * Fake LLM provider used as fallback when a real provider (Ollama) is not available.
 * Returns deterministic, fast responses suitable for local demos and tests.
 */
public class FakeLLMProvider implements LLMProvider {

    private final String name;

    public FakeLLMProvider() {
        this.name = "fake";
    }

    @Override
    public LLMResponse chat(String prompt) {
        String content = "fake chat response for prompt: " + (prompt == null ? "<null>" : (prompt.length() > 200 ? prompt.substring(0,200) + "..." : prompt));
        return LLMResponse.success(content, 0L);
    }

    @Override
    public LLMResponse analyze(String code, String instruction) {
        String content = "fake analyze response for instruction: " + instruction + "\nsummary: keep changes minimal and add tests.";
        return LLMResponse.success(content, 0L);
    }

    @Override
    public boolean isAvailable() {
        return true; // always available as fallback
    }

    @Override
    public String getProviderName() {
        return "FakeProvider";
    }
}
