#requires -version 3
Import-Module CredentialDeployment

# Install using topshelf
$fsp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.AdjustmentLetters.exe'
&$fsp install -username:$OctopusParameters['topshelf:ServiceUserAccount'] -password:$OctopusParameters['topshelf:ServiceUserPassword'] --autostart | Write-Host 
