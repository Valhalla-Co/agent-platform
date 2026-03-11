package br.com.valhalla.agentbase;

import br.com.valhalla.agentcore.dto.TaskRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RestAgentGatewayNegativeTest {
    private MockWebServer server;

    @BeforeEach
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    public void teardown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testCreateTaskServerError() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
        String baseUrl = server.url("").toString();
        RestAgentGateway gateway = new RestAgentGateway(baseUrl, "");
        TaskRequest req = new TaskRequest();
        req.setTitle("t");
        req.setDescription("d");

        Exception ex = assertThrows(RuntimeException.class, () -> gateway.createTask(req));
        assertTrue(ex.getMessage().contains("Unexpected response"));
    }
}
