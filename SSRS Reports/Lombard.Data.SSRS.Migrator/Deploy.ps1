$sqlScript = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Scripts\InitCreate.sql'

Write-Host 'Replacing variable on SQL Script: $sqlScript'

$octopusVariables = @{
    linkedServer = $OctopusParameters['LinkedServer'] #[VSQLS-NABDC\SQL1]
    documentumDb = $OctopusParameters['DocumentumDb'] #[VDBS_NABDC]
}

# Replace sql script variables
$scriptVariables = @{
    linkedServer = 'VSQLS-NABDC\\SQL1'
    documentumDb = 'VDBS_NABDC'
}

(Get-Content $sqlScript) | 
ForEach-Object { $_ -replace $scriptVariables.linkedServer, $octopusVariables.linkedServer } | 
ForEach-Object { $_ -replace $scriptVariables.documentumDb, $octopusVariables.documentumDb } | 
Set-Content ($sqlScript)