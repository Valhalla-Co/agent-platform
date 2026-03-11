package br.com.valhalla.agentbase;

import br.com.valhalla.agentcore.dto.TaskRequest;
import br.com.valhalla.agentcore.dto.TaskResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RestAgentGatewayTest {
    private MockWebServer server;

    @BeforeEach
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testCreateTaskSuccess() throws Exception {
        String body = "{\"id\":\"1\",\"externalKey\":\"PLAT-123\",\"status\":\"CREATED\"}";
        server.enqueue(new MockResponse().setResponseCode(201).setBody(body).addHeader("Content-Type", "application/json"));

        String baseUrl = server.url("").toString();
        RestAgentGateway gateway = new RestAgentGateway(baseUrl, "");

        TaskRequest req = new TaskRequest();
        req.setTitle("t");
        req.setDescription("d");

        TaskResponse resp = gateway.createTask(req);
        assertNotNull(resp);
        assertEquals("1", resp.getId());
        assertEquals("PLAT-123", resp.getExternalKey());
        assertEquals("CREATED", resp.getStatus());
    }
}
