$tomcatdir = [environment]::GetEnvironmentVariable("CATALINA_HOME","Machine")
$confDir = Join-Path $tomcatdir conf
$currentPath= Split-Path $script:MyInvocation.MyCommand.Path
$contextXml = "context.xml"
$serverXml = "server.xml"
$serverXmlSSL = "serverSSL.xml"
$bpmXml = "bpm-platform.xml"
$dfc = "dfc.properties"

Write-Host "Copying $bpmXml to $confDir"
Copy-Item $bpmXml $confDir -Force

Write-Host "Copying $contextXml to $confDir"
Copy-Item $contextXml $confDir -Force

Write-Host "Copying $dfc to $confDir"
Copy-Item $dfc $confDir -Force

If ($SSL -eq $True)
{
	Write-Host "SSL flag set to true. Copying $serverXmlSSL to $confDir\server.xml"
	Copy-Item $serverXmlSSL "$confDir\server.xml" -Force
} 
Else
{
	Write-Host "Copying $serverXml to $confDir"
	Copy-Item $serverXml $confDir -Force
}
