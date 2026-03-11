package br.com.valhalla.agentbase.odin;

import br.com.valhalla.agentbase.discovery.ProjectContext;

import java.util.*;

/**
 * Pattern Learner - Aprende padrões de código, arquitetura e design.
 */
public class PatternLearner {

    public LearnedPatterns learn(ProjectContext context) {
        LearnedPatterns patterns = new LearnedPatterns();

        // Aprender padrões arquiteturais
        learnArchitecturalPatterns(context, patterns);

        // Aprender padrões de design
        learnDesignPatterns(context, patterns);

        // Aprender padrões de código
        learnCodePatterns(context, patterns);

        return patterns;
    }

    private void learnArchitecturalPatterns(ProjectContext context, LearnedPatterns patterns) {
        // Detectar estrutura de pacotes
        if (context.getProjectType().equals("Maven")) {
            patterns.addArchitecturalPattern("Maven Multi-Module",
                "Projeto Maven com estrutura modular");
        }

        // Detectar camadas
        boolean hasDomain = context.getSourceFiles().stream()
            .anyMatch(f -> f.toString().contains("/domain/"));
        boolean hasPorts = context.getSourceFiles().stream()
            .anyMatch(f -> f.toString().contains("/ports/"));
        boolean hasDto = context.getSourceFiles().stream()
            .anyMatch(f -> f.toString().contains("/dto/"));

        if (hasDomain && hasPorts) {
            patterns.addArchitecturalPattern("Hexagonal Architecture (Ports & Adapters)",
                "Arquitetura hexagonal com separação clara de domínio e portas");
        }

        if (hasDto) {
            patterns.addArchitecturalPattern("DTO Pattern",
                "Uso de Data Transfer Objects para transferência de dados");
        }
    }

    private void learnDesignPatterns(ProjectContext context, LearnedPatterns patterns) {
        // Detectar padrões de design comuns
        context.getSourceFiles().forEach(filePath -> {
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

            if (fileName.endsWith("Gateway.java")) {
                patterns.addDesignPattern("Gateway Pattern",
                    "Uso de gateways para abstração de acesso externo");
            }

            if (fileName.endsWith("Repository.java")) {
                patterns.addDesignPattern("Repository Pattern",
                    "Padrão Repository para acesso a dados");
            }

            if (fileName.endsWith("UseCase.java")) {
                patterns.addDesignPattern("Use Case Pattern",
                    "Casos de uso como unidades de lógica de negócio");
            }

            if (fileName.endsWith("Factory.java")) {
                patterns.addDesignPattern("Factory Pattern",
                    "Padrão Factory para criação de objetos");
            }
        });
    }

    private void learnCodePatterns(ProjectContext context, LearnedPatterns patterns) {
        // Análise de convenções de código
        patterns.addCodePattern("Package Structure",
            "Estrutura de pacotes organizada por funcionalidade");

        patterns.addCodePattern("Naming Conventions",
            "Convenções de nomenclatura consistentes");

        // Detectar framework/lib usage
        if (context.getProjectType().equals("Maven")) {
            patterns.addCodePattern("Maven Dependency Management",
                "Gerenciamento de dependências via Maven");
        }
    }
}
