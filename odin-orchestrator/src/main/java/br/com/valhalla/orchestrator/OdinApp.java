package br.com.valhalla.orchestrator;

import br.com.valhalla.providers.ollama.OllamaProvider;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * OdinApp: única entrada para input do usuário.
 * Fluxo: OdinApp -> Odin (persona) -> OdinOrchestrator -> agentes (Wayland/Mimir).
 */
public class OdinApp {

    public static void main(String[] args) {
        try {
            printBanner();

            Config cfg = parseArgs(args);
            String defaultRepo = System.getProperty("user.dir");
            cfg.repoPath = (cfg.repoPath == null || cfg.repoPath.isBlank()) ? defaultRepo : cfg.repoPath;

            // Environment overrides (useful for automation / running via Maven)
            // Example:
            //   set ODINAPP_PROMPT=...
            //   set ODINAPP_REPO=.
            //   .\mvnw.cmd -pl odin-orchestrator -am exec:java
            applyEnvOverrides(cfg);

            if (cfg.prompt != null) {
                Odin odin = new Odin();
                String out = odin.handleUserRequest(
                        cfg.prompt,
                        Paths.get(cfg.repoPath),
                        cfg.intent,
                        cfg.personaPrompt,
                        cfg.allowedAgentsOverride
                );
                System.out.println(out);
                return;
            }

            // Interactive mode: input only via this app.
            Scanner scanner = new Scanner(System.in);
            Odin odin = new Odin();

            System.out.println("OdinApp: pronto. Informe seu pedido e pressione Enter.");
            System.out.println("Comandos: :help, :repo <path>, :intent <intent>, :agents <Wayland,Mimir>, :persona <text>, :reset, exit");
            if (!cfg.silentProviderInfo) {
                System.out.println("LLM (Ollama) disponível: " + new OllamaProvider().isAvailable());
            }

            while (true) {
                System.out.print("\nodinapp> ");
                String line = scanner.nextLine();
                if (line == null) continue;
                String prompt = line.trim();

                if (prompt.isEmpty()) continue;
                if (prompt.equalsIgnoreCase("exit") || prompt.equalsIgnoreCase("quit") || prompt.equalsIgnoreCase("sair")) {
                    System.out.println("OdinApp: encerrando...");
                    break;
                }

                if (prompt.startsWith(":")) {
                    boolean ok = handleCommand(prompt.substring(1).trim(), cfg, defaultRepo);
                    if (!ok) System.out.println("OdinApp: comando desconhecido. Digite :help");
                    continue;
                }

                String out = odin.handleUserRequest(
                        prompt,
                        Paths.get(cfg.repoPath),
                        cfg.intent,
                        cfg.personaPrompt,
                        cfg.allowedAgentsOverride
                );
                System.out.println(out);
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printBanner() {
        System.out.println("""

╔══════════════════════════════════════════════════════════════╗
║                    ODIN APP (Orchestrator)                  ║
║              Input -> Odin (persona) -> Agents             ║
╚══════════════════════════════════════════════════════════════╝
""");
    }

    private static boolean handleCommand(String command, Config cfg, String defaultRepo) {
        if (command.equalsIgnoreCase("help")) {
            System.out.println("""
Comandos:
  :help
  :repo <path>        define diretório do repo
  :intent <intent>   exemplo: analyze|refactor|review|build|test (opcional)
  :agents <list>     exemplo: Wayland,Mimir (opcional; se omitido, Odin decide)
  :persona <text>    persona custom (opcional)
  :reset             reseta intent/persona/agents e repo para o diretório atual
  :status            mostra status atual
""");
            return true;
        }

        if (command.equalsIgnoreCase("reset")) {
            cfg.repoPath = defaultRepo;
            cfg.intent = null;
            cfg.personaPrompt = null;
            cfg.allowedAgentsOverride = null;
            System.out.println("OdinApp: config resetada.");
            return true;
        }

        if (command.equalsIgnoreCase("status")) {
            System.out.println("OdinApp status:");
            System.out.println("  repoPath=" + cfg.repoPath);
            System.out.println("  intent=" + cfg.intent);
            System.out.println("  personaPrompt=" + (cfg.personaPrompt == null ? "<default>" : "<custom>"));
            System.out.println("  agentsOverride=" + cfg.allowedAgentsOverride);
            return true;
        }

        if (command.startsWith("repo ")) {
            String v = command.substring("repo ".length()).trim();
            if (!v.isEmpty()) cfg.repoPath = v;
            System.out.println("OdinApp: repoPath=" + cfg.repoPath);
            return true;
        }

        if (command.startsWith("intent ")) {
            String v = command.substring("intent ".length()).trim();
            cfg.intent = v.isEmpty() ? null : v;
            System.out.println("OdinApp: intent=" + cfg.intent);
            return true;
        }

        if (command.startsWith("agents ")) {
            String v = command.substring("agents ".length()).trim();
            cfg.allowedAgentsOverride = v.isEmpty() ? null : parseAgentsOverride(v);
            System.out.println("OdinApp: agentsOverride=" + cfg.allowedAgentsOverride);
            return true;
        }

        if (command.startsWith("persona ")) {
            String v = command.substring("persona ".length()).trim();
            cfg.personaPrompt = v.isEmpty() ? null : v;
            System.out.println("OdinApp: personaPrompt=" + (cfg.personaPrompt == null ? "<default>" : (cfg.personaPrompt.length() > 40 ? cfg.personaPrompt.substring(0, 40) + "..." : cfg.personaPrompt)));
            return true;
        }

        return false;
    }

    private static void applyEnvOverrides(Config cfg) {
        String p = System.getenv().getOrDefault("ODINAPP_PROMPT", "").trim();
        if (!p.isBlank()) cfg.prompt = p;

        String repo = System.getenv().getOrDefault("ODINAPP_REPO", "").trim();
        if (!repo.isBlank()) cfg.repoPath = repo;

        String intent = System.getenv().getOrDefault("ODINAPP_INTENT", "").trim();
        if (!intent.isBlank()) cfg.intent = intent;

        String persona = System.getenv().getOrDefault("ODINAPP_PERSONA", "").trim();
        if (!persona.isBlank()) cfg.personaPrompt = persona;

        String agents = System.getenv().getOrDefault("ODINAPP_AGENTS", "").trim();
        if (!agents.isBlank()) cfg.allowedAgentsOverride = parseAgentsOverride(agents);
    }

    private static List<String> parseAgentsOverride(String raw) {
        String v = raw.trim();
        if (v.equalsIgnoreCase("both")) return List.of("Wayland", "Mimir");
        if (v.equalsIgnoreCase("wayland")) return List.of("Wayland");
        if (v.equalsIgnoreCase("mimir")) return List.of("Mimir");

        String[] parts = v.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (t.isEmpty()) continue;
            if (t.equalsIgnoreCase("wayland")) t = "Wayland";
            if (t.equalsIgnoreCase("mimir")) t = "Mimir";
            out.add(t);
        }
        return out;
    }

    private static Config parseArgs(String[] args) {
        Config cfg = new Config();

        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a == null) continue;

            if (a.equalsIgnoreCase("--prompt") && i + 1 < args.length) {
                cfg.prompt = args[++i];
                continue;
            }
            if (a.equalsIgnoreCase("--repo") && i + 1 < args.length) {
                cfg.repoPath = args[++i];
                continue;
            }
            if (a.equalsIgnoreCase("--intent") && i + 1 < args.length) {
                cfg.intent = args[++i];
                continue;
            }
            if (a.equalsIgnoreCase("--persona") && i + 1 < args.length) {
                cfg.personaPrompt = args[++i];
                continue;
            }
            if (a.equalsIgnoreCase("--agents") && i + 1 < args.length) {
                cfg.allowedAgentsOverride = parseAgentsOverride(args[++i]);
                continue;
            }
            if (a.equalsIgnoreCase("--silent-provider-info")) {
                cfg.silentProviderInfo = true;
                continue;
            }
        }

        return cfg;
    }

    private static class Config {
        String prompt;
        String repoPath;
        String intent;
        String personaPrompt;
        List<String> allowedAgentsOverride;
        boolean silentProviderInfo = false;
    }
}
