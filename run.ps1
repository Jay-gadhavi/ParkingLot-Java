# PowerShell Script to Compile and Run the Parking Lot Java System

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "Compiling Java source files..." -ForegroundColor Cyan
$sources = (Get-ChildItem -Recurse -Filter *.java src).FullName
javac -d bin $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!" -ForegroundColor Green
    Write-Host "`nStarting Parking Lot Application..." -ForegroundColor Yellow
    java -cp bin com.parkinglot.Main
} else {
    Write-Host "Compilation failed. Please check errors above." -ForegroundColor Red
}
