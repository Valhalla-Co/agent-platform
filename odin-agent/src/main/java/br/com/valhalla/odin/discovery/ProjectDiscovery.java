package br.com.valhalla.odin.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

/**
 * Analisa a estrutura de um projeto-alvo e constrói um {@link ProjectContext}.
 * Permite que o agent-base seja agnóstico ao projeto — pode melhorar
 * qualquer projeto Java/Maven, Node, Python, etc.
 */
public class ProjectDiscovery {

    private static final Logger log = LoggerFactory.getLogger(ProjectDiscovery.class);

    private static final Set<String> IGNORED_DIRS = Set.of(
        "target", "build", "node_modules", ".git", ".idea", ".vscode",
        "bin", "obj", "__pycache__", ".gradle", "dist", "out"
    );

    /**
     * Descobre o contexto do projeto no caminho informado.
     */
    public ProjectContext discover(String projectPath) {
        Path root = Path.of(projectPath).toAbsolutePath().normalize();
        log.info("Descobrindo projeto em: {}", root);

        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Caminho não é um diretório válido: " + root);
        }

        ProjectContext.ProjectType type = detectProjectType(root);
        String language = detectLanguage(type, root);
        String framework = detectFramework(type, root);
        String buildTool = detectBuildTool(type);
        List<String> modules = detectModules(type, root);
        List<String> sourceFiles = collectSourceFiles(root, language);
        Map<String, Object> metadata = collectMetadata(root, type);

        String projectName = root.getFileName().toString();

        log.info("Projeto descoberto: {} [{}] - {} arquivos fonte",
            projectName, type, sourceFiles.size());

