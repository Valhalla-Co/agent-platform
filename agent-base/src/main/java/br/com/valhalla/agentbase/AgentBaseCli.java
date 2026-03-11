package br.com.valhalla.agentbase;

import picocli.CommandLine;

public class AgentBaseCli {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new RootCommand()).execute(args);
        System.exit(exitCode);
    }
}
