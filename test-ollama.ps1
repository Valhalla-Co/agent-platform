# Test Ollama Integration with Odin Agent

Write-Host "================================" -ForegroundColor Cyan
Write-Host "TESTE DE INTEGRACAO OLLAMA" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar Ollama instalado
Write-Host "1. Verificando Ollama..." -ForegroundColor Yellow
$ollamaPath = "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe"
if (Test-Path $ollamaPath) {
    Write-Host "   OK Ollama instalado" -ForegroundColor Green
} else {
    Write-Host "   ERRO Ollama nao encontrado!" -ForegroundColor Red
    exit 1
}

# 2. Verificar processo rodando
Write-Host ""
Write-Host "2. Verificando processo Ollama..." -ForegroundColor Yellow
$process = Get-Process -Name "ollama" -ErrorAction SilentlyContinue
if ($process) {
    Write-Host "   OK Ollama rodando" -ForegroundColor Green
} else {
    Write-Host "   ERRO Ollama nao esta rodando!" -ForegroundColor Red
    exit 1
}

# 3. Verificar API
Write-Host ""
Write-Host "3. Verificando API (porta 11434)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:11434/api/tags" -Method Get -TimeoutSec 5
    Write-Host "   OK API respondendo" -ForegroundColor Green

    $json = $response.Content | ConvertFrom-Json
    Write-Host ""
    Write-Host "4. Modelos instalados:" -ForegroundColor Yellow
    foreach ($model in $json.models) {
        Write-Host "   - $($model.name)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "   ERRO API nao responde" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "OLLAMA CONFIGURADO COM SUCESSO!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Proximo passo: Testar com Odin Agent" -ForegroundColor Yellow
Write-Host "Execute: .\run.cmd" -ForegroundColor Cyan
Write-Host ""
