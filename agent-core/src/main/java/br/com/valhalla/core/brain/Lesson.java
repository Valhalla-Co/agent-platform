package br.com.valhalla.core.brain;

import java.util.List;
import java.util.Map;

/**
 * Representa uma lição (lesson) aprendida pelo Odin.
 * Lessons são documentos MD que ensinam padrões, práticas e conhecimento.
 */
public class Lesson {

    private final String id;
    private final String title;
    private final String category;
    private final String content;
    private final List<String> tags;
    private final Map<String, String> metadata;

    public Lesson(String id, String title, String category, String content,
                  List<String> tags, Map<String, String> metadata) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.content = content;
        this.tags = tags != null ? List.copyOf(tags) : List.of();
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public List<String> getTags() {
        return tags;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Verifica se a lesson é relevante para um contexto específico
     */
    public boolean isRelevantFor(String projectType, String language) {
        String applicableTo = metadata.get("applicable_to");
        if (applicableTo == null) {
            return true; // Lesson genérica
        }

        return applicableTo.toLowerCase().contains(projectType.toLowerCase()) ||
               applicableTo.toLowerCase().contains(language.toLowerCase());
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", tags=" + tags +
                '}';
    }
}
