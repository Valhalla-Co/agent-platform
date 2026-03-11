package br.com.valhalla.odin.discovery;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contexto imutável de um projeto descoberto pelo ProjectDiscovery.
 * Contém informações suficientes para que os agentes entendam a estrutura
 * e tecnologias do projeto-alvo.
 */
public class ProjectContext {

    private final String projectPath;
    private final String projectName;
    private final ProjectType projectType;
    private final String language;
    private final String framework;
    private final String buildTool;
    private final List<String> sourceFiles;
    private final List<String> modules;
    private final Map<String, Object> metadata;

    public ProjectContext(
            String projectPath,
            String projectName,
            ProjectType projectType,
            String language,
            String framework,
            String buildTool,
            List<String> sourceFiles,
            List<String> modules,
            Map<String, Object> metadata) {
        this.projectPath = projectPath;
        this.projectName = projectName;
        this.projectType = projectType;
        this.language = language;
        this.framework = framework;
        this.buildTool = buildTool;
        this.sourceFiles = sourceFiles != null ? List.copyOf(sourceFiles) : List.of();
        this.modules = modules != null ? List.copyOf(modules) : List.of();
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public String getProjectPath() { return projectPath; }
    public String getProjectName() { return projectName; }
    public ProjectType getProjectType() { return projectType; }
    public String getLanguage() { return language; }
    public String getFramework() { return framework; }
    public String getBuildTool() { return buildTool; }
    public List<String> getSourceFiles() { return sourceFiles; }
    public List<String> getModules() { return modules; }
    public Map<String, Object> getMetadata() { return metadata; }

    /**
     * Gera um resumo textual para injeção nos prompts dos agentes.
     */
    public String toPromptSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Project: ").append(projectName).append("\n");
        sb.append("Path: ").append(projectPath).append("\n");
        sb.append("Type: ").append(projectType).append("\n");
        sb.append("Language: ").append(language).append("\n");
        sb.append("Framework: ").append(framework).append("\n");
        sb.append("Build Tool: ").append(buildTool).append("\n");
        sb.append("Modules: ").append(modules.size()).append("\n");
        sb.append("Source Files: ").append(sourceFiles.size()).append("\n");
        return sb.toString();
    }

    public enum ProjectType {
        JAVA_MAVEN,
        JAVA_GRADLE,
        NODE_NPM,
        PYTHON_PIP,
        DOTNET,
        UNKNOWN
    }
}
