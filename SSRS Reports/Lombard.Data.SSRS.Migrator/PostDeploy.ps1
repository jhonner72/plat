#requires -version 3

$migrator = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Data.SSRS.Migrator.exe'
Try {
	&$migrator '-nDsConnectionString' '-pTrackingProfile' | Write-Host
	&$migrator '-nDCDsConnectionString' '-pDocumentumProfile' | Write-Host
}
Catch {
	Write-Error "Problem when running the migrator, check!"
	Exit 1
}
