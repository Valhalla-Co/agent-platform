package br.com.valhalla.agentbase;

import br.com.valhalla.agentbase.cli.AnalyzeCommand;
import br.com.valhalla.agentbase.cli.ImproveCommand;
import br.com.valhalla.agentbase.cli.ReviewCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "agent-base",
    mixinStandardHelpOptions = true,
    version = "agent-base 1.0.0",
    description = "CLI multi-agente para análise, melhoria e revisão de projetos",
    subcommands = {
        CreateTaskCommand.class,
        AnalyzeCommand.class,
        ImproveCommand.class,
        ReviewCommand.class
    }
)
public class RootCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("agent-base CLI v1.0.0");
        System.out.println();
        System.out.println("Comandos disponíveis:");
        System.out.println("  analyze      Analisa estrutura e qualidade de um projeto");
        System.out.println("  improve      Propõe melhorias usando agentes IA");
        System.out.println("  review       Revisão de código/arquitetura");
        System.out.println("  create-task  Cria uma tarefa na plataforma via REST");
        System.out.println();
        System.out.println("Use --help para mais detalhes de cada comando.");
    }
}
