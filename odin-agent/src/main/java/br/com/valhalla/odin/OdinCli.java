package br.com.valhalla.odin;

import br.com.valhalla.odin.core.OdinAgent;
import br.com.valhalla.odin.llm.LLMResponse;
import br.com.valhalla.odin.llm.OllamaProvider;
import picocli.CommandLine.Command;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

@Command(
    name = "odin",
    mixinStandardHelpOptions = true,
    version = "Odin Agent 1.0.0",
    description = "Odin - Agente autônomo para análise de código e execução de tarefas"
)
public class OdinCli implements Runnable {

    public static void main(String[] args) {
        new OdinCli().run();
    }

    @Override
    public void run() {
        // OdinCli era um input interativo. Para manter o fluxo "input apenas pelo app",
        // desabilitamos por padrão e pedimos que o usuário use o OdinApp.
        String enableCli = System.getenv().getOrDefault("ODIN_CLI_ENABLE", "false");
        if (!Boolean.parseBoolean(enableCli)) {
            System.out.println("OdinCli: entrada interativa desabilitada por padrão.");
            System.out.println("Use o OdinApp (módulo `odin-orchestrator`) para enviar prompts.");
            return;
        }
        printBanner();
        startListening();
    }

    private void printBanner() {
        System.out.println("""

                ╔══════════════════════════════════════════════════════════════╗
                ║                                                              ║
                ║   ██████╗ ██████╗ ██╗███╗   ██╗                             ║
                ║  ██╔═══██╗██╔══██╗██║████╗  ██║                             ║
                ║  ██║   ██║██║  ██║██║██╔██╗ ██║                             ║
                ║  ██║   ██║██║  ██║██║██║╚██╗██║                             ║
                ║  ╚██████╔╝██████╔╝██║██║ ╚████║                             ║
                ║   ╚═════╝ ╚═════╝ ╚═╝╚═╝  ╚═══╝                             ║
                ║                                                              ║
                ║             Agente Autônomo de Análise de Código            ║
                ║                      Versão 1.0.0                            ║
                ║                                                              ║
                ╚══════════════════════════════════════════════════════════════╝
                """);
    }

