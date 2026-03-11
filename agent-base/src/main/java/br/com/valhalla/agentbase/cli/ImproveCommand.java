package br.com.valhalla.agentbase.cli;

import br.com.valhalla.agentbase.discovery.ProjectContext;
import br.com.valhalla.agentbase.discovery.ProjectDiscovery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Comando CLI para propor melhorias em qualquer projeto usando agentes.
 * Uso: agent-base improve --project <path> [--focus architecture|code|tests]
 */
@Command(name = "improve", description = "Propõe melhorias para o projeto usando agentes IA")
public class ImproveCommand implements Runnable {

    @Option(names = {"-p", "--project"}, required = true,
            description = "Caminho do projeto a ser melhorado")
    private String projectPath;

    @Option(names = {"--focus"}, defaultValue = "all",
            description = "Foco da melhoria: all, architecture, code, tests, security")
    private String focus;

    @Option(names = {"--remote"},
            description = "URL da plataforma remota para processamento LLM")
    private String remoteUrl;

    private final ProjectDiscovery discovery;

    public ImproveCommand() {
        this.discovery = new ProjectDiscovery();
    }

    ImproveCommand(ProjectDiscovery discovery) {
        this.discovery = discovery;
    }

    @Override
    public void run() {
        try {
            System.out.println("🚀 Analisando projeto para melhorias...");
            System.out.println();

            ProjectContext context = discovery.discover(projectPath);

            System.out.println("📋 Projeto: " + context.getProjectName()
                + " [" + context.getFramework() + "]");
            System.out.println("🎯 Foco: " + focus);
            System.out.println();

            // Determinar quais agentes usar baseado no foco
            switch (focus.toLowerCase()) {
                case "architecture" -> proposeArchitectureImprovements(context);
                case "code" -> proposeCodeImprovements(context);
                case "tests" -> proposeTestImprovements(context);
                case "security" -> proposeSecurityImprovements(context);
                default -> proposeAllImprovements(context);
            }

            if (remoteUrl != null) {
                System.out.println("📡 Modo remoto: enviando para " + remoteUrl);
                System.out.println("   (Integração LLM remota será usada para análise detalhada)");
            } else {
                System.out.println("💻 Modo local: usando análise estática");
                System.out.println("   Use --remote <url> para análise por LLM");
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao propor melhorias: " + e.getMessage());
        }
    }

    private void proposeArchitectureImprovements(ProjectContext context) {
        System.out.println("🏗️  Melhorias de Arquitetura");
        System.out.println("═══════════════════════════════════");
        System.out.println("  → Agente: Architect/Product Owner");
        System.out.println("  → Capacidades: ARCHITECTURE_REVIEW, SYSTEM_DESIGN");
        System.out.println("  → Módulos analisados: " + context.getModules().size());
        System.out.println();
    }

    private void proposeCodeImprovements(ProjectContext context) {
        System.out.println("💻 Melhorias de Código");
        System.out.println("═══════════════════════════════════");
        System.out.println("  → Agente: Java Engineer");
        System.out.println("  → Capacidades: CODE_ANALYSIS, REFACTORING");
        System.out.println("  → Arquivos para análise: " + context.getSourceFiles().size());
        System.out.println();
    }

    private void proposeTestImprovements(ProjectContext context) {
        System.out.println("🧪 Melhorias de Testes");
        System.out.println("═══════════════════════════════════");
        boolean hasTests = Boolean.TRUE.equals(context.getMetadata().get("hasTests"));
        System.out.println("  → Testes existentes: " + (hasTests ? "Sim" : "Não"));
        System.out.println("  → Agente: Java Engineer");
        System.out.println("  → Capacidades: CODE_GENERATION (testes)");
        System.out.println();
    }

    private void proposeSecurityImprovements(ProjectContext context) {
        System.out.println("🔒 Melhorias de Segurança");
        System.out.println("═══════════════════════════════════");
        System.out.println("  → Agentes: Architect + Java Engineer (colaboração)");
        System.out.println("  → Capacidades: ARCHITECTURE_REVIEW, CODE_ANALYSIS");
        System.out.println();
    }

    private void proposeAllImprovements(ProjectContext context) {
        proposeArchitectureImprovements(context);
        proposeCodeImprovements(context);
        proposeTestImprovements(context);
        proposeSecurityImprovements(context);
    }
}
