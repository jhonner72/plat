#requires -version 3

$ServiceName = $OctopusParameters['topshelf:ServiceName']

[System.ServiceProcess.ServiceController]$service = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue

If ($service -eq $null) {
	"Service was not found, skipping pre deploy script" | Write-Host
	Exit 0
}

$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Vif.Service.exe'

"Service is stopping..." | Write-Host
&$tfp stop
"Service was stopped" | Write-Host

"Service is uninstalling..." | Write-Host
&$tfp uninstall
"Service is uninstalled" | Write-Host