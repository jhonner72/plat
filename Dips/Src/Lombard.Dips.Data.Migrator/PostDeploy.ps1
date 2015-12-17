#requires -version 3

$migrator = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Dips.Data.Migrator.exe'
Try {
	&$migrator | Write-Host
}
Catch {
	Write-Error "Problem when running the migrator, check!"
	Exit 1
}
