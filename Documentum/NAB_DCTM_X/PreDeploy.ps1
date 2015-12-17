Import-Module Documentum

$currentPath = Split-Path $script:MyInvocation.MyCommand.Path
$path = Join-Path $currentPath "configuration\PreDeploy"
$files = Get-ChildItem -Path $path -File -ErrorAction SilentlyContinue

If($files -eq $null)
{
	Write-Host "There are no pre-deployment configuration scripts to run."
}
Else
{
	Foreach($file in $files)
	{
		$filePath = $file.FullName
	
		Write-Host "Executing $file"
		If($file.Extension -eq ".sql")
		{
			& SQLCMD -S "$DatabaseServer" -d $DatabaseName -U $DatabaseUser -P $DatabasePassword /i $filePath
		}
	
		ElseIf($file.Extension -eq ".dql")
		{
			Execute-Dql -DocBaseName $Docbase -Username $DctmUsername -Password $DctmPassword -DqlPath $filePath
		}
		
		Else
		{
			Write-Error $file.Extension ": unrecognised file extension. File: $file"
		}
	}
}
