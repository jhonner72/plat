#requires -version 3

$ServiceName = $OctopusParameters['topshelf:ServiceName']

[System.ServiceProcess.ServiceController]$service = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue

If ($service -eq $null) {
	"Service was not found, skipping pre deploy script" | Write-Host
	Exit 0
}

If ($service.Status -eq [System.ServiceProcess.ServiceControllerStatus]::Running) {
	"$($MyInvocation.MyCommand): Stopping service" | Write-Host
    $service.Stop()
	$timeout = '00:00:30'
	$service.WaitForStatus('Stopped',$timeout)
}

Try {
	SC.EXE delete $ServiceName -ErrorAction SilentlyContinue | Write-Host
	Start-Sleep -Second 5
}
Catch {
	Write-Error "Error removing service"
	Exit 1
}