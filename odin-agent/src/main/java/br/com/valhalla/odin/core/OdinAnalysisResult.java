package br.com.valhalla.odin.core;

import br.com.valhalla.odin.discovery.ProjectContext;
import br.com.valhalla.odin.patterns.LearnedPatterns;
import br.com.valhalla.odin.specs.ImprovementSpec;

import java.nio.file.Path;

/**
 * Resultado da análise do Odin Agent.
 */
public class OdinAnalysisResult {

    private final ProjectContext context;
    private final LearnedPatterns patterns;
    private final ImprovementSpec spec;
    private final Path specPath;

    public OdinAnalysisResult(ProjectContext context, LearnedPatterns patterns,
                              ImprovementSpec spec, Path specPath) {
        this.context = context;
        this.patterns = patterns;
        this.spec = spec;
        this.specPath = specPath;
    }

    public ProjectContext getContext() {
        return context;
    }

    public LearnedPatterns getPatterns() {
        return patterns;
    }

    public ImprovementSpec getSpec() {
        return spec;
    }

    public Path getSpecPath() {
        return specPath;
    }
}
