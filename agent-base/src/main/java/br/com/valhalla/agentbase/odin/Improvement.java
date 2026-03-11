package br.com.valhalla.agentbase.odin;

import java.util.List;

/**
 * Representa uma melhoria sugerida pelo Odin Agent.
 */
public class Improvement {

    private final String title;
    private final String description;
    private final ImprovementPriority priority;
    private final ImprovementCategory category;
    private final String estimation;
    private final List<String> actionSteps;

    public Improvement(String title, String description, ImprovementPriority priority,
                      ImprovementCategory category, String estimation, List<String> actionSteps) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.estimation = estimation;
        this.actionSteps = actionSteps;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ImprovementPriority getPriority() {
        return priority;
    }

    public ImprovementCategory getCategory() {
        return category;
    }

    public String getEstimation() {
        return estimation;
    }

    public List<String> getActionSteps() {
        return actionSteps;
    }
}
