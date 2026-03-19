package br.com.valhalla.odin.llm;

/**
 * Configuração para LLM Providers.
 * Sem necessidade de API keys para Ollama!
 */
public class LLMConfig {
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "codellama:7b";
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final int DEFAULT_TIMEOUT_SECONDS = 120;

    public static String getBaseUrl() {
        return System.getProperty("ollama.base.url", DEFAULT_BASE_URL);
    }

    public static String getModel() {
        return System.getProperty("ollama.model", DEFAULT_MODEL);
    }

    public static int getMaxTokens() {
        String value = System.getProperty("ollama.max.tokens", String.valueOf(DEFAULT_MAX_TOKENS));
        return Integer.parseInt(value);
    }

    public static double getTemperature() {
        String value = System.getProperty("ollama.temperature", String.valueOf(DEFAULT_TEMPERATURE));
        return Double.parseDouble(value);
    }

    public static int getTimeoutSeconds() {
        String value = System.getProperty("ollama.timeout.seconds", String.valueOf(DEFAULT_TIMEOUT_SECONDS));
        return Integer.parseInt(value);
    }
}
