package br.com.valhalla.agentbase.gateway;

import br.com.valhalla.agentcore.dto.TaskRequest;
import br.com.valhalla.agentcore.dto.TaskResponse;
import br.com.valhalla.agentcore.ports.AgentGateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Gateway para operações em projetos locais (sem servidor REST).
 * Salva tarefas como arquivos JSON no diretório do projeto.
 * Permite fluxo offline: agentes analisam localmente.
 */
public class LocalFileSystemGateway implements AgentGateway {

    private final Path outputDir;
    private final ObjectMapper mapper;

    public LocalFileSystemGateway(String projectPath) {
        this.outputDir = Path.of(projectPath, ".agent-base", "tasks");
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ensureOutputDir();
    }

    @Override
    public TaskResponse createTask(TaskRequest request) throws Exception {
        String taskId = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = timestamp + "_" + taskId.substring(0, 8) + ".json";

        Path taskFile = outputDir.resolve(fileName);

        // Salvar tarefa como JSON
        mapper.writeValue(taskFile.toFile(), request);

        TaskResponse response = new TaskResponse();
        response.setId(taskId);
        response.setExternalKey(fileName);
        response.setStatus("CREATED_LOCAL");
        return response;
    }

    private void ensureOutputDir() {
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar diretório de saída: " + outputDir, e);
        }
    }
}
