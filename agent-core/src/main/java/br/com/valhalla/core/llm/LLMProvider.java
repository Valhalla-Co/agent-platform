package br.com.valhalla.core.llm;

/**
 * Interface para provedores de LLM (Large Language Models).
 * Abstrai a comunicaÃ§Ã£o com diferentes providers (Ollama, OpenAI, Claude, etc).
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
     * Analisa cÃ³digo com base em uma instruÃ§Ã£o especÃ­fica.
     *
     * @param code O cÃ³digo a ser analisado
     * @param instruction A instruÃ§Ã£o de anÃ¡lise
     * @return Resposta com a anÃ¡lise
     */
    LLMResponse analyze(String code, String instruction);

    /**
     * Verifica se o provider estÃ¡ disponÃ­vel e funcionando.
     *
     * @return true se o provider estÃ¡ acessÃ­vel
     */
    boolean isAvailable();

    /**
     * Retorna o nome do provider.
     *
     * @return Nome do provider (ex: "Ollama (codellama:7b)")
     */
    String getProviderName();
}
