#requires -version 3

$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Vif.Service.exe'
&$tfp start | Write-Host