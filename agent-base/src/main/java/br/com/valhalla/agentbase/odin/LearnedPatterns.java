package br.com.valhalla.agentbase.odin;

import java.util.*;

/**
 * Padrões aprendidos pelo Odin Agent.
 */
public class LearnedPatterns {

    private final Map<String, String> architecturalPatterns = new LinkedHashMap<>();
    private final Map<String, String> designPatterns = new LinkedHashMap<>();
    private final Map<String, String> codePatterns = new LinkedHashMap<>();

    public void addArchitecturalPattern(String name, String description) {
        architecturalPatterns.put(name, description);
    }

    public void addDesignPattern(String name, String description) {
        designPatterns.put(name, description);
    }

    public void addCodePattern(String name, String description) {
        codePatterns.put(name, description);
    }

    public Map<String, String> getArchitecturalPatterns() {
        return Collections.unmodifiableMap(architecturalPatterns);
    }

    public Map<String, String> getDesignPatterns() {
        return Collections.unmodifiableMap(designPatterns);
    }

    public Map<String, String> getCodePatterns() {
        return Collections.unmodifiableMap(codePatterns);
    }
}
