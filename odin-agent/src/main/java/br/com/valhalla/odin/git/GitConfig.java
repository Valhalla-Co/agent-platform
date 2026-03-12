package br.com.valhalla.odin.git;

/**
 * Configuração para gerenciamento de repositórios Git
 */
public class GitConfig {

    private static final String DEFAULT_ORGANIZATION = "Valhalla-Co";
    private static final String DEFAULT_WORKSPACE = "workspace/branches";
    private static final String GITHUB_BASE_URL = "https://github.com";

    private final String organization;
    private final String workspacePath;
    private final String githubToken;

    public GitConfig() {
        this(DEFAULT_ORGANIZATION, DEFAULT_WORKSPACE, null);
    }

    public GitConfig(String organization, String workspacePath, String githubToken) {
        this.organization = organization != null ? organization : DEFAULT_ORGANIZATION;
        this.workspacePath = workspacePath != null ? workspacePath : DEFAULT_WORKSPACE;
        this.githubToken = githubToken; // Opcional, para repos privados
    }

    public String getOrganization() {
        return organization;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public String buildRepoUrl(String repoName) {
        if (githubToken != null && !githubToken.isEmpty()) {
            return String.format("%s/%s/%s.git", GITHUB_BASE_URL, organization, repoName);
        }
        return String.format("%s/%s/%s.git", GITHUB_BASE_URL, organization, repoName);
    }

    public static GitConfig fromEnv() {
        String org = System.getenv("ODIN_GIT_ORG");
        String workspace = System.getenv("ODIN_WORKSPACE");
        String token = System.getenv("GITHUB_TOKEN");

        return new GitConfig(org, workspace, token);
    }
}
