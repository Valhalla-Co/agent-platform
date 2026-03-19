package br.com.valhalla.odin.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Provider para Ollama - LLM local e gratuito.
 */
public class OllamaProvider implements LLMProvider {
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String baseUrl;
    private final String model;

    public OllamaProvider() {
        this.baseUrl = LLMConfig.getBaseUrl();
        this.model = LLMConfig.getModel();
        this.mapper = new ObjectMapper();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(LLMConfig.getTimeoutSeconds(), TimeUnit.SECONDS)
                .readTimeout(LLMConfig.getTimeoutSeconds(), TimeUnit.SECONDS)
                .writeTimeout(LLMConfig.getTimeoutSeconds(), TimeUnit.SECONDS)
                .build();
    }

    @Override
    public LLMResponse chat(String prompt) {
        long startTime = System.currentTimeMillis();

        try {
            String jsonBody = String.format(
                    "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":false}",
                    model, escapeJson(prompt)
            );

            Request request = new Request.Builder()
                    .url(baseUrl + "/api/generate")
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return LLMResponse.error("Ollama error: HTTP " + response.code());
                }

                String responseBody = response.body().string();
                JsonNode jsonNode = mapper.readTree(responseBody);
                String content = jsonNode.get("response").asText();

                long responseTime = System.currentTimeMillis() - startTime;
                return LLMResponse.success(content, responseTime);
            }
        } catch (IOException e) {
            return LLMResponse.error("Connection error: " + e.getMessage());
        }
    }

    @Override
    public LLMResponse analyze(String code, String instruction) {
        String prompt = String.format(
                "%s\n\nCódigo:\n```\n%s\n```",
                instruction, code
        );
        return chat(prompt);
    }

    @Override
    public boolean isAvailable() {
        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/tags")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "Ollama (" + model + ")";
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
