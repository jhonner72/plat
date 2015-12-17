$tomcatdir = [environment]::GetEnvironmentVariable("CATALINA_HOME","Machine")
$confDir = Join-Path $tomcatdir conf
$currentPath= Split-Path $script:MyInvocation.MyCommand.Path
$contextXml = "context.xml"
$serverXml = "server.xml"
$serverXmlSSL = "serverSSL.xml"

Write-Host "Copying $contextXml to $confDir"
Copy-Item $contextXml -Destination $confDir

If ($SSL -eq $True)
{
	Write-Host "SSL flag set to true. Copying $serverXmlSSL to $confDir\server.xml"
	Copy-Item $serverXmlSSL -Destination "$confDir\server.xml"
} 
Else
{
	Write-Host "Copying $serverXml to $confDir"
	Copy-Item $serverXml -Destination $confDir
}
