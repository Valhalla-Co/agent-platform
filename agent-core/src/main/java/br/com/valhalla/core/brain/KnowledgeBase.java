package br.com.valhalla.core.brain;

import java.nio.file.Path;
import java.util.List;

/**
 * Interface para base de conhecimento (brain) dos agentes.
 */
public interface KnowledgeBase {

    /**
     * Inicializa a base de conhecimento carregando lessons.
     */
    void initialize();

    /**
     * Busca lessons por tag.
     */
    List<Lesson> findByTag(String tag);

    /**
     * Busca lessons por categoria.
     */
    List<Lesson> findByCategory(String category);

    /**
     * Retorna todas as lessons.
     */
    List<Lesson> getAllLessons();

    /**
     * Retorna o número de lessons carregadas.
     */
    int size();

    /**
     * Retorna o path da brain.
     */
    Path getBrainPath();
}
