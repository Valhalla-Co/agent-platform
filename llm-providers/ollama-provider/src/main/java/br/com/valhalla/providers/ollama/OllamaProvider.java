package br.com.valhalla.providers.ollama;

import br.com.valhalla.core.llm.LLMProvider;
import br.com.valhalla.core.llm.LLMResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * OllamaProvider usando HTTP para integrar com uma instância local do Ollama (configurável).
 * Configuração via variável de ambiente OLLAMA_API_URL (default http://localhost:11434).
 *
 * Nota: o endpoint exato da API do Ollama pode variar conforme a versão; este provider tenta
 * um payload genérico e devolve o corpo como texto quando não for possível parsear JSON.
 */
public class OllamaProvider implements LLMProvider {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;

    public OllamaProvider() {
        this.baseUrl = System.getenv().getOrDefault("OLLAMA_API_URL", "http://localhost:11434");
        this.model = System.getenv().getOrDefault("OLLAMA_MODEL", "codellama:7b");
        this.client = new OkHttpClient.Builder()
                .callTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public LLMResponse chat(String prompt) {
        long start = Instant.now().toEpochMilli();
        try {
            // Ollama standard endpoint for text generation
            // https://github.com/ollama/ollama/blob/main/docs/api.md
            String url = baseUrl + "/api/generate";

            String payload = mapper.createObjectNode()
                    .put("model", model)
                    .put("prompt", prompt)
                    .put("stream", false)
                    .toString();

            RequestBody body = RequestBody.create(payload, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                long elapsed = Instant.now().toEpochMilli() - start;
                if (!response.isSuccessful()) {
                    String err = response.body() != null ? response.body().string() : "unknown";
                    return LLMResponse.error("HTTP " + response.code() + ": " + err);
                }

                String respBody = response.body() != null ? response.body().string() : "";
                try {
                    JsonNode node = mapper.readTree(respBody);
                    // Standard field name: "response"
                    if (node.has("response") && node.get("response").isTextual()) {
                        return LLMResponse.success(node.get("response").asText(), elapsed);
                    }
                } catch (Exception ignored) {
                    // fall back to raw below
                }

                return LLMResponse.success(respBody, elapsed);
            }

        } catch (IOException e) {
            return LLMResponse.error("I/O error: " + e.getMessage());
        }
    }

    @Override
    public LLMResponse analyze(String code, String instruction) {
        String prompt = "Analyze the following code with instruction: " + instruction + "\n\n" + code;
        return chat(prompt);
    }

    @Override
    public boolean isAvailable() {
        // Most robust check for local Ollama: /api/tags
        Request request = new Request.Builder().url(baseUrl + "/api/tags").get().build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "Ollama (" + model + ")";
    }
}
