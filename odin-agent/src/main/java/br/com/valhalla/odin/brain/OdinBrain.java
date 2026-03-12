package br.com.valhalla.odin.brain;

import br.com.valhalla.odin.discovery.ProjectContext;
import br.com.valhalla.odin.git.RepositoryManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * O "cérebro" do Odin Agent.
 * Gerencia o conhecimento através de lessons e aplica durante análises.
 * Também gerencia repositórios para aplicar mudanças.
 */
public class OdinBrain {

    private final LessonRepository repository;
    private final RepositoryManager repoManager;
    private boolean initialized = false;

    public OdinBrain() {
        // Por padrão, procura odin-brain no diretório atual
        this(Paths.get("odin-brain"));
    }

    public OdinBrain(Path brainPath) {
        this.repository = new LessonRepository(brainPath);
        this.repoManager = new RepositoryManager();
    }

    /**
     * Inicializa o cérebro carregando todas as lessons
     */
    public void initialize() {
        try {
            repository.load();
            initialized = true;

            if (repository.size() > 0) {
                System.out.println("Brain loaded: " + repository.size() + " lessons");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize brain: " + e.getMessage());
        }
    }

    /**
     * Aprende com lessons relevantes para o contexto do projeto
     */
    public List<String> learn(ProjectContext context) {
        if (!initialized) {
            initialize();
        }

        List<String> insights = new ArrayList<>();

        // Busca lessons relevantes
        List<Lesson> relevantLessons = repository.findRelevant(
            context.getProjectType().toString(),
            context.getLanguage(),
            List.of() // Por enquanto sem filtro de tags
        );

        // Extrai insights das lessons
        for (Lesson lesson : relevantLessons) {
            String insight = extractInsight(lesson, context);
            if (insight != null && !insight.isBlank()) {
                insights.add(insight);
            }
        }

        return insights;
    }

    /**
     * Extrai insight específico de uma lesson para o contexto
     */
    private String extractInsight(Lesson lesson, ProjectContext context) {
        // Por enquanto, retorna o título da lesson como insight
        // Futuramente pode usar LLM para extrair insights mais específicos
        return lesson.getTitle();
    }

    /**
     * Busca lessons por categoria
     */
    public List<Lesson> getLessonsByCategory(String category) {
        if (!initialized) {
            initialize();
        }
        return repository.findByCategory(category);
    }

    /**
     * Retorna todas as lessons carregadas
     */
    public List<Lesson> getAllLessons() {
        if (!initialized) {
            initialize();
        }
        return repository.getAll();
    }

    /**
     * Retorna resumo do conhecimento carregado
     */
    public String getKnowledgeSummary() {
        if (!initialized) {
            return "Brain not initialized";
        }

        int total = repository.size();
        if (total == 0) {
            return "No lessons loaded";
        }

        List<String> categories = repository.getAll().stream()
            .map(Lesson::getCategory)
            .distinct()
            .collect(Collectors.toList());

        return String.format("%d lessons in %d categories: %s",
            total, categories.size(), String.join(", ", categories));
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Clona um repositório da organização Valhalla-Co
     */
    public Path cloneRepository(String repoName) throws IOException {
        return repoManager.cloneRepository(repoName);
    }

    /**
     * Clona um repositório em uma branch específica
     */
    public Path cloneRepository(String repoName, String branch) throws IOException {
        return repoManager.cloneRepository(repoName, branch);
    }

    /**
     * Lista repositórios clonados no workspace
     */
    public List<String> listWorkspaceRepositories() throws IOException {
        return repoManager.listClonedRepositories();
    }

    /**
     * Aplica conhecimento do Odin em um repositório
     * Clona o repo, analisa, gera spec e cria branch com melhorias
     */
    public Path applyKnowledgeToRepository(String repoName) throws IOException {
        System.out.println("Applying Odin knowledge to: " + repoName);

        // 1. Clone o repositório
        Path repoPath = cloneRepository(repoName);

        // 2. Cria branch para mudanças
        String branchName = "odin/improvements-" + System.currentTimeMillis();
        repoManager.createBranch(repoPath, branchName);
        System.out.println("Created branch: " + branchName);

        return repoPath;
    }

    /**
     * Retorna o RepositoryManager para operações avançadas
     */
    public RepositoryManager getRepositoryManager() {
        return repoManager;
    }

    /**
     * Retorna o caminho do workspace
     */
    public Path getWorkspacePath() {
        return repoManager.getWorkspaceRoot();
    }
}
