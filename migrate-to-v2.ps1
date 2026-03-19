# Script de Migração Automática - Agent Platform v2.0
# Cria estrutura agent-core completa

Write-Host "================================" -ForegroundColor Cyan
Write-Host "AGENT PLATFORM V2.0 - MIGRATION" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

$ErrorActionPreference = "Stop"
$baseDir = "C:\Users\xleos\Documents\projects\agent-platform"
Set-Location $baseDir

# 1. BACKUP
Write-Host "1. Criando backup..." -ForegroundColor Yellow
$backupDir = "backup-$(Get-Date -Format 'yyyyMMdd-HHmmss')"
if (-not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir | Out-Null
    Copy-Item "odin-agent" -Destination "$backupDir\odin-agent" -Recurse -Force
    Write-Host "   Backup criado: $backupDir" -ForegroundColor Green
}

# 2. COPIAR LLM PARA AGENT-CORE
Write-Host ""
Write-Host "2. Copiando LLM para agent-core..." -ForegroundColor Yellow

$llmSource = "odin-agent\src\main\java\br\com\valhalla\odin\llm"
$llmDest = "agent-core\src\main\java\br\com\valhalla\core\llm"

if (-not (Test-Path $llmDest)) {
    New-Item -ItemType Directory -Path $llmDest -Force | Out-Null
}

$llmFiles = @(
    "LLMProvider.java",
    "LLMResponse.java",
    "LLMConfig.java",
    "PromptTemplate.java"
)

foreach ($file in $llmFiles) {
    $sourcePath = Join-Path $llmSource $file
    $destPath = Join-Path $llmDest $file
    
    if (Test-Path $sourcePath) {
        # Ler conteúdo
        $content = Get-Content $sourcePath -Raw
        
        # Atualizar package
        $content = $content -replace 'package br\.com\.valhalla\.odin\.llm;', 'package br.com.valhalla.core.llm;'
        
        # Salvar
        Set-Content -Path $destPath -Value $content -Encoding UTF8
        Write-Host "   OK $file" -ForegroundColor Green
    }
}

# 3. COPIAR BRAIN PARA AGENT-CORE
Write-Host ""
Write-Host "3. Copiando Brain para agent-core..." -ForegroundColor Yellow

$brainSource = "odin-agent\src\main\java\br\com\valhalla\odin\brain"
$brainDest = "agent-core\src\main\java\br\com\valhalla\core\brain"

if (-not (Test-Path $brainDest)) {
    New-Item -ItemType Directory -Path $brainDest -Force | Out-Null
}

$brainFiles = @(
    "Lesson.java",
    "LessonParser.java",
    "LessonRepository.java"
)

foreach ($file in $brainFiles) {
    $sourcePath = Join-Path $brainSource $file
    $destPath = Join-Path $brainDest $file
    
    if (Test-Path $sourcePath) {
        # Ler conteúdo
        $content = Get-Content $sourcePath -Raw
        
        # Atualizar package
        $content = $content -replace 'package br\.com\.valhalla\.odin\.brain;', 'package br.com.valhalla.core.brain;'
        
        # Atualizar imports de odin para core
        $content = $content -replace 'import br\.com\.valhalla\.odin\.', 'import br.com.valhalla.core.'
        
        # Salvar
        Set-Content -Path $destPath -Value $content -Encoding UTF8
        Write-Host "   OK $file" -ForegroundColor Green
    }
}

# 4. CRIAR KNOWLEDGEBASE INTERFACE
Write-Host ""
Write-Host "4. Criando KnowledgeBase interface..." -ForegroundColor Yellow

$knowledgeBaseContent = @"
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
"@

$kbPath = "agent-core\src\main\java\br\com\valhalla\core\brain\KnowledgeBase.java"
Set-Content -Path $kbPath -Value $knowledgeBaseContent -Encoding UTF8
Write-Host "   OK KnowledgeBase.java" -ForegroundColor Green

# 5. COPIAR GIT PARA REPOSITORY
Write-Host ""
Write-Host "5. Copiando Git para agent-core/repository..." -ForegroundColor Yellow

$gitSource = "odin-agent\src\main\java\br\com\valhalla\odin\git"
$repoSource = "agent-core\src\main\java\br\com\valhalla\core\repository"

if (-not (Test-Path $repoSource)) {
    New-Item -ItemType Directory -Path $repoSource -Force | Out-Null
}

$gitFiles = @(
    "GitConfig.java",
    "RepositoryManager.java"
)

foreach ($file in $gitFiles) {
    $sourcePath = Join-Path $gitSource $file
    $destPath = Join-Path $repoSource $file
    
    if (Test-Path $sourcePath) {
        # Ler conteúdo
        $content = Get-Content $sourcePath -Raw
        
        # Atualizar package
        $content = $content -replace 'package br\.com\.valhalla\.odin\.git;', 'package br.com.valhalla.core.repository;'
        
        # Atualizar imports
        $content = $content -replace 'import br\.com\.valhalla\.odin\.', 'import br.com.valhalla.core.'
        
        # Salvar
        Set-Content -Path $destPath -Value $content -Encoding UTF8
        Write-Host "   OK $file" -ForegroundColor Green
    }
}

