Import-Module WindowsServiceDeployment

Stop-WinSvc -ServiceName $ServiceName -Timeout '00:06:00'
