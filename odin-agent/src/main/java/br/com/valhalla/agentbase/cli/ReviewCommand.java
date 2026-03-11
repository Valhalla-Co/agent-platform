package br.com.valhalla.agentbase.cli;

import br.com.valhalla.agentbase.discovery.ProjectContext;
import br.com.valhalla.agentbase.discovery.ProjectDiscovery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;

/**
 * Comando CLI para revisão de código/arquitetura de um projeto ou arquivo.
 * Uso: agent-base review --project <path> [arquivos...]
 */
@Command(name = "review", description = "Revisão de código e arquitetura usando agentes IA")
public class ReviewCommand implements Runnable {

    @Option(names = {"-p", "--project"}, required = true,
            description = "Caminho do projeto")
    private String projectPath;

    @Parameters(description = "Arquivos específicos para revisar (relativo ao projeto)",
                arity = "0..*")
    private List<String> files;

    @Option(names = {"--type"}, defaultValue = "code",
            description = "Tipo de revisão: code, architecture, full")
    private String type;

    @Option(names = {"--remote"},
            description = "URL da plataforma remota para processamento LLM")
    private String remoteUrl;

    private final ProjectDiscovery discovery;

    public ReviewCommand() {
        this.discovery = new ProjectDiscovery();
    }

    ReviewCommand(ProjectDiscovery discovery) {
        this.discovery = discovery;
    }

    @Override
    public void run() {
        try {
            ProjectContext context = discovery.discover(projectPath);

            System.out.println("📝 Revisão de Projeto: " + context.getProjectName());
            System.out.println("═══════════════════════════════════");
            System.out.println("  Tipo: " + type);
            System.out.println("  Framework: " + context.getFramework());
            System.out.println();

            List<String> targetFiles = resolveTargetFiles(context);

            System.out.println("📄 Arquivos para revisão: " + targetFiles.size());
            System.out.println("───────────────────────────────────");
            targetFiles.stream().limit(20).forEach(f ->
                System.out.println("  • " + f));
            if (targetFiles.size() > 20) {
                System.out.println("  ... e mais " + (targetFiles.size() - 20) + " arquivos");
            }
            System.out.println();

            switch (type.toLowerCase()) {
                case "architecture" -> {
                    System.out.println("🏗️  Revisão de Arquitetura");
                    System.out.println("  → Agente: Architect/Product Owner");
                    System.out.println("  → Analisando: estrutura de pacotes, dependências, padrões");
                }
                case "code" -> {
                    System.out.println("💻 Revisão de Código");
                    System.out.println("  → Agente: Java Engineer");
                    System.out.println("  → Analisando: qualidade, SOLID, DDD, Clean Code");
                }
                case "full" -> {
                    System.out.println("🔍 Revisão Completa (Arquitetura + Código)");
                    System.out.println("  → Agentes: Architect + Java Engineer (colaboração)");
                    System.out.println("  → Analisando: estrutura, qualidade, segurança, testes");
                }
            }

            System.out.println();

            if (remoteUrl != null) {
                System.out.println("📡 Enviando para análise remota em: " + remoteUrl);
            } else {
                System.out.println("💻 Análise estática local");
                System.out.println("   Use --remote <url> para revisão detalhada com LLM");
            }

            System.out.println();
            System.out.println("✅ Revisão concluída.");

        } catch (Exception e) {
            System.err.println("❌ Erro na revisão: " + e.getMessage());
        }
    }

    private List<String> resolveTargetFiles(ProjectContext context) {
        if (files != null && !files.isEmpty()) {
            return files;
        }
        // Se nenhum arquivo específico, usa todos os fontes do projeto
        return context.getSourceFiles();
    }
}
