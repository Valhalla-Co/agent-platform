package br.com.valhalla.agentbase;

import br.com.valhalla.agentcore.dto.TaskRequest;
import br.com.valhalla.agentcore.dto.TaskResponse;
import br.com.valhalla.agentcore.ports.AgentGateway;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CreateTaskCommandTest {

    static class FakeGateway implements AgentGateway {
        @Override
        public TaskResponse createTask(TaskRequest request) throws Exception {
            TaskResponse tr = new TaskResponse();
            tr.setId("1");
            tr.setExternalKey("PLAT-1");
            tr.setStatus("CREATED");
            return tr;
        }
    }

    @Test
    public void testCreateTaskCommandPrintsResult() {
        CreateTaskCommand cmd = new CreateTaskCommand(new FakeGateway());
        cmd.setTitle("Title");
        cmd.setDescription("Desc");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(baos));

        cmd.run();

        System.setOut(oldOut);
        String out = baos.toString();
        assertTrue(out.contains("Created task: PLAT-1 status=CREATED"));
    }
}
