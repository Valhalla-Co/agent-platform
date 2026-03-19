param(
    [string]$repoPath = '..',
    [string]$branchName = $(Get-Date -Format 'yyyyMMdd-HHmmss'),
    [string]$payloadFile,
    [string]$requestId,
    [string]$intent,
    [int]$timeoutSeconds,
    [string]$logDir,
    [switch]$nonInteractive
)

function Write-Result {
    param($resultPath, $resultObj)
    $json = $resultObj | ConvertTo-Json -Depth 6
    $json | Out-File -FilePath $resultPath -Encoding UTF8 -Force
    Write-Host $json
}

# Load payload (file or stdin)
try {
    $payloadJson = $null
    if ($payloadFile) {
        $payloadJson = Get-Content -LiteralPath $payloadFile -Raw -ErrorAction Stop
    } elseif ($MyInvocation.ExpectingInput) {
        $payloadJson = [System.IO.StreamReader]::new([Console]::OpenStandardInput()).ReadToEnd()
    }
    if ($payloadJson) {
        $payload = $payloadJson | ConvertFrom-Json -ErrorAction Stop
    }
} catch {
    Write-Host "Erro ao ler payload: $($_.Exception.Message)" -ForegroundColor Red
    exit 2
}

# Derive values
if (-not $requestId) { $requestId = $payload.requestId }
if (-not $requestId) { $requestId = "req-$([guid]::NewGuid().ToString())" }
if (-not $repoPath -or $repoPath -eq '') { $repoPath = $payload.repoPath }
if (-not $repoPath -or $repoPath -eq '') { $repoPath = '..' }
if (-not $intent) { $intent = $payload.intent }
if (-not $timeoutSeconds) { $timeoutSeconds = $payload.timeoutSeconds }

if (-not $logDir -or $logDir -eq '') {
    $logDir = Join-Path -Path $repoPath -ChildPath "agent-workspace/$requestId/logs"
}
$workspaceDir = Split-Path -Parent $logDir
if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir -Force | Out-Null }

$resultPath = Join-Path $workspaceDir 'result.json'
$buildLog = Join-Path $logDir 'build.log'

Write-Host "Architect Agent CLI wrapper: requestId=$requestId intent=$intent repoPath=$repoPath"

# Prepare branch workspace (optional placeholder)
$dest = Join-Path -Path $repoPath -ChildPath "branches/architect-agent/$branchName"
if (-not (Test-Path $dest)) { New-Item -ItemType Directory -Path $dest -Force | Out-Null }

$exitCode = 0
$status = 'done'
$summary = 'Build executado com sucesso.'
$artifacts = @()

Push-Location $repoPath
try {
    if (Test-Path '.\mvnw.cmd') {
        Write-Host "Running mvnw -pl :architect-agent -DskipTests clean package (log: $buildLog)"
        .\mvnw.cmd -pl :architect-agent -DskipTests clean package *> $buildLog
        $exitCode = $LASTEXITCODE
        if ($exitCode -ne 0) {
            $status = 'failed'
            $summary = "Build falhou (exit $exitCode). Veja $buildLog"
        } else {
            # Collect artifact if exists
            $jar = Get-ChildItem -Path '.\architect-agent\target' -Filter '*.jar' -ErrorAction SilentlyContinue | Select-Object -First 1
            if ($jar) { $artifacts += $jar.FullName }
        }
    } else {
        $status = 'failed'
        $summary = 'mvnw.cmd não encontrado no repo root.'
        $exitCode = 1
    }
} catch {
    $status = 'failed'
    $exitCode = 1
    $summary = "Erro ao executar build: $($_.Exception.Message)"
} finally {
    Pop-Location
}

$result = [ordered]@{
    requestId = $requestId
    status = $status
    exitCode = $exitCode
    artifacts = $artifacts
    logs = $buildLog
    summary = $summary
}

Write-Result -resultPath $resultPath -resultObj $result
exit $exitCode
