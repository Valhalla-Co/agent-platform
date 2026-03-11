package br.com.valhalla.agentcore.ports;

import br.com.valhalla.agentcore.dto.TaskRequest;
import br.com.valhalla.agentcore.dto.TaskResponse;

public interface AgentGateway {
    TaskResponse createTask(TaskRequest request) throws Exception;
}
