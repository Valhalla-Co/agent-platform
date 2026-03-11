package br.com.valhalla.agentbase;

import br.com.valhalla.agentcore.dto.TaskRequest;
import br.com.valhalla.agentcore.dto.TaskResponse;
import br.com.valhalla.agentcore.ports.AgentGateway;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class RestAgentGateway implements AgentGateway {

    private final String baseUrl;
    private final String token;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public RestAgentGateway(String baseUrl, String token) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.token = token;
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    @Override
    public TaskResponse createTask(TaskRequest request) throws Exception {
        String url = baseUrl + "/api/agents/tasks";
        String body = mapper.writeValueAsString(Map.of(
                "title", request.getTitle(),
                "description", request.getDescription(),
                "metadata", request.getMetadata()
        ));
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null && !token.isBlank()) {
            b.header("Authorization", "Bearer " + token);
        }
        HttpRequest httpRequest = b.build();
        HttpResponse<String> resp = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            Map map = mapper.readValue(resp.body(), Map.class);
            TaskResponse tr = new TaskResponse();
            tr.setId((String) map.getOrDefault("id", null));
            tr.setExternalKey((String) map.getOrDefault("externalKey", map.get("key")));
            tr.setStatus((String) map.getOrDefault("status", "CREATED"));
            return tr;
        }
        throw new RuntimeException("Unexpected response from platform: " + resp.statusCode() + " - " + resp.body());
    }
}
