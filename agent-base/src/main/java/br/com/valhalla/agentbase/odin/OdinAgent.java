package br.com.valhalla.agentbase.odin;

import br.com.valhalla.agentbase.discovery.ProjectContext;
import br.com.valhalla.agentbase.discovery.ProjectDiscovery;

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
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     🔱 ODIN AGENT 🔱                           ║");
        System.out.println("║            Agente Especializado em Análise de Código          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("📂 Projeto: " + projectPath.toAbsolutePath());
        System.out.println("⏰ Início: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println();

        // Fase 1: Descoberta do projeto
        System.out.println("🔍 Fase 1: Descoberta do Projeto");
        System.out.println("─────────────────────────────────────────────────────────────────");
        ProjectContext context = discovery.discover(projectPath.toAbsolutePath().toString());
        System.out.println("✅ Projeto descoberto: " + context.getProjectName());
        System.out.println("   • Tipo: " + context.getProjectType());
        System.out.println("   • Arquivos analisados: " + context.getSourceFiles().size());
        System.out.println();

        // Fase 2: Aprendizado de padrões
        System.out.println("🧠 Fase 2: Aprendizado de Padrões");
        System.out.println("─────────────────────────────────────────────────────────────────");
        LearnedPatterns patterns = patternLearner.learn(context);
        System.out.println("✅ Padrões identificados:");
        System.out.println("   • Arquiteturais: " + patterns.getArchitecturalPatterns().size());
        System.out.println("   • Design: " + patterns.getDesignPatterns().size());
        System.out.println("   • Código: " + patterns.getCodePatterns().size());
        System.out.println();

        // Fase 3: Geração de Spec
        System.out.println("📝 Fase 3: Geração de Spec de Melhorias");
        System.out.println("─────────────────────────────────────────────────────────────────");
        ImprovementSpec spec = specGenerator.generate(context, patterns);
        System.out.println("✅ Spec gerada:");
        System.out.println("   • Melhorias sugeridas: " + spec.getImprovements().size());
        System.out.println("   • Prioridade alta: " + spec.getHighPriorityCount());
        System.out.println("   • Estimativa: " + spec.getTotalEstimation());
        System.out.println();

        // Salvar spec
        Path specPath = saveSpec(projectPath, spec);
        System.out.println("💾 Spec salva em: " + specPath);
        System.out.println();

        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                   ✅ ANÁLISE CONCLUÍDA ✅                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        return new OdinAnalysisResult(context, patterns, spec, specPath);
    }

    private Path saveSpec(Path projectPath, ImprovementSpec spec) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path specPath = projectPath.resolve("odin-specs").resolve("improvement-spec-" + timestamp + ".md");
        specPath.getParent().toFile().mkdirs();

        try {
            java.nio.file.Files.writeString(specPath, spec.toMarkdown());
        } catch (Exception e) {
            System.err.println("⚠️  Erro ao salvar spec: " + e.getMessage());
        }

        return specPath;
    }
}
