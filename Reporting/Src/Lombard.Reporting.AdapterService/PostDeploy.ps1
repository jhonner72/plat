#requires -version 3

$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Reporting.AdapterService.exe'
&$tfp start | Write-Host