package br.com.valhalla.odin.core;
import br.com.valhalla.odin.discovery.ProjectContext;
import br.com.valhalla.odin.patterns.LearnedPatterns;
import br.com.valhalla.odin.specs.*;
import br.com.valhalla.agentbase.discovery.ProjectContext;

import java.util.*;

/**
 * Gerador de Specs de Melhoria.
 */
public class SpecGenerator {

    public ImprovementSpec generate(ProjectContext context, LearnedPatterns patterns) {
        ImprovementSpec spec = new ImprovementSpec(context.getProjectName());

        // Analisar e gerar melhorias baseadas nos padrões
        generateArchitecturalImprovements(context, patterns, spec);
        generateDesignImprovements(context, patterns, spec);
        generateCodeQualityImprovements(context, patterns, spec);
        generateDocumentationImprovements(context, spec);
        generateTestingImprovements(context, spec);

        return spec;
    }

    private void generateArchitecturalImprovements(ProjectContext context,
                                                   LearnedPatterns patterns,
                                                   ImprovementSpec spec) {
        // Verificar se falta documentação de arquitetura
        if (!hasArchitectureDoc(context)) {
            spec.addImprovement(new Improvement(
                "Documentação de Arquitetura",
                "Criar documento detalhando a arquitetura do projeto, incluindo diagrams e decisões arquiteturais",
                ImprovementPriority.HIGH,
                ImprovementCategory.ARCHITECTURE,
                "8h",
                Arrays.asList(
                    "Criar arquivo docs/architecture.md",
                    "Documentar padrões identificados: " + patterns.getArchitecturalPatterns().keySet(),
                    "Adicionar diagramas C4 (Context, Container, Component)",
                    "Documentar decisões arquiteturais (ADRs)"
                )
            ));
        }

        // Sugerir melhorias na modularização
        if (context.getProjectType().equals("Maven") && context.getSourceFiles().size() > 100) {
            spec.addImprovement(new Improvement(
                "Revisão de Modularização",
                "Avaliar oportunidades de modularização adicional para melhorar manutenibilidade",
                ImprovementPriority.MEDIUM,
                ImprovementCategory.ARCHITECTURE,
                "16h",
                Arrays.asList(
                    "Analisar acoplamento entre módulos",
                    "Identificar responsabilidades que podem ser extraídas",
                    "Avaliar criação de novos módulos específicos",
                    "Revisar dependências entre módulos"
                )
            ));
        }
    }

    private void generateDesignImprovements(ProjectContext context,
                                           LearnedPatterns patterns,
                                           ImprovementSpec spec) {
        // Sugerir implementação de padrões faltantes
        if (!patterns.getDesignPatterns().containsKey("Builder Pattern")) {
            spec.addImprovement(new Improvement(
                "Implementar Builder Pattern",
                "Adicionar builders para objetos complexos, melhorando legibilidade",
                ImprovementPriority.LOW,
                ImprovementCategory.DESIGN,
                "4h",
                Arrays.asList(
                    "Identificar classes com muitos parâmetros no construtor",
                    "Implementar builders fluentes",
                    "Adicionar validações nos builders",
                    "Documentar uso dos builders"
                )
            ));
        }

        // Validação e tratamento de erros
        spec.addImprovement(new Improvement(
            "Estratégia de Tratamento de Erros",
            "Implementar estratégia consistente de tratamento e logging de erros",
            ImprovementPriority.HIGH,
            ImprovementCategory.DESIGN,
            "12h",
            Arrays.asList(
                "Definir hierarquia de exceções customizadas",
                "Implementar handlers globais de exceção",
                "Adicionar logging estruturado",
                "Criar documentação de códigos de erro"
            )
        ));
    }

    private void generateCodeQualityImprovements(ProjectContext context,
                                                 LearnedPatterns patterns,
                                                 ImprovementSpec spec) {
        spec.addImprovement(new Improvement(
            "Análise Estática de Código",
            "Configurar ferramentas de análise estática (SonarQube, Checkstyle, SpotBugs)",
            ImprovementPriority.HIGH,
            ImprovementCategory.CODE_QUALITY,
            "6h",
            Arrays.asList(
                "Configurar SonarQube ou alternativa",
                "Adicionar Checkstyle com regras customizadas",
                "Configurar SpotBugs",
                "Integrar análise no CI/CD",
                "Definir quality gates"
            )
        ));

        spec.addImprovement(new Improvement(
            "Refatoração de Code Smells",
            "Identificar e refatorar code smells existentes",
            ImprovementPriority.MEDIUM,
            ImprovementCategory.CODE_QUALITY,
            "20h",
            Arrays.asList(
                "Executar análise com ferramentas",
                "Priorizar code smells críticos",
                "Refatorar métodos longos",
                "Reduzir complexidade ciclomática",
                "Eliminar código duplicado"
            )
        ));
    }

    private void generateDocumentationImprovements(ProjectContext context,
                                                   ImprovementSpec spec) {
        spec.addImprovement(new Improvement(
            "Documentação de API",
            "Melhorar documentação de APIs públicas e contratos",
            ImprovementPriority.MEDIUM,
            ImprovementCategory.DOCUMENTATION,
            "8h",
            Arrays.asList(
                "Adicionar JavaDoc completo em classes públicas",
                "Documentar contratos de interfaces",
                "Criar exemplos de uso",
                "Gerar documentação com JavaDoc/Dokka"
            )
        ));

        spec.addImprovement(new Improvement(
            "README e Guias",
            "Expandir README com guias de contribuição e desenvolvimento",
            ImprovementPriority.MEDIUM,
            ImprovementCategory.DOCUMENTATION,
            "4h",
            Arrays.asList(
                "Atualizar README com informações completas",
                "Criar CONTRIBUTING.md",
                "Adicionar guia de setup detalhado",
                "Documentar convenções do projeto"
            )
        ));
    }

    private void generateTestingImprovements(ProjectContext context,
                                            ImprovementSpec spec) {
        spec.addImprovement(new Improvement(
            "Cobertura de Testes",
            "Aumentar cobertura de testes unitários e de integração",
            ImprovementPriority.HIGH,
            ImprovementCategory.TESTING,
            "40h",
            Arrays.asList(
                "Configurar JaCoCo para medir cobertura",
                "Meta: atingir 80% de cobertura",
                "Adicionar testes unitários faltantes",
                "Implementar testes de integração",
                "Adicionar testes de contrato"
            )
        ));

        spec.addImprovement(new Improvement(
            "Testes de Performance",
            "Implementar testes de performance e carga",
            ImprovementPriority.LOW,
            ImprovementCategory.TESTING,
            "16h",
            Arrays.asList(
                "Configurar JMH para benchmarks",
                "Criar testes de carga com Gatling/JMeter",
                "Definir SLAs e métricas",
                "Implementar monitoramento de performance"
            )
        ));
    }

    private boolean hasArchitectureDoc(ProjectContext context) {
        return context.getSourceFiles().stream()
            .anyMatch(f -> f.toString().contains("architecture") &&
                          (f.toString().endsWith(".md") || f.toString().endsWith(".adoc")));
    }
}