# 6. CRIAR WORKSPACEMANAGER
Write-Host ""
Write-Host "6. Criando WorkspaceManager..." -ForegroundColor Yellow

$workspaceManagerContent = @"
package br.com.valhalla.core.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia workspace de projetos clonados.
 * Projetos externos são clonados para workspace/branches/
 */
public class WorkspaceManager {
    
    private final Path workspaceRoot;
    private final RepositoryManager repoManager;
    
    public WorkspaceManager() {
        this(Paths.get("workspace/branches"));
    }
    
    public WorkspaceManager(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
        this.repoManager = new RepositoryManager();
        ensureWorkspaceExists();
    }
    
    private void ensureWorkspaceExists() {
        try {
            if (!Files.exists(workspaceRoot)) {
                Files.createDirectories(workspaceRoot);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create workspace directory", e);
        }
    }
    
    /**
     * Clona um projeto para o workspace.
     */
    public Path cloneProject(String gitUrl) {
        String projectName = extractProjectName(gitUrl);
        Path projectPath = workspaceRoot.resolve(projectName);
        
        if (Files.exists(projectPath)) {
            System.out.println("Project already exists: " + projectPath);
            return projectPath;
        }
        
        return repoManager.cloneRepository(gitUrl, workspaceRoot.toString());
    }
    
    /**
     * Lista todos os projetos no workspace.
     */
    public List<Path> listProjects() {
        try {
            return Files.list(workspaceRoot)
                    .filter(Files::isDirectory)
                    .filter(p -> Files.exists(p.resolve(".git")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Retorna o path de um projeto pelo nome.
     */
    public Path getProject(String projectName) {
        Path projectPath = workspaceRoot.resolve(projectName);
        return Files.exists(projectPath) ? projectPath : null;
    }
    
    /**
     * Remove um projeto do workspace.
     */
    public boolean removeProject(String projectName) {
        Path projectPath = workspaceRoot.resolve(projectName);
        if (Files.exists(projectPath)) {
            return deleteDirectory(projectPath);
        }
        return false;
    }
    
    private String extractProjectName(String gitUrl) {
        String name = gitUrl.substring(gitUrl.lastIndexOf('/') + 1);
        if (name.endsWith(".git")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }
    
    private boolean deleteDirectory(Path path) {
        try {
            Files.walk(path)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            // ignore
                        }
                    });
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }
}
"@

$wmPath = "agent-core\src\main\java\br\com\valhalla\core\repository\WorkspaceManager.java"
Set-Content -Path $wmPath -Value $workspaceManagerContent -Encoding UTF8
Write-Host "   OK WorkspaceManager.java" -ForegroundColor Green

# 7. CRIAR WORKSPACE/BRANCHES
Write-Host ""
Write-Host "7. Criando estrutura workspace..." -ForegroundColor Yellow

$workspaceDir = "workspace\branches"
if (-not (Test-Path $workspaceDir)) {
    New-Item -ItemType Directory -Path $workspaceDir -Force | Out-Null
    Write-Host "   OK workspace/branches/" -ForegroundColor Green
}

# 8. CRIAR .GITIGNORE PARA WORKSPACE
$gitignoreContent = @"
# Workspace - projetos clonados
*
!.gitignore
"@

$gitignorePath = "workspace\branches\.gitignore"
Set-Content -Path $gitignorePath -Value $gitignoreContent -Encoding UTF8
Write-Host "   OK workspace/branches/.gitignore" -ForegroundColor Green

# 9. BACKUP POM ATUAL E SUBSTITUIR
Write-Host ""
Write-Host "8. Atualizando POM principal..." -ForegroundColor Yellow

if (Test-Path "pom.xml") {
    Copy-Item "pom.xml" -Destination "pom-old.xml.bak" -Force
    Write-Host "   Backup: pom-old.xml.bak" -ForegroundColor Green
}

if (Test-Path "pom-new.xml") {
    Copy-Item "pom-new.xml" -Destination "pom.xml" -Force
    Write-Host "   OK pom.xml atualizado" -ForegroundColor Green
}

# 10. RESUMO
Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "MIGRACAO CONCLUIDA!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Arquivos copiados:" -ForegroundColor Yellow
Write-Host "  - 4 classes LLM" -ForegroundColor White
Write-Host "  - 3 classes Brain" -ForegroundColor White
Write-Host "  - 2 classes Repository" -ForegroundColor White
Write-Host "  - 2 classes criadas (KnowledgeBase, WorkspaceManager)" -ForegroundColor White
Write-Host ""

Write-Host "Estrutura criada:" -ForegroundColor Yellow
Write-Host "  - agent-core/llm/" -ForegroundColor White
Write-Host "  - agent-core/brain/" -ForegroundColor White
Write-Host "  - agent-core/repository/" -ForegroundColor White
Write-Host "  - workspace/branches/" -ForegroundColor White
Write-Host ""

Write-Host "Backup:" -ForegroundColor Yellow
Write-Host "  - $backupDir/" -ForegroundColor White
Write-Host "  - pom-old.xml.bak" -ForegroundColor White
Write-Host ""

Write-Host "Proximo passo:" -ForegroundColor Yellow
Write-Host "  mvnw clean compile" -ForegroundColor Cyan
Write-Host ""
