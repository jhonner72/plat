#requires -version 3

Try {
	$tfp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Adapters.MftAdapter.exe'
	&$tfp start | Write-Host
}
Catch {
	Write-Error 'Problem when starting the service'
	Exit 1
}