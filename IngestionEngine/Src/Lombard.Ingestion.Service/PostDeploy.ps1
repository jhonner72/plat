#requires -version 3

$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Ingestion.Service.exe'
&$tfp start | Write-Host