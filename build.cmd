@echo off
echo ================================================================
echo Building Agent Platform
echo ================================================================
echo.
echo Setting JAVA_HOME to C:\DevTools\JDK\jdk-21.0.2
set JAVA_HOME=C:\DevTools\JDK\jdk-21.0.2
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Verifying Java installation...
"%JAVA_HOME%\bin\java.exe" -version
echo.

if errorlevel 1 (
    echo ERROR: Java not found at %JAVA_HOME%
    echo Please verify the JDK is installed at the specified location.
    pause
    exit /b 1
)

echo.
echo Java found! Starting Maven build...
echo.
echo ================================================================
echo Running: mvnw.cmd clean install
echo ================================================================
echo.

call mvnw.cmd clean install

if errorlevel 1 (
    echo.
    echo ================================================================
    echo BUILD FAILED!
    echo ================================================================
    pause
    exit /b 1
) else (
    echo.
    echo ================================================================
    echo BUILD SUCCESS!
    echo ================================================================
    echo.
    echo You can now run the application:
    echo java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar --help
    echo.
    pause
)
