#requires -version 3

$migrator = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.NABD2UserLoad.Migrator.exe'
Try {
	&$migrator '-ndefaultConnection' | Write-Host
}
Catch {
	Write-Error "Problem when running the migrator, check!"
	Exit 1
}
