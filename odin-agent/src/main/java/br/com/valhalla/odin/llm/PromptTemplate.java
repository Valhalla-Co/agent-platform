package br.com.valhalla.odin.llm;

/**
 * Templates de prompts reutilizáveis para LLM.
 */
public class PromptTemplate {

    /**
     * Template para análise de código.
     */
    public static String codeAnalysis(String projectPath, String context) {
        return String.format(
                "Você é um especialista em arquitetura de software e qualidade de código.\n\n" +
                "Analise o projeto Java localizado em: '%s'\n" +
                "Contexto adicional: %s\n\n" +
                "Forneça 3-5 melhorias específicas e práticas focadas em:\n" +
                "1. Arquitetura e Design Patterns\n" +
                "2. Qualidade e Manutenibilidade do Código\n" +
                "3. Cobertura de Testes\n" +
                "4. Documentação\n" +
                "5. Performance\n\n" +
                "Para cada melhoria, inclua:\n" +
                "- Descrição clara do problema\n" +
                "- Sugestão de solução\n" +
                "- Prioridade (Alta/Média/Baixa)\n" +
                "- Estimativa de esforço em horas",
                projectPath, context
        );
    }

    /**
     * Template para revisão de código.
     */
    public static String codeReview(String code, String filename) {
        return String.format(
                "Revise o seguinte código do arquivo '%s':\n\n" +
                "```java\n%s\n```\n\n" +
                "Analise:\n" +
                "1. Bugs potenciais\n" +
                "2. Problemas de segurança\n" +
                "3. Code smells\n" +
                "4. Melhorias de performance\n" +
                "5. Sugestões de refatoração\n\n" +
                "Seja específico e forneça exemplos de código melhorado quando relevante.",
                filename, code
        );
    }

    /**
     * Template para sugestão de testes.
     */
    public static String testSuggestions(String code, String className) {
        return String.format(
                "Analise a classe '%s' e sugira casos de teste:\n\n" +
                "```java\n%s\n```\n\n" +
                "Forneça:\n" +
                "1. Lista de métodos que precisam de testes\n" +
                "2. Casos de teste para cada método (cenários positivos e negativos)\n" +
                "3. Sugestões de testes de integração\n" +
                "4. Edge cases importantes\n\n" +
                "Use JUnit 5 e Mockito quando necessário.",
                className, code
        );
    }

    /**
     * Template para documentação de código.
     */
    public static String documentation(String code, String type) {
        return String.format(
                "Gere documentação profissional para o seguinte %s:\n\n" +
                "```java\n%s\n```\n\n" +
                "Inclua:\n" +
                "1. Javadoc completo para classes e métodos\n" +
                "2. Descrição do propósito e responsabilidades\n" +
                "3. Exemplos de uso quando relevante\n" +
                "4. Notas sobre edge cases ou comportamentos especiais",
                type, code
        );
    }

    /**
     * Template para sugestão de refatoração.
     */
    public static String refactoringSuggestions(String code, String issue) {
        return String.format(
                "O seguinte código apresenta este problema: %s\n\n" +
                "```java\n%s\n```\n\n" +
                "Sugira refatoração:\n" +
                "1. Identifique o problema específico\n" +
                "2. Explique por que é um problema\n" +
                "3. Forneça código refatorado\n" +
                "4. Explique os benefícios da mudança\n" +
                "5. Liste possíveis trade-offs",
                issue, code
        );
    }

    /**
     * Template para análise de arquitetura.
     */
    public static String architectureAnalysis(String structure) {
        return String.format(
                "Analise a estrutura do projeto:\n\n%s\n\n" +
                "Avalie:\n" +
                "1. Separação de responsabilidades (layers/modules)\n" +
                "2. Acoplamento entre componentes\n" +
                "3. Coesão dos módulos\n" +
                "4. Padrões arquiteturais identificados\n" +
                "5. Sugestões de melhoria arquitetural\n\n" +
                "Considere princípios SOLID e Clean Architecture.",
                structure
        );
    }

    /**
     * Template para perguntas gerais sobre código.
     */
    public static String generalQuestion(String question, String context) {
        if (context != null && !context.isEmpty()) {
            return String.format(
                    "Contexto: %s\n\n" +
                    "Pergunta: %s\n\n" +
                    "Responda de forma clara e objetiva, com exemplos práticos quando relevante.",
                    context, question
            );
        }
        return question;
    }
}
