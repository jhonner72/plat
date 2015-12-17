#requires -version 3

$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.ECLMatchingEngine.Service.exe'
&$tfp start | Write-Host