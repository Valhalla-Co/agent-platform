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
    description = "Odin - Code analysis and improvement spec generator"
)
public class OdinCli implements Runnable {

    @Parameters(index = "0", description = "Caminho do projeto a ser analisado")
    private String projectPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new OdinCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        if (projectPath == null || projectPath.isEmpty()) {
            System.err.println("Error: Project path not provided");
            System.err.println("Usage: odin <project-path>");
            System.exit(1);
            return;
        }

        Path path = Paths.get(projectPath);

        if (!path.toFile().exists()) {
            System.err.println("Error: Path does not exist: " + path.toAbsolutePath());
            System.exit(1);
            return;
        }

        try {
            OdinAgent odin = new OdinAgent();
            odin.analyze(path);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
