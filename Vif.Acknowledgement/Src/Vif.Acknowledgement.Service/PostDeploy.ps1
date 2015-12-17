#requires -version 3

$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Vif.Acknowledgement.Service.exe'
&$tfp start | Write-Host