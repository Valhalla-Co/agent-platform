package br.com.valhalla.odin.specs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Spec de Melhoria gerada pelo Odin Agent.
 */
public class ImprovementSpec {

    private final String projectName;
    private final LocalDateTime generatedAt;
    private final List<Improvement> improvements;

    public ImprovementSpec(String projectName) {
        this.projectName = projectName;
        this.generatedAt = LocalDateTime.now();
        this.improvements = new ArrayList<>();
    }

    public void addImprovement(Improvement improvement) {
        improvements.add(improvement);
    }

    public List<Improvement> getImprovements() {
        return Collections.unmodifiableList(improvements);
    }

    public long getHighPriorityCount() {
        return improvements.stream()
            .filter(i -> i.getPriority() == ImprovementPriority.HIGH)
            .count();
    }

    public String getTotalEstimation() {
        // Simplificação: somar horas
        int totalHours = improvements.stream()
            .mapToInt(i -> parseHours(i.getEstimation()))
            .sum();
        return totalHours + "h";
    }

    private int parseHours(String estimation) {
        try {
            return Integer.parseInt(estimation.replace("h", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public String toMarkdown() {
        StringBuilder md = new StringBuilder();

        // Header
        md.append("# 📋 Spec de Melhorias - ").append(projectName).append("\n\n");
        md.append("**Gerado por:** Odin Agent 🔱\n\n");
        md.append("**Data:** ").append(generatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");
        md.append("---\n\n");

        // Sumário Executivo
        md.append("## 📊 Sumário Executivo\n\n");
        md.append("- **Total de Melhorias:** ").append(improvements.size()).append("\n");
        md.append("- **Prioridade Alta:** ").append(getHighPriorityCount()).append("\n");
        md.append("- **Estimativa Total:** ").append(getTotalEstimation()).append("\n\n");

        // Distribuição por Categoria
        md.append("### Distribuição por Categoria\n\n");
        Map<ImprovementCategory, Long> byCategory = improvements.stream()
            .collect(Collectors.groupingBy(Improvement::getCategory, Collectors.counting()));

        byCategory.forEach((category, count) ->
            md.append("- **").append(category).append(":** ").append(count).append("\n")
        );
        md.append("\n");

        // Distribuição por Prioridade
        md.append("### Distribuição por Prioridade\n\n");
        Map<ImprovementPriority, Long> byPriority = improvements.stream()
            .collect(Collectors.groupingBy(Improvement::getPriority, Collectors.counting()));

        byPriority.forEach((priority, count) ->
            md.append("- **").append(priority).append(":** ").append(count).append("\n")
        );
        md.append("\n");

        md.append("---\n\n");

        // Melhorias por Prioridade
        md.append("## 🎯 Melhorias Recomendadas\n\n");

        // Alta Prioridade
        appendImprovementsByPriority(md, ImprovementPriority.HIGH, "🔴 Alta Prioridade");

        // Média Prioridade
        appendImprovementsByPriority(md, ImprovementPriority.MEDIUM, "🟡 Média Prioridade");

        // Baixa Prioridade
        appendImprovementsByPriority(md, ImprovementPriority.LOW, "🟢 Baixa Prioridade");

        // Rodapé
        md.append("\n---\n\n");
        md.append("## 📝 Próximos Passos\n\n");
        md.append("1. Revisar e priorizar as melhorias sugeridas\n");
        md.append("2. Criar issues/tasks no sistema de gerenciamento\n");
        md.append("3. Alocar recursos e definir sprints\n");
        md.append("4. Executar melhorias em ordem de prioridade\n");
        md.append("5. Validar resultados e medir impacto\n\n");

        md.append("---\n\n");
        md.append("*Documento gerado automaticamente pelo Odin Agent*\n");

        return md.toString();
    }

    private void appendImprovementsByPriority(StringBuilder md, ImprovementPriority priority, String title) {
        List<Improvement> filtered = improvements.stream()
            .filter(i -> i.getPriority() == priority)
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return;
        }

        md.append("### ").append(title).append("\n\n");

        int index = 1;
        for (Improvement imp : filtered) {
            md.append("#### ").append(index++).append(". ").append(imp.getTitle()).append("\n\n");
            md.append("**Categoria:** ").append(imp.getCategory()).append("\n\n");
            md.append("**Estimativa:** ").append(imp.getEstimation()).append("\n\n");
            md.append("**Descrição:**\n\n");
            md.append(imp.getDescription()).append("\n\n");
            md.append("**Plano de Ação:**\n\n");
            for (String step : imp.getActionSteps()) {
                md.append("- ").append(step).append("\n");
            }
            md.append("\n---\n\n");
        }
    }
}
