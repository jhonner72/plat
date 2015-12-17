#requires -version 3
Import-Module CredentialDeployment

# Install using topshelf
$fsp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.ECLMatchingEngine.Service.exe'
&$fsp install -username:$OctopusParameters['topshelf:ServiceUserAccount'] -password:$OctopusParameters['topshelf:ServiceUserPassword'] --autostart | Write-Host 


$fsp = "C:\tfs\Plat\ECL.Matching.Engine\src\ECL.Matching.Engine\bin\Debug\Lombard.ECLMatchingEngine.Service.exe"
