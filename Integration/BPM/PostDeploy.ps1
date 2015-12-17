Import-Module WindowsServiceDeployment

Start-WinSvc -ServiceName $ServiceName -Timeout '00:06:00'
