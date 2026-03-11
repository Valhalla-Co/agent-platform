package br.com.valhalla.agentcore;

import br.com.valhalla.agentcore.dto.TaskRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TaskRequestTest {

    @Test
    public void testTaskRequestGetters() {
        TaskRequest tr = new TaskRequest("1", "title", "desc", Map.of("k", "v"));
        assertEquals("1", tr.getId());
        assertEquals("title", tr.getTitle());
        assertEquals("desc", tr.getDescription());
        assertNotNull(tr.getMetadata());
        assertEquals("v", tr.getMetadata().get("k"));
    }
}
