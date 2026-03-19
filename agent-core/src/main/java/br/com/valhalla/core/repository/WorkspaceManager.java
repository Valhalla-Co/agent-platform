package br.com.valhalla.core.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia workspace de projetos clonados.
 * Projetos externos são clonados para workspace/branches/
 */
public class WorkspaceManager {

    private final Path workspaceRoot;
    private final RepositoryManager repoManager;

    public WorkspaceManager() {
        this(Paths.get("workspace/branches"));
    }

    public WorkspaceManager(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
        this.repoManager = new RepositoryManager();
        ensureWorkspaceExists();
    }

    private void ensureWorkspaceExists() {
        try {
            if (!Files.exists(workspaceRoot)) {
                Files.createDirectories(workspaceRoot);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create workspace directory", e);
        }
    }

    /**
     * Clona um projeto para o workspace.
     */
    public Path cloneProject(String gitUrl) {
        String projectName = extractProjectName(gitUrl);
        Path projectPath = workspaceRoot.resolve(projectName);

        if (Files.exists(projectPath)) {
            System.out.println("Project already exists: " + projectPath);
            return projectPath;
        }

        try {
            return repoManager.cloneRepository(gitUrl, workspaceRoot.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to clone project: " + gitUrl, e);
        }
    }

    /**
     * Lista todos os projetos no workspace.
     */
    public List<Path> listProjects() {
        try {
            return Files.list(workspaceRoot)
                    .filter(Files::isDirectory)
                    .filter(p -> Files.exists(p.resolve(".git")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Retorna o path de um projeto pelo nome.
     */
    public Path getProject(String projectName) {
        Path projectPath = workspaceRoot.resolve(projectName);
        return Files.exists(projectPath) ? projectPath : null;
    }

    /**
     * Remove um projeto do workspace.
     */
    public boolean removeProject(String projectName) {
        Path projectPath = workspaceRoot.resolve(projectName);
        if (Files.exists(projectPath)) {
            return deleteDirectory(projectPath);
        }
        return false;
    }

    private String extractProjectName(String gitUrl) {
        String name = gitUrl.substring(gitUrl.lastIndexOf('/') + 1);
        if (name.endsWith(".git")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }

    private boolean deleteDirectory(Path path) {
        try {
            Files.walk(path)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            // ignore
                        }
                    });
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }
}
