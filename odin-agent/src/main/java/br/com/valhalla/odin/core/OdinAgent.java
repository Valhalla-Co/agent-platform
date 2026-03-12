package br.com.valhalla.odin.core;

import br.com.valhalla.odin.discovery.ProjectContext;
import br.com.valhalla.odin.discovery.ProjectDiscovery;
import br.com.valhalla.odin.patterns.LearnedPatterns;
import br.com.valhalla.odin.specs.ImprovementSpec;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Odin - Agente especializado em análise de código e geração de specs de melhoria.
 *
 * Odin aprende com os padrões encontrados no código e gera documentos de especificação
 * detalhados para planejamento de melhorias.
 */
public class OdinAgent {

    private final ProjectDiscovery discovery;
    private final PatternLearner patternLearner;
    private final SpecGenerator specGenerator;

    public OdinAgent() {
        this.discovery = new ProjectDiscovery();
        this.patternLearner = new PatternLearner();
        this.specGenerator = new SpecGenerator();
    }

    /**
     * Analisa um projeto, aprende seus padrões e gera uma spec de melhorias.
     */
    public OdinAnalysisResult analyze(Path projectPath) {
        System.out.println("\n=== ODIN AGENT - Code Analysis ===");
        System.out.println("Project: " + projectPath.toAbsolutePath());
        System.out.println("Started: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println();

        // Fase 1: Descoberta do projeto
        System.out.println("[1/3] Project Discovery");
        ProjectContext context = discovery.discover(projectPath.toAbsolutePath().toString());
        System.out.println("  Project: " + context.getProjectName() + " (" + context.getProjectType() + ")");
        System.out.println("  Files analyzed: " + context.getSourceFiles().size());
        System.out.println();

        // Fase 2: Aprendizado de padrões
        System.out.println("[2/3] Pattern Learning");
        LearnedPatterns patterns = patternLearner.learn(context);
        System.out.println("  Patterns identified: " +
            (patterns.getArchitecturalPatterns().size() +
             patterns.getDesignPatterns().size() +
             patterns.getCodePatterns().size()));
        System.out.println();

        // Fase 3: Geração de Spec
        System.out.println("[3/3] Generating Improvement Spec");
        ImprovementSpec spec = specGenerator.generate(context, patterns);
        System.out.println("  Improvements suggested: " + spec.getImprovements().size() +
                           " (High priority: " + spec.getHighPriorityCount() + ")");
        System.out.println("  Estimated effort: " + spec.getTotalEstimation());
        System.out.println();

        // Salvar spec
        Path specPath = saveSpec(projectPath, spec);
        System.out.println("Spec saved: " + specPath);
        System.out.println("\nAnalysis completed successfully.\n");

        return new OdinAnalysisResult(context, patterns, spec, specPath);
    }

    private Path saveSpec(Path projectPath, ImprovementSpec spec) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path specPath = projectPath.resolve("odin-specs").resolve("improvement-spec-" + timestamp + ".md");
        specPath.getParent().toFile().mkdirs();

        try {
            java.nio.file.Files.writeString(specPath, spec.toMarkdown());
        } catch (Exception e) {
            System.err.println("Warning: Failed to save spec - " + e.getMessage());
        }

        return specPath;
    }
}
