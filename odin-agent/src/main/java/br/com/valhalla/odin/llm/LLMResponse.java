package br.com.valhalla.odin.llm;

/**
 * Resposta de um LLM Provider.
 */
public class LLMResponse {
    private String content;
    private boolean success;
    private String error;
    private long responseTimeMs;
    private int tokensUsed;

    private LLMResponse() {}

    /**
     * Cria uma resposta de sucesso.
     */
    public static LLMResponse success(String content, long responseTime) {
        LLMResponse response = new LLMResponse();
        response.content = content;
        response.success = true;
        response.responseTimeMs = responseTime;
        return response;
    }

    /**
     * Cria uma resposta de erro.
     */
    public static LLMResponse error(String error) {
        LLMResponse response = new LLMResponse();
        response.success = false;
        response.error = error;
        return response;
    }

    // Getters
    public String getContent() {
        return content;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public int getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(int tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
}