    private void startListening() {
        System.out.println("🔱 Status: INICIALIZADO");
        System.out.println("👂 Modo: ESCUTANDO");

        // Verificar status do Ollama
        OllamaProvider llm = new OllamaProvider();
        if (llm.isAvailable()) {
            System.out.println("🦙 LLM: " + llm.getProviderName() + " ✅");
            System.out.println("💚 Custo: $0.00 - Gratuito!");
        } else {
            System.out.println("⚠️  LLM: Ollama não detectado");
            System.out.println("   Instale: https://ollama.ai/download/windows");
        }

        System.out.println("\n💡 Odin está pronto para receber seus prompts.");
        System.out.println("   Digite suas solicitações naturalmente.\n");

        Scanner scanner = new Scanner(System.in);
        OdinAgent odin = new OdinAgent();
        String workingDir = System.getProperty("user.dir");

        while (true) {
            System.out.print("\n🔱 odin> ");
            String prompt = scanner.nextLine().trim();

            if (prompt.isEmpty()) {
                continue;
            }

            // Comandos de controle
            if (prompt.equalsIgnoreCase("exit") || prompt.equalsIgnoreCase("quit") || prompt.equalsIgnoreCase("sair")) {
                System.out.println("\n👋 Odin finalizando... Até logo!\n");
                break;
            }

            if (prompt.equalsIgnoreCase("status")) {
                showStatus(workingDir);
                continue;
            }

            if (prompt.equalsIgnoreCase("help") || prompt.equalsIgnoreCase("ajuda")) {
                showHelp();
                continue;
            }

            // Processar prompt
            try {
                System.out.println("\n🤔 Processando: \"" + prompt + "\"");
                processPrompt(prompt, odin, workingDir);
            } catch (Exception e) {
                System.err.println("\n❌ Erro ao processar prompt: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private void processPrompt(String prompt, OdinAgent odin, String workingDir) {
        // Inicializar Ollama
        OllamaProvider llm = new OllamaProvider();

        // Verificar se Ollama está disponível
        if (!llm.isAvailable()) {
            System.out.println("\n⚠️  Ollama não está disponível.");
            System.out.println("   Certifique-se que:");
            System.out.println("   1. Ollama está instalado");
            System.out.println("   2. Ollama está rodando (ollama serve)");
            System.out.println("   3. Um modelo está instalado (ollama pull codellama:7b)");
            System.out.println("\n   Usando análise rule-based...\n");
            processWithoutLLM(prompt, odin, workingDir);
            return;
        }

        String lowerPrompt = prompt.toLowerCase();

        // Análise de código com LLM
        if (lowerPrompt.contains("analisa") || lowerPrompt.contains("analyze") ||
            lowerPrompt.contains("análise") || lowerPrompt.contains("analysis") ||
            lowerPrompt.contains("examina") || lowerPrompt.contains("examine")) {

            System.out.println("\n🤔 Processando com Ollama (" + llm.getProviderName() + ")...");
            System.out.println("⏳ Isso pode levar 10-30 segundos...\n");

            String analysisPrompt = String.format(
                "Analise o projeto Java localizado em '%s' e sugira 3-5 melhorias específicas e práticas. " +
                "Foque em arquitetura, testes, documentação e qualidade de código.",
                workingDir
            );

            LLMResponse response = llm.chat(analysisPrompt);

            if (response.isSuccess()) {
                System.out.println("📊 Análise do Ollama:\n");
                System.out.println(response.getContent());
                System.out.println("\n⚡ Tempo: " + (response.getResponseTimeMs() / 1000.0) + "s | Custo: $0.00");
            } else {
                System.err.println("❌ Erro: " + response.getError());
                System.out.println("\n📂 Executando análise rule-based...\n");
                String path = extractPath(prompt, workingDir);
                odin.analyze(Paths.get(path));
            }
            return;
        }

        // Status do projeto
        if (lowerPrompt.contains("status do projeto") || lowerPrompt.contains("project status")) {
            showProjectStatus(workingDir);
            return;
        }

        // Listar specs
        if (lowerPrompt.contains("listar specs") || lowerPrompt.contains("list specs") ||
            lowerPrompt.contains("mostrar specs") || lowerPrompt.contains("show specs")) {
            listSpecs(workingDir);
            return;
        }

        // Pergunta genérica - usar LLM
        System.out.println("\n🤔 Processando com Ollama...\n");

        LLMResponse response = llm.chat(prompt);

        if (response.isSuccess()) {
            System.out.println(response.getContent());
            System.out.println("\n⚡ Tempo: " + (response.getResponseTimeMs() / 1000.0) + "s");
        } else {
            System.err.println("❌ Erro: " + response.getError());
        }
    }

    /**
     * Processa prompt sem LLM (fallback).
     */
    private void processWithoutLLM(String prompt, OdinAgent odin, String workingDir) {
        String lowerPrompt = prompt.toLowerCase();

        if (lowerPrompt.contains("analisa") || lowerPrompt.contains("analyze")) {
            String path = extractPath(prompt, workingDir);
            System.out.println("📂 Analisando projeto em: " + path + "\n");
            odin.analyze(Paths.get(path));
            return;
        }

        System.out.println("\n💭 Para usar respostas inteligentes, instale o Ollama:");
        System.out.println("   https://ollama.ai/download/windows\n");
    }

    private String extractPath(String prompt, String defaultPath) {
        // Tentar extrair caminho do prompt
        String[] words = prompt.split("\\s+");
        for (String word : words) {
            if (word.contains("/") || word.contains("\\") || word.equals(".")) {
                return word;
            }
        }
        return defaultPath;
    }

    private void showStatus(String workingDir) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║          STATUS DO ODIN AGENT                    ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("  🟢 Status: ATIVO");
        System.out.println("  👂 Modo: ESCUTANDO");
        System.out.println("  📂 Diretório: " + workingDir);
        System.out.println("  🔱 Versão: 1.0.0");
    }

    private void showHelp() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║          GUIA DE USO DO ODIN                     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("\n💡 Exemplos de prompts:\n");
        System.out.println("  • \"analisa o projeto\"");
        System.out.println("  • \"analyze this codebase\"");
        System.out.println("  • \"examina o código em ./src\"");
        System.out.println("  • \"listar specs geradas\"");
        System.out.println("  • \"status do projeto\"");
        System.out.println("\n🎮 Comandos de controle:\n");
        System.out.println("  • status  - Mostra status do Odin");
        System.out.println("  • help    - Mostra esta ajuda");
        System.out.println("  • exit    - Finaliza o Odin");
    }

    private void showProjectStatus(String workingDir) {
        System.out.println("\n📊 Status do Projeto: " + workingDir);

        Path specsDir = Paths.get(workingDir, "odin-specs");
        if (specsDir.toFile().exists()) {
            long specCount = Objects.requireNonNull(specsDir.toFile().listFiles()).length;
            System.out.println("  📄 Specs geradas: " + specCount);
        } else {
            System.out.println("  📄 Specs geradas: 0");
        }
    }

    private void listSpecs(String workingDir) {
        Path specsDir = Paths.get(workingDir, "odin-specs");
        if (!specsDir.toFile().exists()) {
            System.out.println("\n📭 Nenhuma spec encontrada.");
            return;
        }

        System.out.println("\n📄 Specs Geradas:\n");
        java.io.File[] specs = specsDir.toFile().listFiles();
        if (specs != null && specs.length > 0) {
            for (int i = 0; i < specs.length; i++) {
                System.out.println("  " + (i + 1) + ". " + specs[i].getName());
            }
        } else {
            System.out.println("  Nenhuma spec encontrada.");
        }
    }

}