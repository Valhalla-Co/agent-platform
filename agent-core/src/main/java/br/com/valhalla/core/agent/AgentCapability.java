package br.com.valhalla.core.agent;

import java.util.HashSet;
import java.util.Set;

/**
 * Define as habilidades (capabilities) de um agente.
 */
public class AgentCapability {
    private final Set<String> skills;
    private final Set<String> technologies;
    private final int priority;

    public AgentCapability() {
        this(new HashSet<>(), new HashSet<>(), 0);
    }

    public AgentCapability(Set<String> skills, Set<String> technologies, int priority) {
        this.skills = (skills == null) ? new HashSet<>() : new HashSet<>(skills);
        this.technologies = (technologies == null) ? new HashSet<>() : new HashSet<>(technologies);
        this.priority = priority;
    }

    /**
     * Verifica se o agente tem a habilidade especificada.
     */
    public boolean hasSkill(String skill) {
        return skills.contains(skill);
    }

    /**
     * Verifica se o agente suporta a tecnologia especificada.
     */
    public boolean supportsTechnology(String technology) {
        return technologies.contains(technology);
    }

    /**
     * Verifica se o agente pode processar a tarefa baseado em keywords.
     */
    public boolean canHandle(String task) {
        String lowerTask = task.toLowerCase();

        // Verifica se alguma skill está presente na tarefa
        for (String skill : skills) {
            if (lowerTask.contains(skill.toLowerCase())) {
                return true;
            }
        }

        // Verifica se alguma tecnologia está presente na tarefa
        for (String tech : technologies) {
            if (lowerTask.contains(tech.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    // Getters
    public Set<String> getSkills() {
        return new HashSet<>(skills);
    }

    public Set<String> getTechnologies() {
        return new HashSet<>(technologies);
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "AgentCapability{" +
                "skills=" + skills +
                ", technologies=" + technologies +
                ", priority=" + priority +
                '}';
    }
}
