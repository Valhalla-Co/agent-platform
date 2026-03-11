package br.com.valhalla.agentcore.ports;

/**
 * Port para operações LLM (chat completion e embeddings).
 * Interface base que pode ser estendida para operações de agentes.
 */
public interface LLMPort {

    /**
     * Gera uma resposta do LLM.
     */
    String generateResponse(String prompt, int maxTokens, double temperature);

    /**
     * Gera um vetor de embedding para texto.
     */
    float[] generateEmbedding(String text);

    /**
     * Gera uma resposta com parâmetros padrão.
     */
    default String generateResponse(String prompt) {
        return generateResponse(prompt, 512, 0.7);
    }
}
