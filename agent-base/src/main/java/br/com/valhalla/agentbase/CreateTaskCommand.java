package br.com.valhalla.agentbase;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import br.com.valhalla.agentcore.dto.TaskRequest;
import br.com.valhalla.agentcore.dto.TaskResponse;
import br.com.valhalla.agentcore.ports.AgentGateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@Command(name = "create-task", description = "Create a task on the platform via REST")
public class CreateTaskCommand implements Runnable {

    @Option(names = {"-t", "--title"}, required = true, description = "Task title")
    private String title;

    @Option(names = {"-d", "--description"}, required = false, description = "Task description")
    private String description;

    @Option(names = {"-m", "--metadata"}, required = false, description = "Metadata as key=value pairs, comma-separated")
    private String metadata;

    private final ObjectMapper mapper = new ObjectMapper();
    private AgentGateway gateway; // injectable for tests

    // Default constructor used by Picocli
    public CreateTaskCommand() {
        String baseUrl = System.getenv("AGENT_PLATFORM_URL");
        String token = System.getenv("AGENT_PLATFORM_API_TOKEN");
        if (baseUrl != null && !baseUrl.isBlank()) {
            this.gateway = new RestAgentGateway(baseUrl, token);
        }
    }

    // Test constructor to inject a fake gateway
    CreateTaskCommand(AgentGateway gateway) {
        this.gateway = gateway;
    }

    // Setters to help unit tests set options directly
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public void setGateway(AgentGateway gateway) { this.gateway = gateway; }

    @Override
    public void run() {
        try {
            TaskRequest req = new TaskRequest();
            req.setTitle(title);
            req.setDescription(description);
            if (metadata != null && !metadata.isBlank()) {
                Map<String, Object> meta = new HashMap<>();
                String[] parts = metadata.split(",");
                for (String p : parts) {
                    String[] kv = p.split("=");
                    if (kv.length == 2) meta.put(kv[0].trim(), kv[1].trim());
                }
                req.setMetadata(meta);
            }

            if (gateway == null) {
                String baseUrl = System.getenv("AGENT_PLATFORM_URL");
                String token = System.getenv("AGENT_PLATFORM_API_TOKEN");
                if (baseUrl == null || baseUrl.isBlank()) {
                    System.err.println("AGENT_PLATFORM_URL is not set and no gateway injected");
                    return;
                }
                gateway = new RestAgentGateway(baseUrl, token);
            }

            TaskResponse resp = gateway.createTask(req);
            System.out.println("Created task: " + resp.getExternalKey() + " status=" + resp.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create task: " + e.getMessage());
        }
    }
}
