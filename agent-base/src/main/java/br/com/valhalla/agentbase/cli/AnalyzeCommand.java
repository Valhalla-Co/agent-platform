package br.com.valhalla.agentbase.cli;

import br.com.valhalla.agentbase.discovery.ProjectContext;
import br.com.valhalla.agentbase.discovery.ProjectDiscovery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Comando CLI para analisar a estrutura e qualidade de qualquer projeto.
 * Uso: agent-base analyze --project <path> [--depth deep|shallow]
 */
@Command(name = "analyze", description = "Analisa a estrutura e qualidade de um projeto")
public class AnalyzeCommand implements Runnable {

    @Option(names = {"-p", "--project"}, required = true,
            description = "Caminho do projeto a ser analisado")
    private String projectPath;

    @Option(names = {"--depth"}, defaultValue = "shallow",
            description = "Profundidade da análise: shallow ou deep")
    private String depth;

    @Option(names = {"--output"}, defaultValue = "console",
            description = "Formato de saída: console, json ou markdown")
    private String output;

    private final ProjectDiscovery discovery;

    public AnalyzeCommand() {
        this.discovery = new ProjectDiscovery();
    }

    // Construtor para testes
    AnalyzeCommand(ProjectDiscovery discovery) {
        this.discovery = discovery;
    }

    @Override
    public void run() {
        try {
            System.out.println("🔍 Analisando projeto em: " + projectPath);
            System.out.println();

            ProjectContext context = discovery.discover(projectPath);

            System.out.println("📋 Resumo do Projeto");
            System.out.println("═══════════════════════════════════");
            System.out.println("  Nome:       " + context.getProjectName());
            System.out.println("  Tipo:       " + context.getProjectType());
            System.out.println("  Linguagem:  " + context.getLanguage());
            System.out.println("  Framework:  " + context.getFramework());
            System.out.println("  Build Tool: " + context.getBuildTool());
            System.out.println("  Módulos:    " + context.getModules().size());
            System.out.println("  Arquivos:   " + context.getSourceFiles().size());
            System.out.println();

            System.out.println("📦 Metadados");
            System.out.println("───────────────────────────────────");
            context.getMetadata().forEach((k, v) ->
                System.out.println("  " + k + ": " + v));
            System.out.println();

            if (!context.getModules().isEmpty()) {
                System.out.println("📁 Módulos");
                System.out.println("───────────────────────────────────");
                context.getModules().forEach(m ->
                    System.out.println("  • " + m));
                System.out.println();
            }

            if ("deep".equalsIgnoreCase(depth)) {
                System.out.println("📄 Arquivos Fonte (" + context.getSourceFiles().size() + ")");
                System.out.println("───────────────────────────────────");
                context.getSourceFiles().stream()
                    .sorted()
                    .forEach(f -> System.out.println("  " + f));
                System.out.println();
            }

            System.out.println("✅ Análise concluída.");
            System.out.println();
            System.out.println("💡 Para melhorar o projeto, use: agent-base improve --project " + projectPath);

        } catch (Exception e) {
            System.err.println("❌ Erro ao analisar projeto: " + e.getMessage());
        }
    }
}
