@echo off
REM ================================================================
REM Odin Agent - Agente Especializado em Análise de Código
REM ================================================================

set JAVA_HOME=C:\DevTools\JDK\jdk-21.0.2
set PATH=%JAVA_HOME%\bin;%PATH%

REM Verifica se o JAR existe
if not exist "odin-agent\target\odin-agent-1.0.0-SNAPSHOT.jar" (
    echo.
    echo [ERRO] Odin Agent nao encontrado!
    echo Execute primeiro: .\build.cmd
    echo.
    pause
    exit /b 1
)

REM Executa o Odin Agent com os argumentos passados
java -jar odin-agent\target\odin-agent-1.0.0-SNAPSHOT.jar %*
