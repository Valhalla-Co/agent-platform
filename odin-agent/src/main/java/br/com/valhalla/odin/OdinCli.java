package br.com.valhalla.odin;

import br.com.valhalla.odin.core.OdinAgent;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;

@Command(
    name = "odin",
    mixinStandardHelpOptions = true,
    version = "Odin Agent 1.0.0",
    description = "🔱 Odin - Agente especializado em análise de código e geração de specs de melhoria"
)
public class AgentBaseCli implements Runnable {

    @Parameters(index = "0", description = "Caminho do projeto a ser analisado")
    private String projectPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new AgentBaseCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        if (projectPath == null || projectPath.isEmpty()) {
            System.err.println("❌ Erro: Caminho do projeto não fornecido");
            System.err.println();
            System.err.println("Uso: odin <caminho-do-projeto>");
            System.err.println();
            System.err.println("Exemplo:");
            System.err.println("  odin .");
            System.err.println("  odin C:\\projetos\\meu-projeto");
            System.exit(1);
            return;
        }

        Path path = Paths.get(projectPath);

        if (!path.toFile().exists()) {
            System.err.println("❌ Erro: Caminho não existe: " + path.toAbsolutePath());
            System.exit(1);
            return;
        }

        try {
            OdinAgent odin = new OdinAgent();
            odin.analyze(path);
        } catch (Exception e) {
            System.err.println("❌ Erro durante análise: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
