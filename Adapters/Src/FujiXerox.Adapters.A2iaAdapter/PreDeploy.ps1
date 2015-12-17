#requires -version 3
Import-Module WindowsServiceDeployment

$serviceName = $OctopusParameters['topshelf:ServiceName']

[System.ServiceProcess.ServiceController]$service = Get-Service -Name $serviceName -ErrorAction SilentlyContinue
if($service -ne $null -and $service.Status -eq [System.ServiceProcess.ServiceControllerStatus]::Running) {
	Stop-WinSvc -ServiceName $serviceName
}
