package br.com.valhalla.odin.llm;

/**
 * Interface para provedores de LLM (Large Language Models).
 * Abstrai a comunicação com diferentes providers (Ollama, OpenAI, Claude, etc).
 */
public interface LLMProvider {

    /**
     * Envia um prompt para o LLM e recebe a resposta.
     *
     * @param prompt O prompt/pergunta para o LLM
     * @return Resposta do LLM
     */
    LLMResponse chat(String prompt);

    /**
     * Analisa código com base em uma instrução específica.
     *
     * @param code O código a ser analisado
     * @param instruction A instrução de análise
     * @return Resposta com a análise
     */
    LLMResponse analyze(String code, String instruction);

    /**
     * Verifica se o provider está disponível e funcionando.
     *
     * @return true se o provider está acessível
     */
    boolean isAvailable();

    /**
     * Retorna o nome do provider.
     *
     * @return Nome do provider (ex: "Ollama (codellama:7b)")
     */
    String getProviderName();
}
