package br.com.valhalla.core.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerenciador de repositórios Git.
 * Permite clonar, atualizar e gerenciar repositórios da organização.
 */
public class RepositoryManager {

    private final GitConfig config;
    private final Path workspaceRoot;

    public RepositoryManager() {
        this(GitConfig.fromEnv());
    }

    public RepositoryManager(GitConfig config) {
        this.config = config;
        this.workspaceRoot = Paths.get(config.getWorkspacePath()).toAbsolutePath();
    }

    /**
     * Clona um repositório da organização
     */
    public Path cloneRepository(String repoName) throws IOException {
        return cloneRepository(repoName, null);
    }

    /**
     * Clona um repositório em uma branch específica
     */
    public Path cloneRepository(String repoName, String branch) throws IOException {
        ensureWorkspaceExists();

        Path repoPath = workspaceRoot.resolve(repoName);

        // Se já existe, apenas atualiza
        if (Files.exists(repoPath)) {
            System.out.println("Repository exists: " + repoName + " (updating)");
            updateRepository(repoPath, branch);
            return repoPath;
        }

        // Clone do repositório
        String repoUrl = config.buildRepoUrl(repoName);
        System.out.println("Cloning: " + config.getOrganization() + "/" + repoName);

        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("clone");

        if (branch != null && !branch.isEmpty()) {
            command.add("-b");
            command.add(branch);
        }

        command.add(repoUrl);
        command.add(repoPath.toString());

        int exitCode = executeCommand(command, workspaceRoot);

        if (exitCode != 0) {
            throw new IOException("Failed to clone repository: " + repoName);
        }

        System.out.println("Cloned: " + repoPath);
        return repoPath;
    }

    /**
     * Atualiza um repositório existente
     */
    public void updateRepository(Path repoPath, String branch) throws IOException {
        if (!Files.exists(repoPath.resolve(".git"))) {
            throw new IOException("Not a git repository: " + repoPath);
        }

        // Git fetch
        executeCommand(List.of("git", "fetch", "--all"), repoPath);

        // Checkout branch se especificada
        if (branch != null && !branch.isEmpty()) {
            executeCommand(List.of("git", "checkout", branch), repoPath);
        }

        // Git pull
        executeCommand(List.of("git", "pull"), repoPath);
    }

    /**
     * Lista repositórios clonados no workspace
     */
    public List<String> listClonedRepositories() throws IOException {
        List<String> repos = new ArrayList<>();

        if (!Files.exists(workspaceRoot)) {
            return repos;
        }

        try (var stream = Files.list(workspaceRoot)) {
            stream.filter(Files::isDirectory)
                  .filter(p -> Files.exists(p.resolve(".git")))
                  .forEach(p -> repos.add(p.getFileName().toString()));
        }

        return repos;
    }

    /**
     * Cria uma nova branch em um repositório
     */
    public void createBranch(Path repoPath, String branchName) throws IOException {
        executeCommand(List.of("git", "checkout", "-b", branchName), repoPath);
    }

    /**
     * Faz commit de mudanças
     */
    public void commit(Path repoPath, String message) throws IOException {
        executeCommand(List.of("git", "add", "."), repoPath);
        executeCommand(List.of("git", "commit", "-m", message), repoPath);
    }

    /**
     * Faz push para o remoto
     */
    public void push(Path repoPath, String branch) throws IOException {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("push");
        command.add("origin");
        command.add(branch);

        executeCommand(command, repoPath);
    }

    /**
     * Verifica se o workspace existe, cria se necessário
     */
    private void ensureWorkspaceExists() throws IOException {
        if (!Files.exists(workspaceRoot)) {
            Files.createDirectories(workspaceRoot);
            System.out.println("Workspace created: " + workspaceRoot);
        }
    }

    /**
     * Executa um comando Git
     */
    private int executeCommand(List<String> command, Path workingDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  " + line);
            }
        }

        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Command interrupted", e);
        }
    }

    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }

    public GitConfig getConfig() {
        return config;
    }
}
