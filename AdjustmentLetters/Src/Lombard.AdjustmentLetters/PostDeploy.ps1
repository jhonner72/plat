#requires -version 3

$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.AdjustmentLetters.exe'
&$tfp start | Write-Host