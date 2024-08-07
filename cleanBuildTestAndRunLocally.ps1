# PowerShell Script: cleanBuildTestAndRunLocally.ps1

Write-Host "Navigating to the microservice root directory:" $PSScriptRoot
Set-Location -Path $PSScriptRoot

Write-Host "Clear any pre-existing generated and compiled classes."
.\gradlew clean --no-daemon

Write-Host "Generate classes, compile and run all tests."
.\gradlew build --no-daemon

Write-Host "Start the WireMock stub server."
Start-Process -FilePath 'powershell' -ArgumentList '-Command', './gradlew startVaccinationsWireMockServer'

Write-Host "Application local config from application-local.yaml:"
Get-Content -Path "src\main\resources\application-local.yaml"

Write-Host "Start the microservice."
Start-Process -FilePath 'powershell' -ArgumentList '-Command', "./gradlew bootRun --args='--spring.profiles.active=local'"

Read-Host -Prompt "Press Enter to execute the load tests, once the microservice has started successfully."
Start-Process -FilePath 'powershell' -ArgumentList '-Command', './gradlew :load-test:run --no-daemon'

Read-Host -Prompt "Press Enter to execute the API tests, once the previous load tests have completed."
Start-Process -FilePath 'powershell' -ArgumentList '-Command', './gradlew :api-test:run --no-daemon'
