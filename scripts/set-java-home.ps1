<#
Script para definir JAVA_HOME (Machine) e adicionar o bin ao PATH da máquina.
IMPORTANTE: execute este script em um PowerShell aberto como Administrador.
#>
param(
    [string]$JdkPath = 'C:\DevTools\JDK\jdk-21.0.2'
)

Write-Host "Definindo JAVA_HOME para: $JdkPath"
if (-not (Test-Path $JdkPath)) {
    Write-Host "ERRO: caminho informado não existe: $JdkPath" -ForegroundColor Red
    exit 2
}
try {
    [Environment]::SetEnvironmentVariable('JAVA_HOME',$JdkPath,'Machine')
    $machinePath = [Environment]::GetEnvironmentVariable('Path','Machine')
    $jdkBin = Join-Path $JdkPath 'bin'
    if ($machinePath -notlike "*${jdkBin}*") {
        [Environment]::SetEnvironmentVariable('Path',$machinePath + ';' + $jdkBin,'Machine')
    }
    Write-Host "JAVA_HOME definido (Machine). Saia e reabra terminais para aplicar as mudanças." -ForegroundColor Green
    Write-Host "Verifique com: java -version e .\mvnw.cmd -v (em novo terminal)"
} catch {
    Write-Host "ERRO ao setar variáveis: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
