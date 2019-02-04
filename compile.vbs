
Dim oShell

Set oShell = WScript.CreateObject ("WSCript.shell")

oShell.run "bat\compile.bat",1

Set oShell = Nothing