        return new ProjectContext(
            root.toString(), projectName, type, language,
            framework, buildTool, sourceFiles, modules, metadata
        );
    }

    private ProjectContext.ProjectType detectProjectType(Path root) {
        if (Files.exists(root.resolve("pom.xml"))) {
            return ProjectContext.ProjectType.JAVA_MAVEN;
        }
        if (Files.exists(root.resolve("build.gradle")) || Files.exists(root.resolve("build.gradle.kts"))) {
            return ProjectContext.ProjectType.JAVA_GRADLE;
        }
        if (Files.exists(root.resolve("package.json"))) {
            return ProjectContext.ProjectType.NODE_NPM;
        }
        if (Files.exists(root.resolve("requirements.txt")) || Files.exists(root.resolve("setup.py"))
                || Files.exists(root.resolve("pyproject.toml"))) {
            return ProjectContext.ProjectType.PYTHON_PIP;
        }
        if (Files.exists(root.resolve("*.csproj")) || Files.exists(root.resolve("*.sln"))) {
            return ProjectContext.ProjectType.DOTNET;
        }
        return ProjectContext.ProjectType.UNKNOWN;
    }

    private String detectLanguage(ProjectContext.ProjectType type, Path root) {
        return switch (type) {
            case JAVA_MAVEN, JAVA_GRADLE -> "Java";
            case NODE_NPM -> "JavaScript/TypeScript";
            case PYTHON_PIP -> "Python";
            case DOTNET -> "C#";
            case UNKNOWN -> guessLanguageFromFiles(root);
        };
    }

    private String guessLanguageFromFiles(Path root) {
        try (Stream<Path> files = Files.walk(root, 3)) {
            Set<String> extensions = new HashSet<>();
            files.filter(Files::isRegularFile)
                .forEach(p -> {
                    String name = p.getFileName().toString();
                    int dot = name.lastIndexOf('.');
                    if (dot > 0) extensions.add(name.substring(dot));
                });

            if (extensions.contains(".java")) return "Java";
            if (extensions.contains(".ts") || extensions.contains(".js")) return "JavaScript/TypeScript";
            if (extensions.contains(".py")) return "Python";
            if (extensions.contains(".cs")) return "C#";
            if (extensions.contains(".go")) return "Go";
            if (extensions.contains(".rs")) return "Rust";
        } catch (IOException e) {
            log.warn("Não foi possível analisar arquivos em: {}", root, e);
        }
        return "Unknown";
    }

    private String detectFramework(ProjectContext.ProjectType type, Path root) {
        if (type == ProjectContext.ProjectType.JAVA_MAVEN || type == ProjectContext.ProjectType.JAVA_GRADLE) {
            return detectJavaFramework(root);
        }
        if (type == ProjectContext.ProjectType.NODE_NPM) {
            return "Node.js";
        }
        if (type == ProjectContext.ProjectType.PYTHON_PIP) {
            return detectPythonFramework(root);
        }
        return "Unknown";
    }

    private String detectJavaFramework(Path root) {
        try {
            Path pomPath = root.resolve("pom.xml");
            if (Files.exists(pomPath)) {
                String content = Files.readString(pomPath);
                if (content.contains("spring-boot")) return "Spring Boot";
                if (content.contains("quarkus")) return "Quarkus";
                if (content.contains("micronaut")) return "Micronaut";
                if (content.contains("jakarta.ee") || content.contains("javax.servlet")) return "Jakarta EE";
            }
            Path gradlePath = root.resolve("build.gradle");
            if (Files.exists(gradlePath)) {
                String content = Files.readString(gradlePath);
                if (content.contains("spring-boot")) return "Spring Boot";
                if (content.contains("quarkus")) return "Quarkus";
            }
        } catch (IOException e) {
            log.warn("Não foi possível ler build file", e);
        }
        return "Java SE";
    }

    private String detectPythonFramework(Path root) {
        try {
            Path reqPath = root.resolve("requirements.txt");
            if (Files.exists(reqPath)) {
                String content = Files.readString(reqPath);
                if (content.contains("django")) return "Django";
                if (content.contains("flask")) return "Flask";
                if (content.contains("fastapi")) return "FastAPI";
            }
        } catch (IOException e) {
            log.warn("Não foi possível ler requirements.txt", e);
        }
        return "Python";
    }

    private String detectBuildTool(ProjectContext.ProjectType type) {
        return switch (type) {
            case JAVA_MAVEN -> "Maven";
            case JAVA_GRADLE -> "Gradle";
            case NODE_NPM -> "npm";
            case PYTHON_PIP -> "pip";
            case DOTNET -> "dotnet";
            case UNKNOWN -> "Unknown";
        };
    }

    private List<String> detectModules(ProjectContext.ProjectType type, Path root) {
        List<String> modules = new ArrayList<>();
        if (type == ProjectContext.ProjectType.JAVA_MAVEN) {
            // Procura subdiretórios com pom.xml
            try (Stream<Path> dirs = Files.list(root)) {
                dirs.filter(Files::isDirectory)
                    .filter(d -> Files.exists(d.resolve("pom.xml")))
                    .forEach(d -> modules.add(d.getFileName().toString()));
            } catch (IOException e) {
                log.warn("Não foi possível detectar módulos Maven", e);
            }
        }
        return modules;
    }

    private List<String> collectSourceFiles(Path root, String language) {
        List<String> files = new ArrayList<>();
        Set<String> extensions = getExtensionsForLanguage(language);

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    String dirName = dir.getFileName().toString();
                    if (IGNORED_DIRS.contains(dirName)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String fileName = file.getFileName().toString();
                    int dot = fileName.lastIndexOf('.');
                    if (dot > 0 && extensions.contains(fileName.substring(dot))) {
                        files.add(root.relativize(file).toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.warn("Erro ao coletar arquivos fonte", e);
        }

        return files;
    }

    private Set<String> getExtensionsForLanguage(String language) {
        return switch (language) {
            case "Java" -> Set.of(".java");
            case "JavaScript/TypeScript" -> Set.of(".js", ".ts", ".jsx", ".tsx");
            case "Python" -> Set.of(".py");
            case "C#" -> Set.of(".cs");
            case "Go" -> Set.of(".go");
            case "Rust" -> Set.of(".rs");
            default -> Set.of(".java", ".py", ".js", ".ts");
        };
    }

    private Map<String, Object> collectMetadata(Path root, ProjectContext.ProjectType type) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("hasGit", Files.exists(root.resolve(".git")));
        meta.put("hasDocker", Files.exists(root.resolve("Dockerfile"))
            || Files.exists(root.resolve("docker-compose.yml")));
        meta.put("hasCI", Files.exists(root.resolve(".github/workflows"))
            || Files.exists(root.resolve(".gitlab-ci.yml"))
            || Files.exists(root.resolve("Jenkinsfile")));
        meta.put("hasTests", detectTestsPresence(root, type));
        return meta;
    }

    private boolean detectTestsPresence(Path root, ProjectContext.ProjectType type) {
        if (type == ProjectContext.ProjectType.JAVA_MAVEN) {
            return Files.exists(root.resolve("src/test"));
        }
        if (type == ProjectContext.ProjectType.NODE_NPM) {
            return Files.exists(root.resolve("test")) || Files.exists(root.resolve("__tests__"));
        }
        if (type == ProjectContext.ProjectType.PYTHON_PIP) {
            return Files.exists(root.resolve("tests")) || Files.exists(root.resolve("test"));
        }
        return false;
    }
}
