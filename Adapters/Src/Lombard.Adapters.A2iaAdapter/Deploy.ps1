#requires -version 3

Import-Module CredentialDeployment

$ServiceAccount = $OctopusParameters['topshelf:ServiceUserAccount']
$ServicePassword = $OctopusParameters['topshelf:ServiceUserPassword']

Try {
	# Install using topshelf
	$fsp = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'Lombard.Adapters.A2iaAdapter.exe'
	&$fsp install -username:$ServiceAccount -password:$ServicePassword --autostart | Write-Host
} 
Catch {
	Write-Error 'Problem installing Windows Service'
	Exit 1
}