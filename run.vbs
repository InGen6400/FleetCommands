
Dim oShell

Set oShell = WScript.CreateObject ("WSCript.shell")

oShell.run "bat\run.bat",0

Set oShell = Nothing