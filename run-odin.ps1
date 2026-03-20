# run-odin.ps1 - Build reactor, build classpath and run OdinApp using compiled classes (no need to install to local repo)
# Usage: Powershell ./run-odin.ps1

param(
    [switch]$ForceFake
)

$ErrorActionPreference = 'Stop'

Write-Host "STARTING RUN"
Write-Host "Using JAVA_HOME: $env:JAVA_HOME"
Write-Host "Force fake provider: $ForceFake"

# 1) Build all modules
Write-Host "Building project (skip tests) ..."
if (-not $env:JAVA_HOME) {
    if (Test-Path 'C:\DevTools\JDK\jdk-21.0.2') {
        $env:JAVA_HOME = 'C:\DevTools\JDK\jdk-21.0.2'
    } else {
        Write-Warning "JAVA_HOME not set and default path C:\DevTools\JDK\jdk-21.0.2 not found. Relying on PATH."
    }
}
.\mvnw.cmd -DskipTests package

# 2) Build runtime classpath for odin-orchestrator
Write-Output "Building dependency classpath for :odin-orchestrator ..."
$cpFileRelative = "odin-orchestrator\odin-cp.txt"
if (Test-Path $cpFileRelative) { Remove-Item $cpFileRelative }

# We must use quotes for -D arguments in PowerShell to pass them as single string to batch file
# Output file determined by Maven relative to module directory (odin-orchestrator)
cmd /c mvnw.cmd -pl :odin-orchestrator dependency:build-classpath "-DincludeScope=runtime" "-Dmdep.outputFile=odin-cp.txt"

if (-Not (Test-Path $cpFileRelative)) {
    Write-Error "ERROR: Output file not found at $cpFileRelative"
    exit 1
}

$deps = Get-Content $cpFileRelative -Raw

# 3) Add target/classes of modules to the classpath
$classes = "agent-core\target\classes;dev-agent\target\classes;architect-agent\target\classes;llm-providers\ollama-provider\target\classes;odin-orchestrator\target\classes"

# Windows classpath separator is ;
$full = $deps + ";" + $classes

# 4) Set environment variable to force fake provider if requested
if ($ForceFake) {
    $env:VALHALLA_FORCE_FAKE = 'true'
}

Write-Host "Executing OdinApp with classpath length: " ($full.Length)

# 5) Run the application
$javaExe = "java"
if ($env:JAVA_HOME) {
    $javaExe = "$env:JAVA_HOME\bin\java.exe"
}

Write-Host "Java Executable: $javaExe"
& $javaExe -cp $full br.com.valhalla.orchestrator.OdinApp

Write-Host "Done."
