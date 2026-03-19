package br.com.valhalla.orchestrator;

import br.com.valhalla.providers.ollama.OllamaProvider;
import br.com.valhalla.core.llm.LLMResponse;

/**
 * Simple connectivity check for OllamaProvider.
 * Usage: set OLLAMA_API_URL if different than default (http://localhost:11434)
 */
public class OllamaCheck {
    public static void main(String[] args) {
        OllamaProvider p = new OllamaProvider();
        System.out.println("Configured Ollama base URL: " + System.getenv().getOrDefault("OLLAMA_API_URL", "http://localhost:11434"));
        System.out.println("Configured model: " + System.getenv().getOrDefault("OLLAMA_MODEL", "codellama:7b"));
        System.out.println("Provider name: " + p.getProviderName());
        boolean avail = false;
        try {
            avail = p.isAvailable();
        } catch (Exception e) {
            System.out.println("isAvailable() raised: " + e.getMessage());
        }
        System.out.println("Available: " + avail);

        if (avail) {
            LLMResponse r = p.chat("Hello from OllamaCheck. Provide a one-line status message.");
            if (r != null) {
                System.out.println("LLM success=" + r.isSuccess() + " content=\n" + (r.getContent() == null ? "<null>" : r.getContent()));
                if (!r.isSuccess()) System.out.println("LLM error: " + r.getError());
            } else {
                System.out.println("Null response from provider");
            }
        } else {
            System.out.println("Ollama not available. If you have Ollama installed, start it and ensure OLLAMA_API_URL is reachable.");
        }
    }
}
