@echo off
cd src
javac -encoding utf-8 -Xlint:unchecked -cp .;..\librarys -d ..\out fc/Main.java
cp fc\ui\*.fxml ..\out\fc\ui