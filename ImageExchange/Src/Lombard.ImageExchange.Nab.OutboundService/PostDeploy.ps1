#requires -version 3

$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.ImageExchange.Nab.OutboundService.exe'
&$tfp start | Write-Host