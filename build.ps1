# Build script for YunTech Campus Activity Registration Platform

$JavaFXPath = 'C:\Users\clair\Desktop\Campus_System.worktrees\agents-yuntech-activity-registration-platform\javafx-sdk-24.0.1\lib'
$SrcPath = 'C:\Users\clair\Desktop\Campus_System.worktrees\agents-yuntech-activity-registration-platform\java_frontFX\JAVAFX\src'
$BinPath = 'C:\Users\clair\Desktop\Campus_System.worktrees\agents-yuntech-activity-registration-platform\java_frontFX\JAVAFX\bin'
$LibPath = 'C:\Users\clair\Desktop\Campus_System.worktrees\agents-yuntech-activity-registration-platform\java_frontFX\lib'

# Create bin directory if it doesn't exist
if (-not (Test-Path $BinPath)) {
    New-Item -ItemType Directory -Path $BinPath -Force | Out-Null
}

# Compile all Java files
Write-Host "Compiling Java files..." -ForegroundColor Cyan

# Build classpath
$classPathList = @($BinPath, $LibPath)
$jarFiles = Get-ChildItem -Path $JavaFXPath -Filter "*.jar" -ErrorAction SilentlyContinue
foreach ($jar in $jarFiles) {
    $classPathList += $jar.FullName
}
$classPath = $classPathList -join ";"

Write-Host "Classpath includes $(($jarFiles | Measure-Object).Count) JAR files" -ForegroundColor Green

# Get all Java files
$javaFiles = Get-ChildItem -Path $SrcPath -Filter "*.java" -Recurse
Write-Host "Found $($javaFiles.Count) Java files" -ForegroundColor Green

if ($javaFiles.Count -eq 0) {
    Write-Host "No Java files found!" -ForegroundColor Red
    exit 1
}

# Create a response file with all source files
$responseFile = "$BinPath\build.rsp"
$javaFiles | ForEach-Object { $_.FullName } | Out-File -Encoding UTF8 -FilePath $responseFile

# Compile using response file
Write-Host "Running: javac ..." -ForegroundColor Cyan
& javac -d $BinPath -cp $classPath "@$responseFile"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!" -ForegroundColor Green
} else {
    Write-Host "Compilation failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit 1
}

# Copy resources
Write-Host "Copying resources..." -ForegroundColor Cyan
Copy-Item -Path "$SrcPath/*.css" -Destination $BinPath -Force 2>$null
Copy-Item -Path "$SrcPath/images/*" -Destination "$BinPath/images" -Force -Recurse -ErrorAction SilentlyContinue
Copy-Item -Path "$SrcPath/../data/*" -Destination "$BinPath/data" -Force -Recurse -ErrorAction SilentlyContinue

Write-Host "Build complete!" -ForegroundColor Green
Write-Host "To run the application:" -ForegroundColor Yellow
Write-Host "java --module-path $JavaFXPath --add-modules javafx.controls,javafx.fxml -cp `"$BinPath;$classPath`" Launcher" -ForegroundColor Yellow
